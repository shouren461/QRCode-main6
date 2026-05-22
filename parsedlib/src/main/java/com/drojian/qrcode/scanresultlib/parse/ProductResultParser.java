/*
 * Copyright 2007 ZXing authors
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

package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.CodeFormat;
import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseProductModel;


/**
 * Parses strings of digits that represent a UPC code.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ProductResultParser extends ResultParser {

    // Treat all UPC and EAN variants as UPCs, in the sense that they are all product barcodes.
    @Override
    public ParseProductModel parse(ScanResultModel result) {
        CodeFormat format = result.getCodeFormat();
        if (!(format == CodeFormat.UPC_A || format == CodeFormat.UPC_E ||
                format == CodeFormat.EAN_8 || format == CodeFormat.EAN_13)) {
            return null;
        }
        String rawText = getMassagedText(result);
        if (!isStringOfDigits(rawText, rawText.length())) {
            return null;
        }
        // Not actually checking the checksum again here

        String normalizedProductID;
        // Expand UPC-E for purposes of searching
        if (format == CodeFormat.UPC_E && rawText.length() == 8) {
            normalizedProductID = convertUPCEtoUPCA(rawText);
        } else {
            normalizedProductID = rawText;
        }

        return new ParseProductModel(rawText, normalizedProductID);
    }

    /**
     * Expands a UPC-E value back into its full, equivalent UPC-A code value.
     *
     * @param upce UPC-E code as string of digits
     * @return equivalent UPC-A code as string of digits
     */
    public static String convertUPCEtoUPCA(String upce) {
        char[] upceChars = new char[6];
        upce.getChars(1, 7, upceChars, 0);
        StringBuilder result = new StringBuilder(12);
        result.append(upce.charAt(0));
        char lastChar = upceChars[5];
        switch (lastChar) {
            case '0':
            case '1':
            case '2':
                result.append(upceChars, 0, 2);
                result.append(lastChar);
                result.append("0000");
                result.append(upceChars, 2, 3);
                break;
            case '3':
                result.append(upceChars, 0, 3);
                result.append("00000");
                result.append(upceChars, 3, 2);
                break;
            case '4':
                result.append(upceChars, 0, 4);
                result.append("00000");
                result.append(upceChars[4]);
                break;
            default:
                result.append(upceChars, 0, 5);
                result.append("0000");
                result.append(lastChar);
                break;
        }
        // Only append check digit in conversion if supplied
        if (upce.length() >= 8) {
            result.append(upce.charAt(7));
        }
        return result.toString();
    }

}