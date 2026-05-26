package qrscanner.barcodescanner.barcodereader.qrcodereader.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//创建历史页面的的数据库辅助类 -> 专门用于存储用户在应用内生成的二维码记录
final class CreateHistoryDBHelper extends SQLiteOpenHelper {
    static final String TABLE_NAME = "created";    //表名:创建记录
    static final String ID_COL = "id";             //自增ID
    static final  String TEXT_COL = "text";       //生成二维码的原始内容
    static final String FORMAT_COL = "format";         //二维码类型
    static  final String DISPLAY_COL = "display";    //界面显示文本
    static final String TIMESTAMP_COL = "timestamp"; //生成时间
    static final String DETAILS_COL = "details";     //详情数据
    private static final  int DB_VERSION = 2;      // 升级版本号以修复错误的表结构
    private static final String DB_NAME = "barcode_create_history.db";//创建历史专属数据库文件


    public CreateHistoryDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    //创建表结构
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID_COL + " INTEGER PRIMARY KEY, " +
                        TEXT_COL + " TEXT, " +
                        FORMAT_COL + " TEXT, " +
                        DISPLAY_COL + " TEXT, " +
                        TIMESTAMP_COL + " INTEGER, " +
                        DETAILS_COL + " TEXT);"
        );
    }

    //版本升级逻辑
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int olderVersion, int newVersion) {
        //删除旧数据库并创建新数据库
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
