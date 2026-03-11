# WebView监控库混淆规则
-keep class com.webview.perf.monitor.** { *; }
-keepclassmembers class com.webview.perf.monitor.** { *; }
-dontwarn com.webview.perf.monitor.**

# Kotlin序列化相关
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-dontwarn kotlinx.serialization.**
-keep,includedescriptorclasses class kotlinx.serialization.json.** { *; }
-keepclassmembers,allowobfuscation class kotlinx.serialization.json.** { *; }

# Android WebView相关
-keep class android.webkit.** { *; }
-keepclassmembers class android.webkit.** { *; }

# 字节码插桩相关
-keep class org.objectweb.asm.** { *; }
-keepclassmembers class org.objectweb.asm.** { *; }
