# APK编译指南

## 🚀 快速开始（推荐方案）

### 方案一：使用Android Studio编译（最简单）

1. **下载并安装 Android Studio**
   - 访问：https://developer.android.com/studio
   - 下载并安装最新版本
   - 启动并完成初始设置

2. **打开项目**
   - 启动 Android Studio
   - 选择 "Open an Existing Project"
   - 选择 `android-webview-perf-plugin` 目录

3. **等待Gradle同步**
   - 第一次打开会自动下载依赖
   - 等待右下角的同步完成（可能需要几分钟）

4. **编译APK**
   - 点击菜单：`Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - 或使用快捷键：`Ctrl + F9` (Windows/Linux) / `Cmd + F9` (macOS)

5. **查找APK**
   - 编译完成后会弹出通知
   - 点击 "locate" 或在目录中找到：
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```

---

### 方案二：使用命令行编译

#### Linux/macOS

```bash
# 1. 克隆或下载项目
cd android-webview-perf-plugin

# 2. 一键配置环境（Linux）
chmod +x setup_env.sh
./setup_env.sh
source ~/.bashrc

# 3. 编译APK
chmod +x build_apk.sh
./build_apk.sh
```

#### Windows

```cmd
REM 1. 克隆或下载项目
cd android-webview-perf-plugin

REM 2. 运行编译脚本
build_apk.bat
```

---

## 📋 环境要求详解

### 必须安装的工具

| 工具 | 版本 | 用途 |
|------|------|------|
| Java JDK | 17+ | 编译环境 |
| Android SDK | API 34 | Android平台 |
| Build Tools | 34.0.0 | 编译工具链 |
| Gradle | 8.2+ | 构建工具 |

---

## 🔧 手动配置环境

### Windows

#### 1. 安装Java JDK
```
下载：https://adoptium.net/temurin/releases/
选择：Temurin 17 (LTS) - Windows x64
安装后设置环境变量：
  JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.0.x
  Path 添加 %JAVA_HOME%\bin
```

#### 2. 安装Android Studio
```
下载：https://developer.android.com/studio
安装后打开 SDK Manager：
  - SDK Platforms: 勾选 Android 14 (API 34)
  - SDK Tools: 勾选 Android SDK Build-Tools 34.0.0
  - 点击 Apply 安装
```

#### 3. 设置环境变量
```
系统环境变量中添加：
  ANDROID_HOME = C:\Users\你的用户名\AppData\Local\Android\Sdk
  Path 中添加：
    %ANDROID_HOME%\cmdline-tools\latest\bin
    %ANDROID_HOME%\platform-tools
    %ANDROID_HOME%\build-tools\34.0.0
```

验证安装：
```cmd
java -version
adb version
```

---

### macOS

#### 1. 安装Homebrew
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

#### 2. 安装Java
```bash
brew install openjdk@17

# 添加到shell配置
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

#### 3. 安装Android Studio
```
下载：https://developer.android.com/studio
安装后打开 SDK Manager 安装必要组件
```

#### 4. 设置环境变量
```bash
# 添加到 ~/.zshrc 或 ~/.bash_profile
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/build-tools/34.0.0

source ~/.zshrc
```

---

### Linux (Ubuntu/Debian)

使用提供的一键配置脚本：
```bash
chmod +x setup_env.sh
./setup_env.sh
source ~/.bashrc
```

或手动配置：
```bash
# 安装Java
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# 下载Android Command Line Tools
mkdir -p ~/Android/cmdline-tools
cd ~/Android/cmdline-tools
curl -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip cmdline-tools.zip
mkdir -p latest
mv cmdline-tools/* latest/
rm cmdline-tools.zip
cd -

# 设置环境变量
echo 'export ANDROID_HOME=~/Android' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc

# 接受许可
yes | sdkmanager --licenses

# 安装组件
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 安装Gradle
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.2
```

---

## 📱 安装APK到设备

### 通过USB连接

1. **启用USB调试**
   - 手机：设置 → 开发者选项 → USB调试（勾选）
   - 连接手机时选择"文件传输"模式

2. **安装APK**
```bash
# Linux/macOS
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Windows
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

3. **启动应用**
```bash
adb shell am start -n com.webview.perf.demo/.MainActivity
```

### 通过WiFi连接（无需USB）

```bash
# 1. 确保手机和电脑在同一WiFi网络

# 2. 通过USB连接一次
adb tcpip 5555

# 3. 查看手机IP地址
adb shell ip addr show wlan0

# 4. 通过WiFi连接（替换IP为实际IP）
adb connect 192.168.1.100:5555

# 5. 断开USB，现在可以无线安装了
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔍 常见问题

### Q1: 编译失败，提示找不到Java
```bash
# 检查Java是否安装
java -version

# 设置JAVA_HOME环境变量
export JAVA_HOME=/path/to/java17
```

### Q2: Gradle同步失败
```bash
# 清理Gradle缓存
./gradlew clean
rm -rf ~/.gradle/caches

# 重新同步
./gradlew build --refresh-dependencies
```

### Q3: SDK组件未找到
```bash
# 列出已安装的SDK组件
sdkmanager --list_installed

# 安装缺失的组件
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
```

### Q4: 签名问题
Debug版本会自动使用debug签名密钥，无需额外配置。

如果需要Release版本：
```bash
./gradlew assembleRelease
# APK位置：app/build/outputs/apk/release/app-release.apk
```

---

## 🎯 编译变体

项目支持多种编译配置：

| 命令 | 说明 | 输出位置 |
|------|------|----------|
| `assembleDebug` | Debug版本（带日志，未混淆） | `app/build/outputs/apk/debug/` |
| `assembleRelease` | Release版本（混淆，优化） | `app/build/outputs/apk/release/` |
| `bundleDebug` | Debug AAB（Google Play） | `app/build/outputs/bundle/debug/` |
| `bundleRelease` | Release AAB（Google Play） | `app/build/outputs/bundle/release/` |

---

## 📦 交付APK

编译完成后，APK文件位于：
```
android-webview-perf-plugin/app/build/outputs/apk/debug/app-debug.apk
```

文件大小约：**2-3 MB**

你可以：
1. 直接发送APK文件给他人安装
2. 上传到任何平台分享
3. 通过adb安装到已连接的设备

---

## 🔄 自动化构建（可选）

### GitHub Actions

创建 `.github/workflows/build.yml`：

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build with Gradle
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/*.apk
```

---

## ✅ 验证安装

安装APK后，打开应用：

1. 看到WebView界面 ✅
2. 在输入框输入URL（如：https://www.baidu.com） ✅
3. 点击"加载"按钮 ✅
4. 页面加载完成后，会自动弹出性能监控窗口 ✅
5. 窗口显示详细性能数据 ✅

如果一切正常，恭喜！🎉

---

## 📞 获取帮助

遇到问题？

1. 查看FAQ.md
2. 提交Issue到项目仓库
3. 检查Gradle日志：`./gradlew assembleDebug --info`
4. 检查Android设备日志：`adb logcat | grep WebViewPerf`

---

**祝你编译顺利！** 🚀
