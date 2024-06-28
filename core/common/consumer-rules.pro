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