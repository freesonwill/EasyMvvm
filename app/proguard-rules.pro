# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预检验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# 忽略警告
-ignorewarnings

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 保留Annotation不混淆
-keepattributes Exceptions,*Annotation*,InnerClasses,Deprecated,EnclosingMethod

-renamesourcefileattribute TbsSdkJava
# 避免混淆泛型
-keepattributes Signature

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 混淆映射，生成映射文件================================
# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系 ，然后使用printmapping指定映射文件的名称
#-verbose
#-printmapping proguardMapping.txt
##输出apk包内所有的class的内部结构
#-dump dump.txt
##未混淆的类和成员
#-printseeds seeds.txt
##列出从apk中删除的代码
#-printusage unused.txt
# 混淆映射，生成映射文件=================================


#############################################
# Android开发中一些需要保留的公共部分
#############################################
# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-keep interface android.support.** { *; }
-dontwarn android.support.**
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}
# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}


-dontwarn com.cmic.sso.sdk.**
-dontwarn com.unikuwei.mianmi.account.shield.**
-dontwarn com.sdk.**
-keep class com.cmic.sso.sdk.**{*;}
-keep class com.sdk.** { *;}
-keep class com.unikuwei.mianmi.account.shield.** {*;}
-keep class cn.com.chinatelecom.account.api.**{*;}

-keep class com.tencent.** { *; }

-keep class com.gyf.immersionbar.* {*;}
-dontwarn com.gyf.immersionbar.**

-keep class com.fuxin.roomplayer.data.**{*;}
#kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
################common###############




-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
################glide###############
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

################retrofit###############
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
################RxPermissions#################
-keep class com.tbruyelle.rxpermissions2.** { *; }
-keep interface com.tbruyelle.rxpermissions2.** { *; }

-dontwarn com.kingja.loadsir.**
-keep class com.kingja.loadsir.** {*;}

-keep class com.just.agentweb.** {*;}
-dontwarn com.just.agentweb.**

-keep class **.*_SnakeProxy

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.xcjh.app.bean.**{ *; }
-keep class res.anim.** { *; }
-keep class com.xcjh.app.websocket.bean.**{ *; }
-keep class com.xcjh.base_lib.network.BaseResponse { *; }
-keep class com.xcjh.base_lib.bean.** { *; }

-keep class com.xcjh.app.view.** { *; }
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#日历
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keep class com.haibin.calendarview.** { *; }
################ ViewBinding & DataBinding ###############
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
  public static * bind(android.view.View);
}
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

-keep class com.luck.picture.lib.** { *; }

#如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

# 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#=================
-dontwarn com.king.app.dialog.**
-keep class com.king.app.dialog.**{ *;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn com.king.app.updater.**
-keep class com.king.app.updater.**{ *;}
-keep class * extends com.king.app.updater.**{ *;}
-keep class * implements com.king.app.updater.**{ *;}

#==========推送
#Engagelab
-keep class com.engagelab.** {*;}
-dontwarn com.engagelab.**
#huawei
-keep class com.google.**{*;}
-dontwarn com.google.**

#xiaomi
-keep class com.xiaomi.** {*;}
-dontwarn com.xiaomi.**

#meizu，如果使用R8混淆，可能会导致拿不到token
-keep class com.meizu.** {*;}
-dontwarn com.meizu.**

#oppo
-keep class com.heytap.** {*;}
-dontwarn com.heytap.**

#vivo
-keep class com.vivo.** {*;}
-dontwarn com.vivo.**


-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.hihonor.push.**{*; }
 #==========推送
#XPopup
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}
#==========
#====播放器

-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class com.shuyu.gsyvideoplayer.player.** {*;}
-dontwarn com.shuyu.gsyvideoplayer.player.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class androidx.media3.** {*;}
-keep interface androidx.media3.**

-keep class com.shuyu.alipay.** {*;}
-keep interface com.shuyu.alipay.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#==========
-keep class com.connectsdk.**       { * ; }

#多语言
-keep class com.hjq.language.** {*;}
#权限控制
-keep class com.hjq.permissions.** {*;}

#===
-keep class com.kongzue.dialogx.** { *; }
-dontwarn com.kongzue.dialogx.**

# 额外的，建议将 android.view 也列入 keep 范围：
-keep class android.view.** { *; }

# 若启用模糊效果，请增加如下配置：
-dontwarn androidx.renderscript.**
-keep public class androidx.renderscript.** { *; }