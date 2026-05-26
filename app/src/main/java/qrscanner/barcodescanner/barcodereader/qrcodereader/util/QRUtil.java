package qrscanner.barcodescanner.barcodereader.qrcodereader.util;

import androidx.annotation.Nullable;

import com.drojian.qrcode.baselib.CodeFormat;
import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat;
import com.drojian.qrcode.scanresultlib.ResultHandlerFactory;
import com.drojian.qrcode.zxinglib.ZXingFormatUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.result.ParsedResultType;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;

//二维码/条形码工具类  ->提供关于类型识别，图标获取，名称解析以及格式判断等通用功能
public class QRUtil {

    //根据解析格式和扫描结果获取对应的图标资源ID
    public static int getResultIcon(ParsedFormat type, ScanResultModel result) {
        if (null == type || null == result) {
            // 默认返回商品条码图标
            return R.drawable.vector_ic_result_product;
        }
        switch (type) {
            case CALENDAR:
                return R.drawable.vector_ic_result_calendar;
            default:
                // 如果是条形码格式（一维码），返回商品图标
                if (ResultHandlerFactory.isBarCodeFormat(result)) {
                    return R.drawable.vector_ic_result_product;
                } else {
                    // 否则进一步细分解析（如社交媒体图标）
                    return getSecondIcon(ParsedFormat.TEXT, result);
                }
        }

    }

    //获取扩展类型图标(识别特定的域名或协议,如YouTube,日历等)
    private static int getSecondIcon(ParsedFormat type, ScanResultModel result) {
        if (result.getText().toLowerCase().contains("facebook.com") || result.getText().toLowerCase().contains("fb://profile/")) {
            return R.drawable.ic_creat_facebook;
        }  else if (result.getText().toLowerCase().contains("youtube.com") || result.getText().toLowerCase().contains("youtu.be")) {
            return R.drawable.vector_ic_youtube;
        }  else {
            // 默认的文本或链接图标
            if (type == ParsedFormat.TEXT) {
                return R.drawable.vector_ic_result_text;
            } else {
                return R.drawable.vector_ic_result_uri;
            }
        }
    }

    //获取二维码种类的名称资源ID
    public static int getResultName(ParsedFormat type, ScanResultModel result) {
        if (null == type || null == result) {
            return R.string.result_bar_code;
        }
        switch (type) {
            case URI:
                // 处理特定域名的显示名称
                return getSecondName(ParsedResultType.URI, result);
            case CALENDAR:
                return R.string.result_calendar;
            default:
                if (ResultHandlerFactory.isBarCodeFormat(result)) {
                    return R.string.result_bar_code;
                } else {
                    return getSecondName(ParsedResultType.TEXT, result);
                }
        }
    }
    //获取扩展类型名称资源ID
    private static int getSecondName(ParsedResultType type, ScanResultModel result) {
        if (result.getText().toLowerCase().contains("facebook.com") || result.getText().toLowerCase().contains("fb://profile/")) {
            return R.string.facebook;
        }  else if (result.getText().toLowerCase().contains("youtube.com") || result.getText().toLowerCase().contains("youtu.be")) {
            return R.string.youtube;
        } else {
            if (type == ParsedResultType.TEXT) {
                return R.string.result_text;
            } else {
                return R.string.result_uri;
            }
        }
    }

    //获取分类文本字符串获取对应的枚举类型
    public static CreateType getCategoryByCategoryText(String category) {
        if (category.equalsIgnoreCase("Calendar")) {
            return CreateType.CALENDAR;
        } else if (category.equalsIgnoreCase("Youtube")) {
            return CreateType.YOUTUBE;
        }
        return CreateType.YOUTUBE; // 默认返回 YouTube
    }
    //根据分类文本字符串获取对应的图标
    public static int getIconByCategoryText(String categoty) {
        if (categoty.equalsIgnoreCase("Calendar")) {
            return R.drawable.vector_ic_result_calendar;
        } else if (categoty.equalsIgnoreCase("Youtube")) {
            return R.drawable.vector_ic_youtube;
        }
        return R.drawable.vector_ic_result_text;
    }

    //判断扫描结果是否为"广义"的二维码(包括QR,Data Matrix,Aztec等二维码格式)
    public static boolean isQRCode(ScanResultModel result) {
        BarcodeFormat barcodeFormat = ZXingFormatUtil.conversion(result.getCodeFormat());
        if (null == barcodeFormat) {
            return false;
        }
        switch (barcodeFormat) {
            case QR_CODE:
            case DATA_MATRIX:
            case AZTEC:
            case PDF_417:
            case MAXICODE:
                return true;
        }
        return false;
    }

    //提取URL的主域名部分，用于精简显示  ->例如将https://www.google.com/serach?q=... 缩短为https://www.google.com
    public static String getDomainName(@Nullable String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        // 查找第三个斜杠出现的位置（通常在协议头后面）
        int index = url.indexOf('/', 8);
        if (index != -1) {
            url = url.substring(0, index);
        }
        // 限制最大显示长度为 40 字符
        if (url.length() > 40) {
            return url.substring(0, 40);
        } else {
            return url;
        }
    }

    //粗略逻辑:根据输入数字的长度，猜测师徒生成的条形码类型  ->@param context 输入内容 @retuen 对应的BarCodeFormat格式
    public static BarcodeFormat getBarcodeType(String content) {
        int lengthInput = content.length();
        switch (lengthInput) {
            case 6:
                return BarcodeFormat.UPC_E;
            case 8:
                return BarcodeFormat.EAN_8;
            case 12:
                return BarcodeFormat.UPC_A;
            case 13:
                return BarcodeFormat.EAN_13;
            default:
                // 默认使用 CODE_128，它支持变长且包含多种字符
                return BarcodeFormat.CODE_128;
        }
    }
    //判断是否为正方形/矩形的二维条码(而非窄长的条形码)
    public static boolean isSquareBarcode(CodeFormat codeFormat) {
        if (codeFormat == null) {
            return false;
        }
        switch (codeFormat) {
            case AZTEC:
            case DATA_MATRIX:
            case MAXICODE:
            case QR_CODE:
                return true;
        }
        return false;
    }
}
