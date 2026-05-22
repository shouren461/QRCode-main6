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

package com.google.zxing;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * <p>Encapsulates the result of decoding a barcode within an image.</p>
 *
 * @author Sean Owen
 */
public final class Result {

    private final String text;
    private final byte[] rawBytes;
    private final int numBits;
    private ResultPoint[] resultPoints;
    private BarcodeFormat format;
    private Map<ResultMetadataType, Object> resultMetadata;
    private final long timestamp;
    private String resultDetail;

    public Result(String text,
                  byte[] rawBytes,
                  ResultPoint[] resultPoints,
                  BarcodeFormat format) {
        this(text, rawBytes, resultPoints, format, System.currentTimeMillis());
    }

    public Result(String text,
                  byte[] rawBytes,
                  ResultPoint[] resultPoints,
                  BarcodeFormat format,
                  long timestamp) {
        this(text, rawBytes, rawBytes == null ? 0 : 8 * rawBytes.length,
                resultPoints, format, timestamp);
    }

    public Result(String text,
                  byte[] rawBytes,
                  int numBits,
                  ResultPoint[] resultPoints,
                  BarcodeFormat format,
                  long timestamp) {
        this.text = text;
        this.rawBytes = rawBytes;
        this.numBits = numBits;
        this.resultPoints = resultPoints;
        this.format = format;
        this.resultMetadata = null;
        this.timestamp = timestamp;
    }

    public void setFormat(BarcodeFormat format) {
        this.format = format;
    }

    /**
     * @return raw text encoded by the barcode
     */
    public String getText() {
        return text;
    }

    /**
     * @return raw bytes encoded by the barcode, if applicable, otherwise {@code null}
     */
    public byte[] getRawBytes() {
        return rawBytes;
    }

    /**
     * @return how many bits of {@link #getRawBytes()} are valid; typically 8 times its length
     * @since 3.3.0
     */
    public int getNumBits() {
        return numBits;
    }

    /**
     * @return points related to the barcode in the image. These are typically points
     * identifying finder patterns or the corners of the barcode. The exact meaning is
     * specific to the type of barcode that was decoded.
     */
    public ResultPoint[] getResultPoints() {
        return resultPoints;
    }

    /**
     * @return {@link BarcodeFormat} representing the format of the barcode that was decoded
     */
    public BarcodeFormat getBarcodeFormat() {
        return format;
    }

    /**
     * @return {@link Map} mapping {@link ResultMetadataType} keys to values. May be
     * {@code null}. This contains optional metadata about what was detected about the barcode,
     * like orientation.
     */
    public Map<ResultMetadataType, Object> getResultMetadata() {
        return resultMetadata;
    }

    public void putMetadata(ResultMetadataType type, Object value) {
        if (resultMetadata == null) {
            resultMetadata = new EnumMap<>(ResultMetadataType.class);
        }
        resultMetadata.put(type, value);
    }

    public void putAllMetadata(Map<ResultMetadataType, Object> metadata) {
        if (metadata != null) {
            if (resultMetadata == null) {
                resultMetadata = metadata;
            } else {
                resultMetadata.putAll(metadata);
            }
        }
    }

    public void addResultPoints(ResultPoint[] newPoints) {
        ResultPoint[] oldPoints = resultPoints;
        if (oldPoints == null) {
            resultPoints = newPoints;
        } else if (newPoints != null && newPoints.length > 0) {
            ResultPoint[] allPoints = new ResultPoint[oldPoints.length + newPoints.length];
            System.arraycopy(oldPoints, 0, allPoints, 0, oldPoints.length);
            System.arraycopy(newPoints, 0, allPoints, oldPoints.length, newPoints.length);
            resultPoints = allPoints;
        }
    }

    private static final String JSON_NAME_TEXT = "j_t";
    private static final String JSON_NAME_BARCODE_FORMAT = "j_bf";
    private static final String JSON_NAME_TIMESTAMP = "j_ts";
    private static final String JSON_NAME_RESULT_DETAIL = "j_rd";

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_NAME_TEXT, text);
            jsonObject.put(JSON_NAME_BARCODE_FORMAT, format.name());
            jsonObject.put(JSON_NAME_TIMESTAMP, timestamp);
            if (resultDetail != null) {
                jsonObject.put(JSON_NAME_RESULT_DETAIL, resultDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Result fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String text = jsonObject.getString(JSON_NAME_TEXT);
            BarcodeFormat barcodeFormat = BarcodeFormat.valueOf(jsonObject.getString(JSON_NAME_BARCODE_FORMAT));
            long timestamp = jsonObject.getLong(JSON_NAME_TIMESTAMP);

            Result result = new Result(text, null, null, barcodeFormat, timestamp);
            if (jsonObject.has(JSON_NAME_RESULT_DETAIL)) {
                result.setResultDetail(jsonObject.getString(JSON_NAME_RESULT_DETAIL));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public String getResultDetail() {
        return resultDetail;
    }

    public void setResultDetail(String resultDetail) {
        this.resultDetail = resultDetail;
    }

    @Override
    public String toString() {
        return "Result{" +
                "text='" + text + '\'' +
                ", rawBytes=" + Arrays.toString(rawBytes) +
                ", numBits=" + numBits +
                ", resultPoints=" + Arrays.toString(resultPoints) +
                ", format=" + format +
                ", resultMetadata=" + resultMetadata +
                ", timestamp=" + timestamp +
                '}';
    }
}
