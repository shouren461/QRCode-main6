 #eventbus
 -keepattributes *Annotation*
 -keepclassmembers class ** {
     @org.greenrobot.eventbus.Subscribe <methods>;
 }
 -keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keep class com.google.android.gms.vision.Frame {*;}
-keep class com.google.android.gms.vision.Frame$Builder {*;}
-keep class com.google.android.gms.vision.barcode.Barcode {*;}
-keep class com.google.android.gms.vision.barcode.BarcodeDetector {*;}
-keep class com.google.android.gms.vision.barcode.BarcodeDetector$Builder {*;}