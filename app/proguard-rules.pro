# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# API-Client.begin
-keep public class com.aliyun.iot.aep.sdk.apiclient.** {
    public <methods>;
    public <fields>;
}

-keep public class com.aliyun.iot.aep.sdk.** {
    public <methods>;
    public <fields>;
}

-keep class com.aliyun.alink.linksdk.channel.**{*;}
# API-Client.end
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keepclasseswithmembernames class ** {
    native <methods>;
}

-keepattributes Signature

-keep class sun.misc.Unsafe { *; }

-keep class com.taobao.** {*;}

-keep class com.alibaba.** {*;}

-keep class com.alipay.** {*;}

-keep class com.ut.** {*;}

-keep class com.ta.** {*;}

-keep class anet.**{*;}

-keep class anetwork.**{*;}

-keep class org.android.spdy.**{*;}

-keep class org.android.agoo.**{*;}

-keep class android.os.**{*;}

-dontwarn com.taobao.**

-dontwarn com.alibaba.**

-dontwarn com.alipay.**

-dontwarn anet.**

-dontwarn org.android.spdy.**

-dontwarn org.android.agoo.**

-dontwarn anetwork.**

-dontwarn com.ut.**

-dontwarn com.ta.**

-keep public class com.aliyun.alink.business.devicecenter.** {*;}

-keep public class com.aliyun.alink.linksdk.alcs.coap.**{*;}

-keep public class com.alibaba.sdk.android.**{*;}

-keep public class com.aliyun.iot.aep.sdk.credential.** {
    public <methods>;
    public <fields>;
}

-keep public class com.aliyun.iot.** {
    public <methods>;
    public <fields>;
}

-keep public class com.aliyun.iot.aep.sdk.apiclient.* {
    public <methods>;
    public <fields>;
}

-keep class com.facebook.**{*;}
# react-native.end
-keep  class * implements com.facebook.react.bridge.NativeModule {
    public <methods>;
    protected <methods>;
}

# keep view manager
-keep  class * extends com.facebook.react.uimanager.ViewManager {
    public <methods>;
    protected <methods>;
}

# keep js module
-keep  class * extends com.facebook.react.bridge.JavaScriptModule {
    public <methods>;
}

# keep shadow node
-keep class * extends com.facebook.react.uimanager.ReactShadowNode{
    public <methods>;
    protected <methods>;
}

# keep ReactPackage
-keep class * implements com.facebook.react.ReactPackage{
    public <methods>;
}

# keep ReactPackage
-keep class * implements com.aliyun.iot.ilop.page.**{
    public <methods>;
}

# keep interface class
-keep class com.aliyun.alink.alirn.RNContainer{
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.RNContainerConfig{
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.RNGlobalConfig{
    public <methods>;
    public <fields>;
}

# BoneDevHelper
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$RouterInfo{
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$BoneBundleInfo{
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$OnBondBundleInfoGetListener{
   public <methods>;
}

# cache
-keep public class com.aliyun.alink.alirn.cache.*{
    public <methods>;
}

# launch
-keep class com.aliyun.alink.alirn.launch.LaunchOptionsFactory{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.launch.OnLoadingStatusChangedListener{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.launch.LoadingStatus{
    public <fields>;
}

#preload
-keep class com.aliyun.alink.alirn.preload.*{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.preload.*{
    public <methods>;
}

-keep public class com.aliyun.alink.alirn.preload.sdk.*{
    public <methods>;
    public <fields>;
}

# biz package
-keep class com.aliyun.alink.alirn.rnpackage.biz.BizPackageHolder{
    public <methods>;
}

# ut
-keep class com.aliyun.alink.alirn.usertracker.*{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.usertracker.*{
    public <methods>;
}

#utils
-keep class com.aliyun.alink.alirn.utils.*{
    public <methods>;
}
# component-rncontainer.end

# for BoneBridge @ Start

# keep bone api
-keep  class * extends com.aliyun.alink.sdk.jsbridge.methodexport.BaseBonePlugin {
     @com.aliyun.alink.sdk.jsbridge.methodexport.MethodExported <methods>;
     public <fields>;
}
-keep public class com.aliyun.iot.aep.sdk.bridge.base.BaseBoneService{
    private boolean isBoneInit;
}
# for BoneBridge @ End

# for BundleManager @ begin
-keep class com.aliyun.iot.aep.component.bundlemanager.bean.*{*;}
-keep class com.aliyun.iot.aep.component.bundlemanager.BundleManager{*;}
# for BundleManager @ end

# for Router
 -keep class com.aliyun.iot.aep.routerexternal.* {
     public <methods>;
     public <fields>;
 }
-keep class com.aliyun.alink.page.rn.* {
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.*{
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.page.rn.router.RouterManager$RouterData{
    public <fields>;
}

-keep class com.aliyun.alink.linksdk.tmp.**{*;}

-keep class com.aliyun.alink.linksdk.cmp.**{*;}

-keep class com.aliyun.alink.linksdk.alcs.**{*;}

-keep class com.aliyun.iot.ble.**{*;}

-keep class com.aliyun.iot.breeze.**{*;}

# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 忽略警告
-ignorewarnings

# 设置是否允许改变作用域
-allowaccessmodification

# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames

# apk 包内所有 class 的内部结构
-dump class_files.txt

# 未混淆的类和成员
-printseeds seeds_txt

# 列出从apk中删除的代码
-printusage unused.txt

# 混淆前后的映射
-printmapping mapping.txt

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.view.View

-keepattributes *Annotation*

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepattributes *JavascriptInterface*

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements java.io.Serializable {
    public *;
}

-keep class * implements androidx.appcompat.widget.AppCompatImageView {
    public *;
}

-keep class * implements android.view.View {
    public *;
}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}

-keep class * implements com.alibaba.sdk.android.openaccount.ui.widget.LinearLayoutTemplate {
    public *;
}

-keep class com.alibaba.sdk.android.openaccount.** {*;}

-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

-keep class com.google.gson.** {*;}
-keep class sun.misc.Unsafe.** {*;}
-keep class com.google.gson.stream** {*;}
-keep class com.google.gson.examples.android.model.* {*;}
-keep class com.google.* {
    <fields>;
    <methods>;
}
-dontwarn com.google.gson.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}