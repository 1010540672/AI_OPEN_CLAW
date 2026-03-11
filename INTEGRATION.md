# 快速集成指南

## 5分钟集成到现有项目

### Step 1: 复制插件文件

将以下目录复制到你的项目根目录：
```
your-project/
├── plugin/           # 从示例项目复制
├── monitor-lib/      # 从示例项目复制
└── build.gradle.kts  # 添加配置
```

### Step 2: 修改根配置文件

在 `settings.gradle.kts` 中：
```kotlin
include(":plugin", ":monitor-lib", ":app")
```

在根目录 `build.gradle.kts` 中：
```kotlin
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}
```

### Step 3: 修改应用模块配置

在你的应用模块 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":monitor-lib"))
}

apply {
    plugin("com.webview.perf.plugin")
}

configure<com.webview.perf.WebViewPerfExtension> {
    enableTrace = true
    enableVisualization = true
    packageName = "com.your.package"
}
```

### Step 4: 在Application中初始化

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebViewMonitor.init(this)
        WebViewMonitor.setEnabled(true)
        WebViewMonitor.setVisualizationEnabled(true)
    }
}
```

别忘了在 `AndroidManifest.xml` 中注册：
```xml
<application
    android:name=".MyApplication"
    ...>
```

### Step 5: 添加混淆规则

在 `proguard-rules.pro` 中：
```proguard
-keep class com.webview.perf.monitor.** { *; }
-dontwarn com.webview.perf.monitor.**
```

### Step 6: 运行项目

```bash
./gradlew clean assembleDebug
```

### 完成！✨

现在你的所有WebView加载都会自动被监控！
