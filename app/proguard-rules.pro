# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sagar/Android/Sdk/tools/proguard/proguard-android.txt
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


# INITIAL THINGS

-dontwarn android.support.**
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.**
-dontwarn com.parse.**
-dontwarn android.util.**

-keepclassmembers class air.com.dittotv.AndroidZEECommercial.model.** {
    <fields>;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# GOOGLE PLAY SERVICES
#===================================
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
#-----------------------------------

# autovalue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**

# autovalue gson extension
-keep class **.AutoParcelGson_*
-keepnames @auto.parcelgson.AutoParcelGson class *

-dontwarn org.apache.velocity.**
