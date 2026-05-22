package com.drojian.qrcode.cameralib.decode;


import com.drojian.qrcode.baselib.CodeFormat;

import java.util.EnumSet;
import java.util.Set;

final class DecodeFormatManager {

    public static final Set<CodeFormat> PRODUCT_FORMATS;
    public static final Set<CodeFormat> INDUSTRIAL_FORMATS;
    public static final Set<CodeFormat> ONE_D_FORMATS;
    public static final Set<CodeFormat> QR_CODE_FORMATS = EnumSet.of(CodeFormat.QR_CODE);
    public static final Set<CodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(CodeFormat.DATA_MATRIX);
    public static final Set<CodeFormat> AZTEC_FORMATS = EnumSet.of(CodeFormat.AZTEC);
    public static final Set<CodeFormat> PDF417_FORMATS = EnumSet.of(CodeFormat.PDF_417);

    static {
        PRODUCT_FORMATS = EnumSet.of(CodeFormat.UPC_A,
                CodeFormat.UPC_E,
                CodeFormat.EAN_13,
                CodeFormat.EAN_8,
                CodeFormat.RSS_14,
                CodeFormat.RSS_EXPANDED);
        INDUSTRIAL_FORMATS = EnumSet.of(CodeFormat.CODE_39,
                CodeFormat.CODE_93,
                CodeFormat.CODE_128,
                CodeFormat.ITF,
                CodeFormat.CODABAR);
        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);
    }
}