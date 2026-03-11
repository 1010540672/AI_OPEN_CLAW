# Android Auth MVP Demo

一个使用 Kotlin + Jetpack Compose + MVP 架构的 Android 注册登录应用演示。

## 项目结构

```
app/
├── data/
│   ├── model/
│   │   └── User.kt              # 用户数据模型
│   └── repository/
│       └── AuthRepository.kt     # 认证数据仓库
├── ui/
│   ├── login/
│   │   ├── LoginPresenter.kt     # 登录 Presenter
│   │   └── LoginScreen.kt        # 登录界面
│   ├── register/
│   │   ├── RegisterPresenter.kt  # 注册 Presenter
│   │   └── RegisterScreen.kt     # 注册界面
│   ├── success/
│   │   └── SuccessScreen.kt      # 成功界面
│   └── theme/
│       ├── Color.kt              # 颜色配置
│       ├── Type.kt               # 字体配置
│       └── Theme.kt              # 主题配置
└── util/
    └── ValidationUtils.kt        # 验证工具
```

## 技术栈

- **语言**: Kotlin 2.1.0
- **UI**: Jetpack Compose (Material Design 3)
- **架构**: MVP (Model-View-Presenter)
- **导航**: Navigation Compose
- **状态管理**: StateFlow + ViewModel
- **构建工具**: Gradle (Kotlin DSL)

## 功能特性

✅ 用户注册
✅ 用户登录
✅ 表单验证
✅ 密码显示/隐藏
✅ 加载状态
✅ 错误提示
✅ Material Design 3 UI

## 使用方法

### 预置测试账户

应用内置了一个测试账户：
- 用户名: `admin`
- 密码: `123456`

### 运行项目

1. 克隆或下载项目
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击 Run 按钮或按 `Shift + F10`

## MVP 架构说明

### Model
- `User.kt`: 用户数据模型
- `AuthRepository.kt`: 数据访问层，处理注册和登录逻辑

### View
- `LoginScreen.kt`: 登录界面 Compose UI
- `RegisterScreen.kt`: 注册界面 Compose UI
- `SuccessScreen.kt`: 成功界面 Compose UI

### Presenter
- `LoginPresenter.kt`: 登录业务逻辑，处理登录请求和表单验证
- `RegisterPresenter.kt`: 注册业务逻辑，处理注册请求和表单验证

## 纯演示说明

本应用为纯演示版本，数据存储在内存中：
- 应用重启后注册的用户会丢失
- 预置了 `admin/123456` 测试账户
- 不包含真实后端 API 调用
- 不包含持久化存储（如 SharedPreferences、Room 等）

## 扩展建议

如需升级为生产应用，可以考虑：

1. **数据持久化**
   - 使用 Room 数据库
   - 使用 SharedPreferences 或 DataStore

2. **后端集成**
   - 对接真实 REST API
   - 添加 JWT Token 认证
   - 实现 Token 自动刷新

3. **安全增强**
   - 密码加密（如 bcrypt）
   - HTTPS 通信
   - 添加生物识别登录

4. **功能增强**
   - 忘记密码
   - 第三方登录（Google、微信等）
   - 手机号验证码登录

## License

MIT License
