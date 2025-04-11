## 1. 换肤介绍

- (1) lib_skin使用自定义View换肤，可换肤的view放在lib_skn下的widget包中,并以Sport开头
- (2) 换肤时可以调用SportSkinManager，进行后缀名区分或者加载assets下的皮肤包
- (3) 使用后缀名换肤对需要换肤的资源添加后缀名 如：white, white_light
``` 
val sportSkinManager:SportSkinManager by inject(SportSkinManager::class.java)
sportSkinManager.loadSkin("light")
```
- (4) 使用皮肤包换肤时，将需要换肤的资源添加到需要打包的项目中，并且资源名与原项目相同，打包后的apk将.apk改成.skin，放入原项目的assets/skins路径下
``` 
val sportSkinManager:SportSkinManager by inject(SportSkinManager::class.java)
sportSkinManager.loadSkinAsset("light.skin")

```
## 2. 使用后缀名换肤如下所示

- (1) 新建res-light并在在对应项目的build.gradle下添加
``` 
sourceSets {
getByName("main"){
res.srcDirs("src/main/res","src/main/res-light")
 }
}
```
- (2) 将对应的TextView改成SportTextView 根据需要有SportImageView,SportLinearLayout等，可根据需要在lib_skin下的wight包中查找对应view或自行添加
```
<com.walisport.lib.skin.widget.SportTextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:textColor="@color/secondary_text"
 />
``` 
- (3) res-light下的value/color中添加需要换肤的颜色并改名为 secondary_text_light  
```
    <color name="secondary_text_light">#232530</color>
```
## 3. 可以根据需求自定义换肤的View

- （1）更新 background -> SportSkinBackgroundHelper ,ImageView -> SportSkinImageHelper, TextView ->SportSkinTextHelper
-  (2) 在自定义view中订阅SportSkinManager 的skinflow，

```
    private val backgroundHelper = SportSkinBackGroundHelper(this)
    private val sportSkinManager:SportSkinManager by inject(SportSkinManager::class.java)
    
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

注:  - 如果是自定义属性，可以lib_skin->res->values->attrs 在对应的styleable中添加自定义属性，并在helper中获取对应的值，updateSkin方法中更新

## 3. 多语言切换

- （1）调用SportSkinManager中的changeLanguage方法

```
    private val sportSkinManager:SportSkinManager by inject(SportSkinManager::class.java)
    sportSkinManager.changeLanguage(Locale.SIMPLIFIED_CHINESE)
```
