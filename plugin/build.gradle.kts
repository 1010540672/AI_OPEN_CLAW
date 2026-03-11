plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.2.0")
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation("com.android.tools.build:transform-api:2.0.0-deprecated-use-gradle-api")
}

gradlePlugin {
    plugins {
        create("webviewPerfPlugin") {
            id = "com.webview.perf.plugin"
            implementationClass = "com.webview.perf.WebViewPerfPlugin"
            displayName = "WebView Performance Plugin"
            description = "Bytecode instrumentation plugin for WebView performance monitoring"
        }
    }
}
