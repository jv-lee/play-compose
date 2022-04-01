# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.kts.kts.kts.txt.
#
# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
# public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#android 基本组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.view.View

#support / androidx
-dontwarn androidx.appcompat.app.**
-dontwarn androidx.core.app.**
-dontwarn androidx.legacy.**
-keep class com.google.android.** {*;}
-keep class androidx.core.** {*;}
-keep class androidx.appcompat.** {*;}

-keep public class * extends androidx.annotation.**
-keep public class * extends androidx.appcompat.**
-keep class androidx.appcompat.** {*;}

#design 包
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }
-keep public class com.google.android.material.R$* { *; }

#需要反射ViewPager2 mRecyclerView属性 设置混淆过滤
-keep public class androidx.viewpager2.widget.ViewPager2 { *; }

#自定义view
-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class **.R$* {*;}

-keepclassmembers class * extends android.app.Activity{
public void *(android.view.View);
}

-keepclassmembers class * {
void *(*Event);
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
native <methods>;
}

-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
!static !transient <fields>;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

# 不混淆继承viewModel的构造函数，防止ViewModelFactory反射构造错误
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

#Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#AgentWeb
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**


#排除注解类
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }

# 不混淆使用了注解的类及类成员
-keep @com.lee.library.ioc.annotation.** class* {*;}
# 如果类中有使用了注解的方法，则不混淆类和类成员
-keepclasseswithmembers class * {
@com.lee.library.ioc.annotation.** <methods>;
}
# 如果类中有使用了注解的字段，则不混淆类和类成员
-keepclasseswithmembers class * {
@com.lee.library.ioc.annotation.** <fields>;
}
# 如果类中有使用了注解的构造函数，则不混淆类和类成员
-keepclasseswithmembers class * {
@com.lee.library.ioc.annotation.** <init>(...);
}

# The "Signature" attribute is required to be able to access generic types whencompiling in JDK 5.0 and higher.
-keepattributes Signature
# processing Annotations
-keepattributes *Annotation*


#实体类不参与混淆 使用注释@Keep 标记实体类
#-keep class com.lee.playcompose.library.common.entity.** {*;}

#模块服务不参与混淆 (模块服务实现类使用@Keep注解标注)
-keep public class * extends com.lee.playcompose.service.core.IModuleService