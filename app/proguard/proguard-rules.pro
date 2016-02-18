# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Marked\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-keep class com.qualcomm.ar.**{*;}
-dontwarn com.qualcomm.ar.**
# APACHE
-dontwarn com.apache.**
-dontwarn org.apache.**


-keep class com.marked.**
-keep class com.makeramen.**{*;}
-keep class com.ti.**{*;}
-dontwarn com.ti.**
-keep class com.osterhoutgroup.**{*;}
-keepattributes Signature
-keepattributes Exceptions

# LOGS
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
