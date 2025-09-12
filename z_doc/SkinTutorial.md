## 1. 换肤介绍

- (1) lib_skin使用自定义View换肤，可换肤的view放在lib_skn下的widget包中,并以Sport开头
- (2) 换肤时可以调用SportSkinManager，进行后缀名区分或者加载assets下的皮肤包
- (3) 使用后缀名换肤对需要换肤的资源添加后缀名 如：white, white_light
``` 
val sportSkinManager:SkinnableManager by inject(SkinnableManager::class.java)
sportSkinManager.loadSkin("light")
```
- (4) 使用皮肤包换肤时，将需要换肤的资源添加到需要打包的项目中，并且资源名与原项目相同，打包后的apk将.apk改成.skin，放入原项目的assets/skins路径下
``` 
val sportSkinManager:SkinnableManager by inject(SkinnableManager::class.java)
sportSkinManager.loadSkinAsset("light.skin")
```
## 2.lib_skin建议使用示例
- (1) 如果app存在light 和 dark两种皮肤模式，建议使用任意一种作为默认的皮肤模式。以下使用light作为默认的模式
- (2) light模式的皮肤后缀名称为空字符串，dark模式的皮肤后缀名称为_dark
- (3) light模式的所有资源文件放在res文件夹下，dark资源文件放入res-dark,新建main/res-dark并在在对应项目的build.gradle下添加
``` 
sourceSets {
    getByName("main"){
        res.srcDirs("src/main/res","src/main/res-dark")
    }
}
```
- (4) res-dark下的value/color中添加需要换肤的颜色并改名为 secondary_text_dark
```
<color name="secondary_text_dark">#232530</color>
```
- (5) 将对应的TextView改成SportTextView 根据需要有SportImageView,SportLinearLayout等，可根据需要在lib_skin下的wight包中查找对应view或自行添加

xml下
```
 <arch.cayenne.lib.skin.widget.SkinnableTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textColor="@color/secondary_text"
    android:background="@drawable/card_background"
 />
 ```
代码设置：如果需要跟随皮肤自动变化使用setTextColorRes setBackgroundResource，不需要则直接使用setTextColor setBackground
```
 tv.setTextColorRes(R.color.secondary_text) 
 tv.setBackgroundResource(R.drawable.card_background) 
```
- (6) 如果使用普通的view，可以调用SkinnableResourceManager.getDrawable ,getColor,getTargetResourceId使用
```
 tv.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(R.drawable.card_background)) 
```

- (7) 切换皮肤
切换light
```
 skinnableManager.loadSkin("")
```
切换dark
```
 skinnableManager.loadSkin("dark")
```


## 3. 可以根据需求自定义换肤的View

- (1) 更新 background -> SportSkinBackgroundHelper ,ImageView -> SportSkinImageHelper, TextView ->SportSkinTextHelper  
- (2) 在自定义view中订阅SportSkinManager 的skinflow，

```
private val backgroundHelper = SportSkinBackGroundHelper(this)
private val sportSkinManager:SkinnableManager by inject(SkinnableManager::class.java)

override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    this.findViewTreeLifecycleOwner()?.lifecycleScope?.apply {
        launch {
            sportSkinManager.skinFlow.collect {
                backgroundHelper.updateSkin()
            }
        }
    }
}
```

## 4. 多语言切换

- （1）调用SportSkinManager中的changeLanguage方法

```
private val languageManager:LanguageManager by inject(LanguageManager::class.java)
languageManager.changeLanguage(Locale.SIMPLIFIED_CHINESE)
```
- （2）动态创建的TextView设置多语言

```
SkinnableTextView setTextRes
SkinnableEditText setTextHitRes
SkinnableButton setTextRes
```

## 5. 使用_suffixes.gradle.kts 对res-suffix下drawable mipmap 文件 colors里的name自动添加后缀

- (1) 将app下或module下的build.gradle中添加 
``` 
apply(from = rootProject.file("gradle/_suffixes.gradle.kts"))
```
- (2) 对特定module下的资源文件添加后缀
``` 
./gradlew :module_live:addResourceSuffixes
```

## 6. 使用_duplicate_color_name.gradle.kt 检查每个module中的color name 不与其他模块重复
``` 
./gradlew checkDuplicateColorNames   
```

注: 
- 如果是自定义属性，可以lib_skin->res->values->attrs 在对应的styleable中添加自定义属性，并在helper中获取对应的值，updateSkin方法中更新
- skinnableFontWeight,在SKinnableTextView SkinnableButton SkinnableEditext中新增skinnableFontWeight "app:skinnableFontWeight=500 " 设置默认字体下的fontWeight属性
- Tablayout的 tabBackground属性设置了以后会在换肤后造成阴影，SportTablayout使用sportTabBackground替换tabBackground
- SkinnableTablayout 新增setTabResArray 及时更新tab.text切换语言 ，如果时customView需要监听languageManager.languageFlow自己做切换
- 动态创建Skinnable TextView Button EditText 时，设置textColor setTextColorRes
- SkinnableManager.fitterSuffixSkin 当传入带皮肤后缀的资源id时找到原始的资源id。 由于闪电项目在setBackgroundResource setImageResource时传入了带有皮肤后缀的资源id，因此调用fitterSuffixSkin,传入所有的皮肤后缀名，比如_light 传light，后续调用
setBackgroundResource setImageResource等时会过滤到带有的后缀。

