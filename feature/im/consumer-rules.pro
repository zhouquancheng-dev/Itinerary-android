# 网易云信
-dontwarn com.netease.nim.**
-keep class com.netease.nim.** {*;}
-dontwarn com.netease.nimlib.**
-keep class com.netease.nimlib.** {*;}
-dontwarn com.netease.share.**
-keep class com.netease.share.** {*;}
-dontwarn com.netease.mobsec.**
-keep class com.netease.mobsec.** {*;}
# 如果使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}
# 如果开启数据库功能，需要加入
-keep class net.sqlcipher.** {*;}

# 腾讯IM
-keep class com.tencent.imsdk.** { *; }