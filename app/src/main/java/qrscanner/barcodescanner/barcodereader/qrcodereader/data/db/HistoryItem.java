/*
 * Copyright 2012 ZXing authors
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

import com.google.zxing.Result;

/**
 * 历史记录数据模型
 * 封装了一次扫描或创建二维码的结果信息，用于在列表中展示
 */
public class HistoryItem {

    private final Result result; // ZXing 的原始识别结果对象（包含文本、格式、时间等）
    private final String display; // 经过格式化处理后的显示文本
    private final String details; // 额外的详细信息（如解析后的结构化数据）
    private final String createType; // 创建类型（如 Youtube、TEXT 等，用于创建历史记录）

    private boolean isSelect; // UI 标记：记录当前项在多选删除模式下是否被选中

    public HistoryItem(Result result, String display, String details) {
        this.result = result;
        this.display = display;
        this.details = details;
        this.createType = null;
    }
    
    // 带创建类型的构造函数
    public HistoryItem(Result result, String display, String details, String createType) {
        this.result = result;
        this.display = display;
        this.details = details;
        this.createType = createType;
    }

    //占位构造函数，用于特殊布局显示(如广告占位或空状态)
    public HistoryItem(boolean isFake) {
        this.result = null;
        this.display = null;
        this.details = null;
        this.createType = null;
    }

    public String getDisplay() {
        return display;
    }

    public Result getResult() {
        return result;
    }

    public String getDetails() {
        return details;
    }

    public String getCreateType() {
        return createType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
