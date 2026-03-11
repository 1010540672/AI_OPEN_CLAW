# 🚀 本地编译APK指南

## 当前服务器环境状态

### ✅ 已安装
- **Java JDK 17** - OpenJDK 17.0.18
- **Android SDK** - API 34, Build Tools 34.0.0, Platform Tools
- **项目代码** - 完整源代码

### ❌ 未安装（因网络问题）
- **Gradle 8.2** - 需要下载

---

## 📦 方案一：下载预配置项目包（推荐）

### 在本地编译（最简单）

1. **下载项目文件**
   ```bash
   # 将整个 android-webview-perf-plugin 文件夹下载到本地
   ```

2. **使用Android Studio编译**（推荐）
   - 打开 Android Studio
   - File → Open → 选择 `android-webview-perf-plugin` 文件夹
   - 等待Gradle同步（自动下载Gradle 8.2）
   - Build → Build APK(s)
   - APK位置：`app/build/outputs/apk/debug/app-debug.apk`

3. **或使用命令行**
   ```bash
   cd android-webview-perf-plugin
   ./compile_local.sh
   ```

---

## 🔧 方案二：配置本地环境

### 安装必要工具

#### 1. 安装Java 17
```bash
# macOS
brew install openjdk@17

# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk

# Windows
# 下载: https://adoptium.net/temurin/releases/?version=17
```

#### 2. 安装Android Studio
```bash
# 下载并安装
https://developer.android.com/studio

# 打开SDK Manager安装：
# - Android SDK Platform 34
# - Android SDK Build-Tools 34.0.0
# - Android SDK Platform-Tools
```

#### 3. 验证安装
```bash
java -version
adb version
```

---

## 🏗️ 编译步骤

### 使用Android Studio（最简单）

```
1. 打开项目
   File → Open → 选择 android-webview-perf-plugin

2. 等待同步
   右下角显示 "Gradle sync finished"

3. 编译APK
   Build → Build APK(s)

4. 获取APK
   点击通知中的 "locate"
   路径: app/build/outputs/apk/debug/app-debug.apk
```

### 使用命令行

```bash
# 进入项目目录
cd android-webview-perf-plugin

# 使用Gradle Wrapper（会自动下载Gradle 8.2）
./gradlew clean assembleDebug

# 或使用本地Gradle（如果已安装8.2+）
gradle clean assembleDebug
```

---

## 📱 安装APK

### 方式一：USB连接
```bash
# 1. 手机开启USB调试
# 设置 → 开发者选项 → USB调试

# 2. 安装APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. 启动应用
adb shell am start -n com.webview.perf.demo/.MainActivity
```

### 方式二：直接传输
```bash
# 将APK复制到手机
# 在手机上点击安装
```

---

## 🎯 快速开始

### 使用应用

1. **打开应用**
   - 找到并打开 "WebView Perf Demo"

2. **输入URL**
   - 在输入框中输入网址，例如：`https://www.baidu.com`

3. **点击加载**
   - 点击"加载"按钮

4. **查看性能数据**
   - 页面加载完成后，会自动弹出性能监控窗口
   - 查看详细的加载时间和性能评分

---

## 🆘 常见问题

### Q1: Gradle同步失败
```bash
# 清理Gradle缓存
./gradlew clean
rm -rf ~/.gradle/caches

# 重新同步
./gradlew build --refresh-dependencies
```

### Q2: SDK组件未找到
```bash
# 在Android Studio中打开SDK Manager
# 安装缺失的组件
# 或命令行安装：
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
```

### Q3: Java版本不匹配
```bash
# 检查Java版本
java -version

# 应显示 Java 17
# 如果不是，请设置JAVA_HOME
export JAVA_HOME=/path/to/java17
```

---

## 📋 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | JDK版本 |
| Android SDK | API 34 | 目标平台 |
| Gradle | 8.2+ | 构建工具 |
| Build Tools | 34.0.0 | 编译工具 |

---

## ✅ 验证清单

编译完成后，确认：

- [ ] APK文件已生成
- [ ] 文件大小约 2-3 MB
- [ ] 能够安装到手机
- [ ] 应用可以正常打开
- [ ] 可以加载网页
- [ ] 性能窗口正常显示

---

## 📞 需要帮助？

如果在本地编译遇到问题：

1. 查看项目中的 `FAQ.md`
2. 查看 `BUILD_GUIDE.md` 完整指南
3. 运行 `adb logcat | grep WebViewPerf` 查看日志

---

**注意：** 由于服务器网络限制，无法下载Gradle 8.2完成编译。请在本地使用Android Studio编译，体验最佳且最简单！

**预计本地编译时间：** 5-10分钟（首次下载依赖）
