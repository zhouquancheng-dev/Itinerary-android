## --------------Begin: proguard configuration for Retrofit------------##
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
# Retrofit会反射泛型参数。InnerClasses需要使用Signature，EnclosingMethod需要使用InnerClasses。
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
# Retrofit会反射方法和参数注解。
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
# 保留注解默认值（例如，retrofit2.http.Field.encoded）。
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
# 在优化时保留服务方法参数。
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
# 忽略用于构建工具的注解。
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
# 忽略用于嵌入可空性信息的JSR 305注解。
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
# 被NoClassDefFoundError try/catch保护并且只有在类路径上时才使用。
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
# 只能被Kotlin使用的顶级函数。
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
# 由于Retrofit接口是通过Proxy创建的，因此没有子类型，显式保留接口以防止R8替换所有潜在值为null。
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
# 保留继承的服务。
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
# 使用R8全模式时，对于未保留的类，会去除通用签名。挂起函数会包装在使用的类型参数的继续体中。
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
# R8全模式在未保留的类上去除通用签名。
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
# 使用R8全模式时，对于未保留的类，会去除通用签名。
-keep,allowobfuscation,allowshrinking class retrofit2.Response
## --------------End: proguard configuration for Retrofit--------------##


## --------------Begin: proguard configuration for okhttp3------------##
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
## --------------End: proguard configuration for okhttp3--------------##


## --------------Begin: proguard configuration for kotlinx.serialization------------##
#-keep, allowobfuscation, allowoptimization, allowaccessmodification @kotlinx.serialization.Serializable class *
# 保留所有带有 @Serializable 注解的类
-keep @kotlinx.serialization.Serializable class * { *; }

# 保留 kotlinx.serialization 库中的所有类
-keep class kotlinx.serialization.** { *; }

# 保留生成的序列化器类
-keepnames class * extends kotlinx.serialization.KSerializer { *; }

# 保留 Kotlin 编译器生成的元数据
-keepclassmembers class kotlin.Metadata { *; }

# 保留带有 @SerialName 注解的字段
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# 保留内部伴生对象
-keepclassmembers class **$Companion {
    public static ** INSTANCE;
}

# 保留带有 @Polymorphic 注解的类和子类
-keepclassmembers class ** {
    @kotlinx.serialization.Polymorphic <fields>;
}

# 保留所有嵌套类的序列化字段
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}
## --------------End: proguard configuration for kotlinx.serialization------------##