# Toaster
-keep class com.hjq.toast.** {*;}

# 腾讯 bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# 极验验证码SDK
-dontwarn com.geetest.captcha.**
-keep class com.geetest.captcha.**{*;}

# 阿里云 EMAS SDK
-keep class com.alibaba.sdk.android.**{*;}

# 阿里云认证服务 图形验证码
-dontwarn com.geetest.gtcaptcha4.alicom.**
-keep class com.geetest.gtcaptcha4.alicom.**{*;}

# 阿里云Captcha
-dontwarn com.geetest.gtcaptcha4.alicom.**
-keep class com.geetest.gtcaptcha4.alicom.** { *; }
-dontwarn com.alicom.gtcaptcha4.**
-keep class com.alicom.gtcaptcha4.**$Companion { *; }

-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass
-dontwarn java.lang.Module
-dontwarn java.lang.reflect.AnnotatedType
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.ahocorasick.trie.Emit
-dontwarn org.ahocorasick.trie.Trie$TrieBuilder
-dontwarn org.ahocorasick.trie.Trie
-dontwarn org.graalvm.nativeimage.hosted.Feature$BeforeAnalysisAccess
-dontwarn org.graalvm.nativeimage.hosted.Feature
-dontwarn org.graalvm.nativeimage.hosted.RuntimeResourceAccess

# 阿里云 OSS SDK
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**
-keep class org.apache.commons.**{ *;}
-keep class com.alibaba.sdk.*{ *;}

# PictureSelector
-keep class com.luck.picture.lib.** { *; }
-keep class com.luck.lib.camerax.** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# 高德定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

# Coil3
-dontwarn coil3.PlatformContext