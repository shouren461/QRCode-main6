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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.util.ResultFormatUtil;

/**
 * 创建历史数据管理器
 * 负责用户生成的二维码记录的增、删、改、查
 */
public class CreateHistoryManager {
    private static final String TAG = HistoryManager.class.getSimpleName();

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

    /**
     * 获取所有创建历史记录，按时间倒序排列
     */
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
                String format = cursor.getString(2);
                long timestamp = cursor.getLong(3);
                String details = cursor.getString(4);
                // 封装成统一的历史项对象
                Result result = new Result(text, null, null, BarcodeFormat.valueOf(format), timestamp);
                items.add(new HistoryItem(result, display, details));
            }
        } catch (CursorIndexOutOfBoundsException cioobe) {
            Log.w(TAG, cioobe);
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

    /**
     * 批量删除选中的创建历史记录
     */
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

    /**
     * 新增一条二维码创建记录
     * 逻辑：先删除内容重复的旧记录（去重），再插入新记录
     */
    public void addCreateHistoryItem(Result result, String display) {
        Logcat.d("addHistoryItem " + result);
        // 如果配置中关闭了“保存历史”，则不执行插入
        if (!activity.getIntent().getBooleanExtra("SAVE_HISTORY", true)) {
            return;
        }

        // 先去重
        deletePrevious(result.getText());

        ContentValues values = new ContentValues();
        values.put(CreateHistoryDBHelper.TEXT_COL, result.getText());
        values.put(CreateHistoryDBHelper.FORMAT_COL, result.getBarcodeFormat().toString());

        // 使用工具类提取或解析适合在列表展示的文本
        String s = ResultFormatUtil.extractCreateDisplayText(result);
        values.put(CreateHistoryDBHelper.DISPLAY_COL, s);

        values.put(CreateHistoryDBHelper.TIMESTAMP_COL, System.currentTimeMillis());

        SQLiteOpenHelper helper = new CreateHistoryDBHelper(activity);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            // 执行插入操作
            db.insert(CreateHistoryDBHelper.TABLE_NAME, CreateHistoryDBHelper.TIMESTAMP_COL, values);
        } catch (SQLException sqle) {
            Log.w(TAG, sqle);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据文本内容删除重复的创建记录
     */
    private void deletePrevious(String text) {
        SQLiteOpenHelper helper = new CreateHistoryDBHelper(activity);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.delete(CreateHistoryDBHelper.TABLE_NAME, CreateHistoryDBHelper.TEXT_COL + "=?", new String[]{text});
        } catch (SQLException sqle) {
            Log.w(TAG, sqle);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
