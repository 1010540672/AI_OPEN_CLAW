# 在线APK编译服务推荐

如果你不想配置本地编译环境，可以使用以下在线服务快速编译APK：

---

## 🚀 推荐服务

### 1. GitHub Actions（推荐）

**优势**: 免费、稳定、可自定义

**使用步骤**:

1. 创建 `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

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

    - name: Release APK
      if: startsWith(github.ref, 'refs/tags/')
      uses: softprops/action-gh-release@v1
      with:
        files: app/build/outputs/apk/debug/*.apk
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

2. 推送到GitHub后，自动触发编译
3. 在Actions页面下载编译好的APK

---

### 2. GitLab CI/CD

```yaml
image: openjdk:17-jdk-slim

stages:
  - build

build_apk:
  stage: build
  script:
    - apt-get update && apt-get install -y unzip
    - export ANDROID_HOME=/opt/android-sdk
    - wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
    - unzip -q commandlinetools-linux-9477386_latest.zip -d $ANDROID_HOME/cmdline-tools
    - export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
    - yes | sdkmanager --licenses
    - sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
    - chmod +x gradlew
    - ./gradlew assembleDebug
  artifacts:
    paths:
      - app/build/outputs/apk/debug/*.apk
    expire_in: 1 week
```

---

### 3. CircleCI

```yaml
version: 2.1

orbs:
  android: circleci/android@2.0.0

jobs:
  build:
    docker:
      - image: cimg/android:2023.10
    steps:
      - checkout
      - run:
          name: Build APK
          command: ./gradlew assembleDebug
      - store_artifacts:
          path: app/build/outputs/apk/debug/*.apk
          destination: app-debug.apk
```

---

### 4. 在线APK编译器（第三方）

#### Buildozer.io

- 网址: https://buildozer.io
- 支持从GitHub项目直接编译
- 免费额度有限

#### Repl.it / Replit

- 网址: https://replit.com
- 创建Android模板项目
- 在线编译和运行

#### CodeSandbox（需要配置）

- 网址: https://codesandbox.io
- 支持Android项目

---

## 📦 打包项目为ZIP

如果需要将项目上传到在线服务：

### Linux/macOS

```bash
cd android-webview-perf-plugin
zip -r webview-perf-plugin.zip . -x "*/build/*" "*/.gradle/*" "*/.idea/*" ".git/*"
```

### Windows

在项目文件夹上右键 → 发送到 → 压缩(zipped)文件夹

---

## 🎯 快速使用GitHub Actions编译

### 最简方案

1. 将项目推送到GitHub
2. 在GitHub仓库创建 `.github/workflows/build.yml`
3. 复制上面的YAML内容
4. 提交并推送
5. 在Actions页面点击"Run workflow"
6. 等待编译完成（约5-10分钟）
7. 下载生成的APK

### 手动触发编译

在YAML文件中添加：

```yaml
on:
  workflow_dispatch:  # 允许手动触发
```

然后：
1. 进入GitHub仓库的 Actions 标签
2. 选择 "Build APK"
3. 点击 "Run workflow" → "Run workflow"
4. 等待编译完成
5. 在Artifacts中下载APK

---

## 🔧 自动化编译脚本

### 使用GitHub CLI

安装GitHub CLI后：

```bash
# 安装gh
# macOS: brew install gh
# Linux: https://cli.github.com/

# 登录
gh auth login

# 创建仓库
gh repo create webview-perf-plugin --public

# 推送代码
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/yourname/webview-perf-plugin.git
git push -u origin main

# 触发编译（需要配置workflow_dispatch）
gh workflow run "Build APK"

# 下载APK
gh run download
```

---

## 📊 编译时间估算

| 环境 | 编译时间 |
|------|----------|
| GitHub Actions | 5-10分钟 |
| GitLab CI/CD | 5-8分钟 |
| CircleCI | 5-10分钟 |
| 本地编译（首次） | 10-20分钟 |
| 本地编译（增量） | 1-3分钟 |

---

## 💾 下载编译好的APK

### GitHub Actions

1. 进入仓库的 Actions 标签
2. 选择最近的一次运行
3. 滚动到底部的 Artifacts 部分
4. 点击 "app-debug" 下载

### GitLab CI/CD

1. 进入 CI/CD → Pipelines
2. 点击最近的一次pipeline
3. 在 Jobs 中找到 build_apk
4. 点击下载图标

---

## ⚠️ 注意事项

1. **首次编译**需要下载SDK和依赖，时间较长
2. **免费额度**有限制，注意查看服务条款
3. **敏感信息**不要包含在代码中（API密钥等）
4. **GitHub仓库**设为private可以保护代码
5. **APK大小**可能影响上传速度

---

## 🔗 有用的链接

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [GitLab CI/CD文档](https://docs.gitlab.com/ee/ci/)
- [CircleCI文档](https://circleci.com/docs/)
- [Gradle文档](https://docs.gradle.org/)
- [Android开发者指南](https://developer.android.com/guide)

---

需要帮助？查看 [BUILD_GUIDE.md](./BUILD_GUIDE.md) 或提交Issue。
