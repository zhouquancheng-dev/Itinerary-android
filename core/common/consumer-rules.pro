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