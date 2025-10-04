# ⚡ LSPosed & LSPatch 集成方案

[![LSPosed Status](https://img.shields.io/github/actions/workflow/status/JingMatrix/LSPosed/core.yml?branch=master&logo=github&label=LSPosed%20Build)](https://github.com/JingMatrix/LSPosed/actions/workflows/core.yml?query=event%3Apush+分支%3Amaster+已完成%3)
[![LSPatch Status](https://img.shields.io/github/actions/workflow/status/JingMatrix/LSPatch/main.yml?branch=master&logo=github&label=LSPatch%20Build)](https://github.com/JingMatrix/LSPatch/actions/workflows/main.yml?query=event%3Apush+is%3Acompleted+branch%3Amaster)
[![License](https://img.shields.io/badge/License-GPL--3-blue.svg)](http://www.gnu.org/copyleft/gpl.html)

---

## ✨ 简介 (Introduction)

本项目集成了两个强大的 Android 挂钩（Hooking）框架，为您提供一个**灵活、全覆盖**的 Xposed 模块解决方案：

1.  **LSPosed Framework (基于 Zygisk)：** 适用于**已 Root** 设备，提供系统级的挂钩服务。
2.  **LSPatch Framework (Rootless)：** 适用于**免 Root** 设备，通过 APK 打补丁（Patching）实现模块功能。

无论您的设备状态如何，您都可以使用 Xposed 模块来修改和增强系统及应用程序的行为。

---

## 🚀 核心组件概览

| 组件 | 📁 目录 | 🛠️ 适用场景 | 🔑 核心功能 |
| :--- | :--- | :--- | :--- |
| **LSPosed** | `/LSPosed` | **✅ 已 Root 设备** (需 Magisk v26+) | 基于 Zygisk 的 ART 钩子框架，提供与 **Xposed API** 完全兼容的模块系统。 |
| **LSPatch** | `/LSPatch` | **📱 免 Root 设备** | 通过对目标 APK 进行打补丁，将 LSPosed 核心框架和 Xposed API **注入**到应用中。 |

---

## 1. LSPosed 框架 (Root 方案)

### 📌 概述

LSPosed 是一个强大的 **ART 挂钩框架**，利用 **Magisk Zygisk** 模块和 **LSPlant** 核心框架，实现了对 Android 系统和应用行为的深度修改，同时保持与原始 Xposed API 的兼容性。

### ⚙️ 特性与安装

| 特性 | 描述 |
| :--- | :--- |
| **兼容性** | 与原始 Xposed Framework 模块**完全兼容**。 |
| **支持版本** | Android **8.1 ~ 16**。 |
| **安装要求** | 必须安装 **Magisk v26+**。 |

### ⬇️ 安装步骤

1.  确保设备已安装 **Magisk v26+**。
2.  下载 LSPosed 模块，并在 Magisk 应用中安装。
3.  重启设备。
4.  通过通知栏或桌面快捷方式打开 LSPosed Manager 激活模块。

### 👨‍💻 开发者资源

欢迎开发者基于 LSPosed 编写 Xposed 模块！

* **Xposed API 文档:** [api.xposed.info](https://api.xposed.info/)
* **LSPosed 模块库:** [GitHub Repo](https://github.com/Xposed-Modules-Repo)

---

## 2. LSPatch 项目 (Rootless 方案)

### 📌 概述

LSPatch 是 **LSPosed 框架的免 Root 实现**。它通过**将核心代码和 Xposed API 注入目标 APK**，使模块功能能够在不 Root 的设备上运行。

### ⚙️ 支持版本

* **最小支持:** Android 9
* **最大支持:** 理论上与 LSPosed 相同。

### ⬇️ 使用方法

LSPatch 提供了 Manager App 和命令行两种打补丁方式：

| 方式 | 说明 |
| :--- | :--- |
| **📱 Manager App** | 下载并安装 `manager.apk`，在 Android 设备上可视化操作，选择目标 APK 并打补丁。 |
| **💻 命令行 (Jar)** | 下载 `lspatch.jar`，通过运行 `java -jar lspatch.jar` 进行批量或自动化操作。 |

---

## 📜 许可证 (License)

本项目中的 LSPosed 和 LSPatch 均根据 **GNU General Public License v3 (GPL-3)** 获得许可。

## 🙏 致谢 (Credits)

感谢所有开源项目和贡献者，特别是：

* **LSPosed** ([JingMatrix/LSPosed](https://github.com/JingMatrix/LSPosed))
* **LSPatch** ([JingMatrix/LSPatch](https://github.com/JingMatrix/LSPatch))
* **Magisk**, **LSPlant**, **XposedBridge**, **Xpatch** 等。
