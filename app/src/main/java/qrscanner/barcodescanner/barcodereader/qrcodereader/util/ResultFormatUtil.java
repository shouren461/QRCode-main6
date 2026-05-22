package qrscanner.barcodescanner.barcodereader.qrcodereader.util;

import android.text.TextUtils;

import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat;
import com.google.zxing.Result;



/**
 * 扫描结果格式化工具类
 * 负责从原始的二维码/条形码文本中提取最适合展示给用户看的内容（如日历标题、YouTube 搜索词等）
 */
public class ResultFormatUtil {

    /**
     * 针对“扫描”结果：从二维码详情里提取精简的展示信息
     * @param result 原始识别结果
     * @param handler 解析后的格式处理器类型
     */
    public static String extractScanDisplayText(Result result, ParsedFormat handler) {
        if (handler == null || result == null) {
            return "";
        }
        switch (handler) {
            case CALENDAR:
                // 如果是日历类型，提取事件标题
                return extractCalendar(result.getText());
            case YOUTUBE:
                // 如果是 YouTube，提取关键搜索词或 ID
                return extractYoutube(result.getText());
            default:
        }
        // 默认直接返回原始文本
        return result.getText();
    }
    /**
     * 针对“创建”结果：从生成的二维码详情里提取展示信息
     */
    public static String extractCreateDisplayText(Result result) {
        if (result == null) {
            return "";
        }
        // 注意：此处 switch 使用的是 BarcodeFormat 的自定义扩展逻辑
        switch (result.getBarcodeFormat()) {
            case Calendar:
                return extractCalendar(result.getText());
            case Youtube:
                return extractYoutube(result.getText());
            default:
        }
        return result.getText();
    }
    /**
      解析 iCalendar 协议文本，尝试提取“标题”或“描述”,逻辑：优先查找 SUMMARY 字段，若无则找 DESCRIPTION，最后保底提取开始日期
     */
    private static String extractCalendar(String str) {
        try {
            // 尝试提取标题 (SUMMARY)
            String[] s1 = str.split("DTSTART");
            if (s1.length == 2) {
                // 在 DTSTART 之前的内容中查找 SUMMARY: 之后的内容
                if (!TextUtils.isEmpty(s1[0].split("SUMMARY:")[1].trim())) {
                    return s1[0].split("SUMMARY:")[1].trim();
                }
            }
            
            // 尝试提取描述 (DESCRIPTION)
            s1 = str.split("DESCRIPTION:");
            if (s1.length > 1) {
                // 截取 DESCRIPTION 到 END 之间的内容
                if (!TextUtils.isEmpty(s1[1].split("END")[0])) {
                    if (!TextUtils.isEmpty(s1[1].split("END")[0].trim())) {
                        String s = s1[1].split("END")[0];
                        return s1[1].split("END")[0].trim();
                    }
                }
            }
            
            // 保底逻辑：如果以上都失败，提取开始日期 (DTSTART)
            s1 = str.split("DTSTART:");
            if (s1.length > 1) {
                // 仅提取日期部分（T 之前）
                return s1[1].split("T")[0];
            } else {
                // 适配带参数的日期格式
                s1 = str.split("DTSTART;VALUE=DATE:");
                if (s1.length > 1) {
                    return s1[1].split("DTEND;VALUE=DATE:")[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 无法解析则返回原始字符串
        return str;
    }

    /**
     * 针对 YouTube 链接进行精简提取
     * 逻辑：去除协议头和前缀，仅保留搜索关键词、频道 ID 或视频 ID
     */
    private static String extractYoutube(String str) {
        // 匹配并截取搜索关键词
        if (str.startsWith("vnd.youtube://youtube.com/results?search_query=")) {
            return str.substring("vnd.youtube://youtube.com/results?search_query=".length());
        }
        // 匹配并截取频道 ID
        if (str.startsWith("https://www.youtube.com/channel/")) {
            return str.substring("https://www.youtube.com/channel/".length());
        }
        // 匹配并截取视频 ID
        if (str.startsWith("https://www.youtube.com/watch?v=")) {
            return str.substring("https://www.youtube.com/watch?v=".length());
        }
        return str;
    }
    
}
