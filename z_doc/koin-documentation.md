# Koin - Android Dependency Injection Full Guide

Koin 是一个轻量级的依赖注入框架，专为 Kotlin 应用程序设计，易于使用，且无需代码生成。它为 Android 开发提供了灵活的依赖管理方案，适用于各种场景。

---

## 特性

- **轻量级**: 无需代理、代码生成，直接使用 Kotlin DSL。
- **模块化**: 通过模块组织依赖，支持单例、工厂、作用域。
- **与 Android 集成**: 原生支持 Android Context、ViewModel、WorkManager 等。
- **灵活性**: 支持动态参数注入、范围管理和测试。

---

## 依赖配置

在 `build.gradle` 文件中添加以下依赖：

```gradle
dependencies {
    implementation "io.insert-koin:koin-android:3.x.x" // 核心库
    implementation "io.insert-koin:koin-androidx-viewmodel:3.x.x" // ViewModel 支持
    implementation "io.insert-koin:koin-androidx-workmanager:3.x.x" // WorkManager 支持
    implementation "io.insert-koin:koin-androidx-navigation:3.x.x" // Navigation 支持
    testImplementation "io.insert-koin:koin-test:3.x.x" // 测试支持
}
```

---

## 基本使用

### 1. 定义模块

Koin 使用模块（`module`）来管理依赖，通过 DSL 定义：

```kotlin
import org.koin.dsl.module

val appModule = module {
    single { Repository() } // 单例
    factory { Presenter(get()) } // 工厂模式
}
```

### 2. 启动 Koin

在自定义的 `Application` 类中初始化 Koin：

```kotlin
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication) // 提供 Android Context
            modules(appModule) // 注册模块
        }
    }
}
```

### 3. 注入依赖

#### 在 Activity 或 Fragment 中：

```kotlin
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val repository: Repository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 repository
    }
}
```

#### 在 ViewModel 中：

```kotlin
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 viewModel
    }
}
```

---

## 高级使用

### 1. 动态参数注入

Koin 支持通过参数动态注入依赖：

```kotlin
val dynamicModule = module {
    factory { (id: String) -> DynamicPresenter(id) }
}
```

使用时传递参数：

```kotlin
val presenter: DynamicPresenter by inject { parametersOf("1234") }
```

---

### 2. 作用域（Scopes）

作用域用于管理生命周期内共享的实例，例如 Activity/Fragment 的生命周期：

```kotlin
val scopeModule = module {
    scope(named("ActivityScope")) {
        scoped { ScopedPresenter() }
    }
}
```

在 Activity 中使用：

```kotlin
class MainActivity : AppCompatActivity() {
    private val scopedPresenter: ScopedPresenter by inject()
}
```

---

### 3. Koin 与 Android Jetpack 的集成

#### 与 ViewModel 集成

```kotlin
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}
```

注入 ViewModel：

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
}
```

#### 与 WorkManager 集成

```kotlin
val workManagerModule = module {
    single { MyWorker(get()) }
}
```

在 Worker 中使用：

```kotlin
class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private val repository: Repository by inject()
}
```

#### 与 Navigation 集成

在 Navigation Graph 中直接注入：

```kotlin
val navModule = module {
    viewModel { NavViewModel(get()) }
}
```

---

## 测试支持

Koin 提供丰富的测试功能，无需 Android 环境即可测试依赖注入：

```kotlin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class MyTest : KoinTest {
    private val repository: Repository by inject()

    @Test
    fun testRepository() {
        declareMock<Repository> {
            // 自定义 mock 行为
        }
        assertNotNull(repository)
    }
}
```

---

## 调试工具

启用日志以调试依赖注入问题：

```kotlin
startKoin {
    androidLogger(Level.DEBUG)
    modules(appModule)
}
```

---

## 常见问题

### 为什么依赖注入失败？

- 确保模块已在 `startKoin` 中注册。
- 检查是否正确配置了 Android Context。
- 确保依赖关系没有循环引用。

---

## 总结

Koin 是一个功能强大且易于使用的 Android 依赖注入框架，使用其模块化设计和 Kotlin DSL，可以轻松实现高效、可维护的依赖管理。

官方文档：[Koin Documentation](https://insert-koin.io/)