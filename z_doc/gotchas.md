# 坑🤯

1. 自定义的ViewGroup不继承自ConstraintLayout
```text
不利于减少层级，导致xml中嵌套层级过深，导致卡顿
```

2. 没必要的Fragment/View提前加载了
```text
比如一个xml中存在Winningfragment，子fragment只在胜利状态时才需要加载，而不是一开始就加载，
正确的做法时胜利的时候动态加载，胜利结束移除，否则影响fragment的加载速度
```
```text
同样的道理，view也存在过度加载，常见的比如遮罩，只在某个阶段才会加载遮罩，不是一开始就加载。
否则遮罩图片太多（特别在RecycleView中）会影响fragment的加载速度
```

3. fragment之间的通信
```text
常见的做法是使用EventBus，EventBus作为全局变量，容易滥用且不好跟踪，数据流的发送和接受很难定位
推荐的做法：
（1）使用SharedViewModel，fragment之间使用共享的viewModel
（2）使用内存级别Room数据库，fragment的viewModel对Room操作和监听
```

4. UI层操作数据   
- UI层只应该跟ViewModel交互
  
  ![img.png](img/img_7.png)
  ⚠️ UI層不要有資料層的東西注入，也不應該有資料的業務邏輯

- UI层避免View之间直接操作
```text
常见的错误是：btn点击事件中对其他view进行更改，比如textView。如果收到网络，也需要更改textView。
那么textView的改动点就不唯一了，textView和btn，网络强耦合了。

正确的做法是使用liveData/flow的方式解耦：
textView通过监听liveData/flow来更改，其他操作liveData/flow
```

5. 泛型擦出引起的as?失效
```kotlin
var currentAnimal: Animal = Dog()
abstract class Animal 
class Dog:Animal()
class Cat:Animal()
fun <T: Animal> toAnimal():T?{
    return currentAnimal as? T
}

fun test(){
    toAnimal<Dog>()
    toAnimal<Cat>()
}
```
分析
> toAnimal**没有使用 `inline + reified`，所以 `T` 的类型在运行时其实已经被“擦除”了**（Kotlin 和 Java 一样，运行时无法知道泛型类型）
> return currentAnimal as? T 等价于`return currentAnimal as? Animal`，这里as?起不到类型强转失败返回null的效果。
> toAnimal<Cat>()这里会抛异常

解决方案，reified + inline
```kotlin
inline fun <reified T: Animal> toAnimal():T?{
    return currentAnimal as? T
}
```

6. 在构造时调用可能覆盖的方法
```kotlin
open class Animal {
  init { call() }
  open fun call() {}
}
class Cat:Animal(){
  private var name = "jerry"

  override fun call() {
    println("name.length:${name.length}")
  }
}
println(Cat())
```
Cat的call()在父类的init中执行，此时其name还没有初始化，这样访问的name为null,name.length会报空指针

正确的做法: 不要在构造中调用可被复写的方法，初始化完成之后再调用

7. leaking 'this' in constructor
```kotlin
open class SportView : View {
    private val backgroundTintHelper = SportSkinBackGroundHelper(this)
}
```
```text
❓为什么会报这个错：
在 SportView 的构造函数还未执行完之前，你就将 this（还未完全构造好的对象）传给了 SportSkinBackGroundHelper。如果这个 helper 在构造过程中使用了 this，就可能访问未初始化的字段、方法或状态，从而导致崩溃或不可预期行为。

安全的做法：
backgroundTintHelper在SportView构造完之后再进行构造初始化
```
