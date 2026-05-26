package qrscanner.barcodescanner.barcodereader.qrcodereader.data.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.drojian.qrcode.utillib.log.Logcat;
import com.drojian.qrcode.utillib.utils.SPUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.util.ResultFormatUtil;

//创建历史数据管理器 -> 负责用户生成的二维码记录的增删改查
public class CreateHistoryManager {
    private static final String TAG = CreateHistoryManager.class.getSimpleName();

    // 查询所需的字段集合
    private static final String[] COLUMNS = {
            CreateHistoryDBHelper.TEXT_COL,
            CreateHistoryDBHelper.DISPLAY_COL,
            CreateHistoryDBHelper.FORMAT_COL,
            CreateHistoryDBHelper.TIMESTAMP_COL,
            CreateHistoryDBHelper.DETAILS_COL,
    };

    private final Activity activity;

    public CreateHistoryManager(Activity activity) {
        this.activity = activity;
    }

    //获取所有的创建历史记录，按时间倒序排序
    public List<HistoryItem> buildHistoryItems() {
        SQLiteOpenHelper helper = new CreateHistoryDBHelper(activity);
        List<HistoryItem> items = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(CreateHistoryDBHelper.TABLE_NAME,
                    COLUMNS,
                    null, null, null, null,
                    CreateHistoryDBHelper.TIMESTAMP_COL + " DESC");
            while (cursor.moveToNext()) {
                String text = cursor.getString(0);
                String display = cursor.getString(1);
                String createType = cursor.getString(2); // 现在存储的是创建类型（如 Youtube）
                long timestamp = cursor.getLong(3);
                String details = cursor.getString(4);
                // 封装成统一的历史项对象
                // 创建类型单独存储，Result 使用固定的 QR_CODE 格式
                Result result = new Result(text, null, null, BarcodeFormat.QR_CODE, timestamp);
                items.add(new HistoryItem(result, display, details, createType));
            }
            Logcat.d(TAG, "查询到 " + items.size() + " 条创建历史记录");
        } catch (CursorIndexOutOfBoundsException cioobe) {
            Log.w(TAG, cioobe);
        } catch (Exception e) {
            Log.e(TAG, "查询历史记录失败: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return items;
    }

    //批量删除选中的创建历史记录
    public void deleteHistoryItemByIndexList(List<Integer> indexList) {
        if (indexList == null || indexList.isEmpty()) {
            return;
        }
        SQLiteOpenHelper helper = new CreateHistoryDBHelper(activity);
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            // 先通过排序查出 ID 序列
            cursor = db.query(CreateHistoryDBHelper.TABLE_NAME,
                    new String[]{CreateHistoryDBHelper.ID_COL},
                    null, null, null, null,
                    CreateHistoryDBHelper.TIMESTAMP_COL + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            for (int index : indexList) {
                if (cursor.moveToPosition(index)) {
                    ids.add(cursor.getString(0));
                }
            }
            // 执行物理删除
            for (String id : ids) {
                db.delete(CreateHistoryDBHelper.TABLE_NAME, CreateHistoryDBHelper.ID_COL + '=' + id, null);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    //新增一条二维码创建记录  ->逻辑:先删除内内容重复的旧记录(去重)，再插入新纪录
    // 参数说明：result=二维码解析结果, display=展示文本, format=格式名称(用于兼容旧调用)
    public void addCreateHistoryItem(Result result, String display, String format) {
        // 使用 SharedPreferences 全局设置，而不是依赖 Intent 参数
        boolean saveHistoryEnabled = SPUtil.getInstance().get("SAVE_HISTORY", true);
        
        if (!saveHistoryEnabled) {
            return;
        }

        // 添加 null 检查
        if (result == null) {
            return;
        }
        
        if (result.getText() == null) {
            return;
        }

        try {
            //1,先删除相同内容的旧记录，避免重复
            deletePrevious(result.getText());
            
            //2,创建存储数据的容器
            ContentValues values = new ContentValues();
            //3,存储二维码的原始文本内容
            values.put(CreateHistoryDBHelper.TEXT_COL, result.getText());
            //4,存储二维码的格式类型(优先使用format参数，兼容旧调用)
            String barcodeFormat = format != null ? format : 
                (result.getBarcodeFormat() != null ? result.getBarcodeFormat().toString() : "QR_CODE");
            values.put(CreateHistoryDBHelper.FORMAT_COL, barcodeFormat);
            //5,使用工具类处理，提取适合列表展示的文本
            String s = ResultFormatUtil.extractCreateDisplayText(result);
            values.put(CreateHistoryDBHelper.DISPLAY_COL, s);
            //6,存储时间戳，用于排序显示
            values.put(CreateHistoryDBHelper.TIMESTAMP_COL, System.currentTimeMillis());
            
            //7,获取数据库帮助类实例
            CreateHistoryDBHelper helper = new CreateHistoryDBHelper(activity);
            SQLiteDatabase db = null;
            try {
                //获取可写数据库
                db = helper.getWritableDatabase();
                //8,执行插入操作
                db.insert(CreateHistoryDBHelper.TABLE_NAME, 
                    CreateHistoryDBHelper.TIMESTAMP_COL, values);
            } catch (SQLException sqle) {
                Log.e(TAG, "数据库插入失败: " + sqle.getMessage(), sqle);
            } finally {
                //最后关闭数据库，释放资源
                if (db != null) {
                    db.close();
                }
            }
        } catch (Exception e) {
            // 捕获所有可能的异常，避免静默失败
            Log.e(TAG, "保存创建历史失败: " + e.getMessage(), e);
        }
    }

    // 兼容旧版本的两参数方法
    public void addCreateHistoryItem(Result result, String display) {
        addCreateHistoryItem(result, display, null);
    }


    //根据文本内容删除重复的创建记录
    private void deletePrevious(String text) {
        SQLiteOpenHelper helper = new CreateHistoryDBHelper(activity);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            int deletedCount = db.delete(CreateHistoryDBHelper.TABLE_NAME, CreateHistoryDBHelper.TEXT_COL + "=?", new String[]{text});
            Logcat.d(TAG, "删除了 " + deletedCount + " 条相同内容的记录");
        } catch (SQLException sqle) {
            Log.w(TAG, sqle);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}