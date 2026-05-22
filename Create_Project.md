# Create_Project.md - 二维码创建模块复现全流程

本文档详细说明了如何复现项目中的“创建模块”，重点讲解 **YouTube** 和 **日历 (Calendar)** 两种类型的实现细节。

---

## 1. 核心架构与逻辑流

创建功能的通用流程为：
1. **[UI]** `CreateFragment` (入口) -> 2. **[UI]** `InputActivity` (用户输入) -> 3. **[Model]** `CreateResultModel` (格式化数据) -> 4. **[Engine]** `CreateUtil` (生成位图) -> 5. **[UI]** `CreateResultActivity` (展示与保存)。

---

## 2. YouTube 类型复现步骤

YouTube 二维码本质上是一个 **URL 字符串**。

### A. 输入处理 (`YoutubeInputActivity`)
*   **支持模式**：
    *   **Video ID**: 拼接为 `https://www.youtube.com/watch?v=VIDEO_ID`
    *   **Channel ID**: 拼接为 `https://www.youtube.com/channel/CHANNEL_ID`
    *   **直接 URL**: 直接使用。
*   **代码参考**：
    ```java
    // 关键拼接逻辑
    if (category == CATEGORY_VIDEO) {
        ((CreateYoutubeModel) baseResultModel).setVideoId(inputEtStr);
    }
    ```

### B. 模型构建 (`CreateYoutubeModel`)
*   该模型继承自 `BaseCreateModel`，其 `formatResult()` 方法会将用户输入的 ID 最终转化为标准的 URL 字符串。

---

## 3. 日历 (Calendar) 类型复现步骤

日历二维码采用的是 **iCalendar (VEvent)** 格式。

### A. 输入处理 (`CalenderInputActivity`)
*   **核心字段**：标题 (Summary)、地点 (Location)、描述 (Description)、开始/结束时间。
*   **时间格式**：必须转换为 `YYYYMMDDTHHMMSS` 格式（如果是全天则为 `YYYYMMDD`）。
*   **代码参考**：
    ```java
    // 关键时间格式转换
    String dtstart_date = year_start + month_start + day_start + "T" + hour_start + minute_start + second_start;
    ```

### B. 模型构建 (`CreateCalendarModel`)
*   **VEvent 协议拼接**：
    日历的字符串必须严格遵守以下格式才能被扫码器识别：
    ```text
    BEGIN:VEVENT
    SUMMARY:会议标题
    LOCATION:会议室
    DESCRIPTION:详细描述
    DTSTART:20231027T100000
    DTEND:20231027T110000
    END:VEVENT
    ```

---

## 4. 通用核心环节 (关键点)

### A. 生成位图 (调用 `createlib`)
无论什么类型，最终都调用 `CreateUtil.java` 进行绘制：
```java
// content 为上述步骤拼接好的字符串
Bitmap bitmap = CreateUtil.createQRCode(content, 800, 800);
```

### B. 保存图片
在 `CreateResultActivity` 中实现，将 Bitmap 压缩为 JPEG/PNG 文件并存入手机相册。
```java
// 核心逻辑
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
```

### C. 系统分享
利用 Android 的 `Intent.ACTION_SEND` 调起分享面板。

---

## 💡 给实习生的建议
1. **字符串拼接是灵魂**：YouTube 比较简单，重点处理好日历的 `BEGIN:VEVENT...END:VEVENT` 格式。
2. **UI 细节**：输入页建议参考项目中已有的 `layout_toolbar_input.xml` 保持风格统一。
3. **测试**：生成后，先用手机自带相机扫一下，看能不能识别成 YouTube 链接或日历提醒。
4. 第一阶段：搭建“分类主页” (入口)
   目标：在主界面展示出各种创建类型的列表。
1.
找到代码位置：
◦
app/src/main/java/.../page/create/CreateFragment.java
2.
核心任务：
◦
布局文件：修改 fragment_create.xml，添加一个 RecyclerView。
◦
适配器：编写 CreateRCVAdapter。
◦
知识点应用：
▪
使用 12.5 卡片式布局 (CardView) 作为列表的条目样式，让 UI 看起来精致。
▪
点击某个卡片（比如 YouTube）时，使用 startActivity 跳转到对应的输入页。
第二阶段：编写“YouTube 输入页” (业务逻辑)
目标：接收用户输入并准备好要生成二维码的数据。
1.
找到代码位置：
◦
app/src/main/java/.../page/create/input/YoutubeInputActivity.java
2.
核心任务：
◦
URL 拼接：YouTube 二维码本质上就是一个网址链接。你需要获取用户输入的视频 ID 或 链接，拼成标准的 https://www.youtube.com/... 字符串。
◦
传递数据：点击“生成”按钮后，将这个字符串通过 Intent 传递给结果页，或者在这里直接调用生成逻辑。
◦
知识点应用：
▪
使用 11.4 JSON 解析：如果数据复杂，可以先转成 JSON 字符串再传递。
第三阶段：调用核心引擎 (生成二维码)
目标：将字符串变成一张二维码图片（Bitmap）。
1.
找到代码位置：
◦
createlib/src/main/java/.../create/format/CreateUtil.java
2.
核心任务：
◦
不要重写算法：直接调用 CreateUtil.createQRCode(content, width, height)。
◦
知识点应用：
▪
使用 11.7 协程 (Coroutines)：生成二维码虽然快，但在低端机上可能有微小卡顿。建议在协程的 Dispatchers.IO 中执行生成，在 Dispatchers.Main 中显示。
第四阶段：实现“结果页” (保存与分享)
目标：展示成果，并让用户能把图存到手机里。
1.
找到代码位置：
◦
app/src/main/java/.../page/create/result/CreateResultActivity.java
2.
核心任务：
◦
保存图片：利用 Bitmap.compress() 将图片流写入 SD 卡。
◦
系统分享：使用标准的 Intent.ACTION_SEND 调起系统的分享面板。
◦
知识点应用：
▪
使用 12.2 Toolbar：在页面顶部加一个返回键和保存按钮。
给你的“第一行代码”建议：
如果你现在就要动手，请从 CreateFragment 的布局文件开始：
1.
打开 app/src/main/res/layout/fragment_create.xml。
2.
放一个 RecyclerView。
3.
创建一个简单的 item_create_type.xml，里面放一个 CardView，CardView 里面放一个 ImageView（图标）和 TextView（名称）。
先看到界面，再写跳转逻辑，最后写生成代码。 这种“看得见”的反馈会让你作为实习生更有信心！需要我帮你读取具体的布局代码作为参考吗？
