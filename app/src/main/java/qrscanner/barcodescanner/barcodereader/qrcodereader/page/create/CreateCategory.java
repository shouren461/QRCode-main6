package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create;

import com.drojian.qrcode.createlib.create.CreateFormat;

import java.util.ArrayList;
import java.util.List;

import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;


//创建二维码的分类管理 ->负责维护可创建的的类型列表，以及类型之间的转换逻辑
public class CreateCategory {

    public static final String PREF = "PREF_QRCODE_SAVE_TIME_";

    //根据当前应用支持创建的所有二维码类型列表
    public static List<CreateType> typeList = new ArrayList<>();

    static {
        //初始化支持的类型：目前支持 YouTube 链接和日历事件
        typeList.add(CreateType.YOUTUBE);
        typeList.add(CreateType.CALENDAR);
    }

    //根据创建类型获取对应的格式名称字符串
    public static String getBarcodeFormatByCategory(CreateType type) {
        switch (type) {
            case CALENDAR:
                return "Calendar";
            case YOUTUBE:
                return "Youtube";
        }
        return "";
    }

    //将本项目定义的CreateType转化为底层库需要的CreateForamt格式
    public static CreateFormat transCreateWithType(CreateType type) {
        switch (type) {
            case CALENDAR:
                return CreateFormat.Calendar;
            case YOUTUBE:
                return CreateFormat.Youtube;
        }
        return CreateFormat.Text;
    }
}
