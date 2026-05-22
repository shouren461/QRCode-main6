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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 扫描历史记录的数据库辅助类
 * 负责创建和升级存储扫描结果的本地数据库
 */
final class DBHelper extends SQLiteOpenHelper {

  private static final int DB_VERSION = 5; // 数据库版本号
  private static final String DB_NAME = "barcode_scanner_history.db"; // 数据库文件名
  
  // --- 表及字段常量定义 ---
  static final String TABLE_NAME = "history";     // 表名：扫描历史
  static final String ID_COL = "id";              // 自增主键 ID
  static final String TEXT_COL = "text";          // 原始扫描文本内容
  static final String FORMAT_COL = "format";      // 扫码格式（如 QR_CODE, EAN_13）
  static final String DISPLAY_COL = "display";    // 用于界面显示的精简内容
  static final String TIMESTAMP_COL = "timestamp"; // 扫描时间戳
  static final String DETAILS_COL = "details";    // 详细解析数据（JSON 格式或其他）

  DBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  /**
   * 首次安装或数据库不存在时创建表结构
   */
  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
            ID_COL + " INTEGER PRIMARY KEY, " +
            TEXT_COL + " TEXT, " +
            FORMAT_COL + " TEXT, " +
            DISPLAY_COL + " TEXT, " +
            TIMESTAMP_COL + " INTEGER, " +
            DETAILS_COL + " TEXT);");
  }

  /**
   * 数据库版本升级时的逻辑（此处简单采用删除旧表重新创建）
   */
  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(sqLiteDatabase);
  }

}
