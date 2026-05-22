/*
 * Copyright (C) 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qrscanner.barcodescanner.barcodereader.qrcodereader.data.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanresultlib.BaseResultHandler;
import com.drojian.qrcode.utillib.log.Logcat;
import com.drojian.qrcode.zxinglib.ZXingFormatUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



/**
 * 扫描历史记录管理器
 * 负责扫描记录的查询、删除、单项获取等数据库操作逻辑
 */
public final class HistoryManager {

    private static final String TAG = HistoryManager.class.getSimpleName();

    private static final int MAX_ITEMS = 2000; // 数据库最大存储限制（暂未强制执行）

    // 查询时用到的列集合
    private static final String[] COLUMNS = {
            DBHelper.TEXT_COL,
            DBHelper.DISPLAY_COL,
            DBHelper.FORMAT_COL,
            DBHelper.TIMESTAMP_COL,
            DBHelper.DETAILS_COL,
    };

    private final Activity activity;

    public HistoryManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取所有的扫描历史记录，并按时间倒序排列（最新的在最前面）
     */
    public static List<HistoryItem> buildHistoryItems(@Nullable Context context) {
        if (context == null) {
            Logcat.e("buildHistoryItems return an emptyList, because the parameter context is null!!");
            return new ArrayList<>();
        }
        SQLiteOpenHelper helper = new DBHelper(context);
        List<HistoryItem> items = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            // 查询所有记录，按时间戳降序排列
            cursor = db.query(DBHelper.TABLE_NAME, COLUMNS, null, null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
            while (cursor.moveToNext()) {
                String text = cursor.getString(0);
                String display = cursor.getString(1);
                String format = cursor.getString(2);
                long timestamp = cursor.getLong(3);
                String details = cursor.getString(4);
                // 将数据库数据重新封装为识别结果对象和列表项
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
     * 根据索引列表批量删除历史记录
     * 注意：此方法依赖于当前查询的排序顺序来确定 ID
     */
    public void deleteHistoryItemByIndexList(List<Integer> indexList) {
        if (indexList == null || indexList.isEmpty()) {
            return;
        }
        SQLiteOpenHelper helper = new DBHelper(activity);
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            // 再次通过同样的排序查询出所有 ID
            cursor = db.query(DBHelper.TABLE_NAME,
                    new String[]{DBHelper.ID_COL},
                    null, null, null, null,
                    DBHelper.TIMESTAMP_COL + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            for (int index : indexList) {
                if (cursor.moveToPosition(index)) {
                    ids.add(cursor.getString(0));
                }
            }
            // 循环删除匹配的 ID
            for (String id : ids) {
                db.delete(DBHelper.TABLE_NAME, DBHelper.ID_COL + '=' + id, null);
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
     * 删除内容重复的历史记录（用于新增前去重）
     */
    private void deletePrevious(String text) {
        SQLiteOpenHelper helper = new DBHelper(activity);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.delete(DBHelper.TABLE_NAME, DBHelper.TEXT_COL + "=?", new String[]{text});
        } catch (SQLException sqle) {
            Log.w(TAG, sqle);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
