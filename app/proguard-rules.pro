-keepattributes Signature,InnerClasses,EnclosingMethod
-keep class ua.study.smartgrid.domain.Measurement { *; }
-keep class ua.study.smartgrid.data.LoginRequest { *; }
-keep class ua.study.smartgrid.data.Session { *; }

-keep class ua.study.smartgrid.data.RegistrationRequest { *; }

# Android instrumentation shares tracing classes with the optimized application.
-keep class androidx.tracing.** { *; }

# Preserve shared Kotlin runtime names for instrumentation against release.
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep public application and Compose entry points shared with instrumentation.
-keep,allowoptimization class ua.study.smartgrid.** { *; }
-keep class androidx.compose.** { *; }

-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }
-keep class androidx.activity.** { *; }
