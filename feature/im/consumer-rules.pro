# 腾讯 IM SDK
-keep class com.tencent.imsdk.** { *; }

# 避免混淆 TUIKit
-keep class com.tencent.qcloud.** { *; }
# 避免删除代码逻辑
-dontshrink
-dontoptimize