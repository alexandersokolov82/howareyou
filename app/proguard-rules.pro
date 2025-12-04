# Keep serialization classes
-keepclassmembers class ** implements kotlinx.serialization.KSerializer {
    public static ** INSTANCE;
}
