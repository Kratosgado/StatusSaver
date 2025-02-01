
-keep class com.kratosgado.statussaver.ui.** { *; }
-keep @androidx.compose.material3.ExperimentalMaterial3Api class *
-keep class * extends androidx.compose.ui.graphics.vector.ImageVector
# Keep ViewModels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Parcelables
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
# ========= Hilt =========
-keep class com.kratosgado.statussaver.Hilt_* { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keepclassmembers class * {
    @dagger.hilt.* *;
}

# ========= Jetpack Compose =========
-keep @androidx.compose.runtime.Composable class *

# ========= ExoPlayer/Media3 =========
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ========= Coil =========
-keep class coil.** { *; }
-keep class coil.request.ImageRequest

# ========= Navigation Component =========
-keep class androidx.navigation.** { *; }
#-keepclassmembers class * extends androidx.navigation.fragment.NavHostFragment {
#    public <init>();
#}

# ========= DataStore =========
-keep class androidx.datastore.** { *; }

# ========= WorkManager =========
-keep class androidx.work.** { *; }

# ========= General AndroidX =========
-keep class androidx.lifecycle.** { *; }
-keep class androidx.fragment.** { *; }

# AdMob
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }
-keep class com.google.android.gms.common.GooglePlayServicesUtil { *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient { *; }