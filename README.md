# LSPosed & LSPatch 集成方案

## 简介

本项目集成了两个强大的 Android 挂钩（Hooking）框架，旨在为 Android 设备提供灵活的修改方案，同时覆盖 **Root 权限** 和 **免 Root** 的不同场景：

1.  **LSPosed Framework (基于 Zygisk)：** 适用于已 Root 设备，提供与原始 **Xposed API** 兼容的模块系统。
2.  **LSPatch Framework (Rootless)：** 适用于免 Root 设备，通过对 APK 打补丁（Patching）实现 **Xposed API** 的集成。

## 核心组件与功能

本项目包含以下两个主要子项目，分别位于 `/LSPosed` 和 `/LSPatch` 文件夹下：

| 子项目 | 场景/需求 | 核心功能 |
| :--- | :--- | :--- |
| **LSPosed** | **已 Root 设备** (需要 Magisk v26+) | 基于 Zygisk 的 ART 钩子框架，实现了与 Xposed API 兼容的模块系统。 |
| **LSPatch** | **免 Root 设备** | 通过对目标 APK 进行打补丁（Patching），将 LSPosed 核心框架和 Xposed API 注入到应用中。 |

---

## 1. LSPosed 框架（/LSPosed）

**LSPosed** 是一个利用 **Magisk Zygisk** 模块提供的 ART 钩子框架，它基于 **LSPlant** 框架，旨在提供与原始 **Xposed API** 完全兼容的模块系统。

### 特性

* **API 兼容性：** 与 Xposed Framework 及其模块完全兼容。
* **支持版本：** Android 8.1 至 Android 16。
* **依赖：** 需安装 **Magisk v26+**。

### 安装 (Root 方案)

1.  安装 Magisk v26+。
2.  下载 LSPosed 模块并在 Magisk 应用中安装。
3.  重启设备，从通知栏打开 LSPosed Manager。

### 开发者资源

* **Xposed 框架 API:** [api.xposed.info](https://api.xposed.info/)
* **模块库：** [LSPosed 模块库](https://github.com/Xposed-Modules-Repo)

---

## 2. LSPatch 项目（/LSPatch）

**LSPatch** 提供了 **LSPosed 框架的免 Root 实现**。它通过将核心代码插入到目标 APK 中，实现 **Xposed API** 的集成，让用户在不 Root 设备的情况下也能使用 Xposed 模块。

### 特性

* **Rootless (免 Root)：** 无需 Root 权限即可使用。
* **支持版本：** 最小支持 Android 9，理论上最大支持与 LSPosed 相同的版本。

### 使用方法 (免 Root 方案)

您可以选择通过 Manager 应用或命令行 Jar 包来对目标 APK 进行打补丁：

* **通过 Manager App：**
    1.  下载并安装 `manager.apk` 到您的 Android 设备。
    2.  按照应用指引进行操作。
* **通过 Jar 包 (命令行)：**
    1.  下载 `lspatch.jar`。
    2.  运行 `java -jar lspatch.jar`。

---

## 许可证

本项目中的 **LSPosed** 和 **LSPatch** 均在 **GNU General Public License v3 (GPL-3)** 下获得许可。

## 致谢

本项目感谢以下核心开源项目和贡献者：

* **LSPosed** ([https://github.com/JingMatrix/LSPosed](https://github.com/JingMatrix/LSPosed))
* **LSPatch** ([https://github.com/JingMatrix/LSPatch](https://github.com/JingMatrix/LSPatch))
* **核心依赖项:** Magisk, LSPlant, XposedBridge, Xpatch 等。
