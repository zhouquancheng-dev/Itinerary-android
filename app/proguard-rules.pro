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

#--------------------基本配置开始--------------------------#
# Uncomment this to preserve the line number information for
# debugging stack traces.
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
# 将文件来源重命名为 “SourceFile” 字符串
-renamesourcefileattribute SourceFile

# 忽略警告（慎用）
#-ignorewarnings
# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
#-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
#-dontskipnonpubliclibraryclassmembers

# 混淆时记录日志(打印混淆的详细信息)
# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

#混淆时所采用的算法(谷歌推荐算法)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保持代码中的Annotation不被混淆
-keepattributes *Annotation*
# 保持泛型不被混淆, 这在JSON实体映射时非常重要
-keepattributes Signature
#保持反射不被混淆
-keepattributes EnclosingMethod
#保持异常不被混淆
-keepattributes Exceptions
#保持内部类不被混淆
-keepattributes Exceptions, InnerClasses

# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
#---------------------基本配置结束---------------------#