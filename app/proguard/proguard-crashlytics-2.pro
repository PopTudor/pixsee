# Crashlytics 2.+
-keepattributes SourceFile, LineNumberTable, *Annotation*
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**

# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception

# For Fabric to properly de-obfuscate your crash reports, you need to remove this line from your ProGuard config:
# -printmapping mapping.txt