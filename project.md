# QRCode-main 项目模块与功能解析指南

你好，新同事！这份文档旨在帮你快速理清项目的模块架构，并深入分析你需要复现的三个核心功能：**扫描模块**、**创建模块**和**历史记录模块**。

---

## 一、 项目模块分工概览

本项目采用高度解耦的多模块设计，每个模块各司其职。

| 模块名称 | 功能定位 | 核心职责 |
| :--- | :--- | :--- |
| **`:app`** | **主入口模块** | 粘合各模块业务，管理主界面 Tab 切换，初始化第三方 SDK（Firebase 等）。 |
| **`:baselib`** | **基础通用库** | 提供 Activity/Dialog 基类、权限申请框架、通用工具类（如字符串、图片加载）。 |
| **`:scanlib`** | **扫码业务层** | 封装扫码页 UI 组件（如 `ScanView`）和扫码流程控制。 |
| **`:cameralib`** | **相机驱动层** | 负责 Android 相机硬件的开启、预览流回调和硬件参数配置。 |
| **`:createlib`** | **二维码生成层** | 包含将各种数据模版转化为二维码位图（Bitmap）的底层逻辑。 |
| **`:parsedlib`** | **数据解析层** | 将扫码得到的原始字符串解析为结构化对象（Email, WiFi, YouTube 等）。 |
| **`:zxinglib`** | **ZXing 封装库** | 对 Google ZXing 开源算法的轻量级封装，提供基础编解码能力。 |
| **`:googlevisionlib`** | **视觉识别库** | 集成 Google Vision API，用于在特定场景下辅助识别。 |
| **`:languagelib`** | **国际化库** | 管理 App 内的语言切换逻辑。 |

---

## 二、 核心功能复现深度解析

### 1. 扫描模块 (Scanning)
这是项目的“眼睛”，涉及相机调用与算法反馈。

*   **复现路径**：
    1.  **权限申请**：在 `MainActivity` 中调用 `baselib` 的 `PermissionHelper` 申请相机权限。
    2.  **相机预览**：在 `ScanFragment` 中初始化 `cameralib` 的 `CameraManager`，将预览流绘制到 `ScanView`。
    3.  **结果跳转**：扫描成功后，拿到 `Result` 字符串，调用 `parsedlib` 进行数据解析，最后跳转至 `ScanResultActivity`。
*   **关键类参考**：
    *   `qrscanner.barcodescanner...page.scan.ScanFragment`: 扫描页的主控逻辑。
    *   `com.drojian.qrcode.cameralib.camera.CameraManager`: 负责开启摄像头。
    *   `com.drojian.qrcode.baselib.ScanResultModel`: 扫码结果的数据载体。

### 2. 创建模块 (Creation)
这是项目的“工厂”，负责将用户输入转化为图形。

*   **复现路径**：
    1.  **分类主页**：在 `CreateFragment` 中使用 `RecyclerView` 配合 `CardView` 展示各类型。
    2.  **二级输入页**：实现 `YoutubeInputActivity`（重点学习 URL 拼接）和 `CalenderInputActivity`（重点学习 VEvent 格式）。
    3.  **生成位图**：调用 `createlib` 中的 `CreateUtil.createQRCode()`。
    4.  **保存分享**：在 `CreateResultActivity` 中，利用 `Bitmap.compress()` 保存到本地，通过 `Intent.ACTION_SEND` 实现分享。
*   **关键类参考**：
    *   `qrscanner...page.create.input.YoutubeInputActivity`: YouTube 输入逻辑。
    *   `com.drojian.qrcode.createlib.create.format.CreateUtil`: 生成二维码的核心算法。
    *   `qrscanner...page.create.result.CreateResultActivity`: 生成结果页。

### 3. 历史记录模块 (History)
这是项目的“记忆”，负责数据的持久化存储。

*   **复现路径**：
    1.  **数据库搭建**：本项目当前使用的是原生的 `SQLiteOpenHelper`（位于 `app` 模块的 `data.db` 包下）。你需要定义表结构（`HistoryItem`）。
    2.  **增删查改**：使用 `HistoryManager`（扫描历史）和 `CreateHistoryManager`（创建历史）进行操作。
    3.  **UI 展示**：在 `HistoryFragment` 中使用 `ViewPager2` 嵌套两个子 Fragment，分别展示扫描和创建的列表。
    4.  **结果复现**：点击历史条目，将保存的 JSON 字符串还原成 `ScanResultModel`，再次跳转到结果页。
*   **关键类参考**：
    *   `qrscanner...data.db.DBHelper`: 数据库建表。
    *   `qrscanner...data.db.HistoryManager`: 数据库操作单例。
    *   `qrscanner...page.history.ScanHistoryFragment`: 列表展示页。

---

## 三、 给实习生的复现建议

1.  **UI 方面**：不要纠结复杂的动画（如折叠标题栏、下拉刷新），本项目并没有使用这些复杂组件。请重点练好 `ConstraintLayout` 和 `CardView`。
2.  **数据方面**：**JSON 解析（11.4节）**是模块间通信的灵魂，请务必掌握 `JSONObject` 的用法。
3.  **线程方面**：扫码和生成位图必须在后台线程运行。虽然本项目大量使用 Java 线程，但建议你使用 **Kotlin 协程（11.7节）** 来实现，这更符合现代 Android 开发趋势。
4.  **架构方面**：虽然本项目历史记录使用了原生 SQLite，但导师可能会更希望看到你使用 **Room（13.5节）**。如果你有时间，建议尝试用 Room 重构历史模块。

祝你在复现过程中收获满满，遇到困难随时翻阅本手册！
