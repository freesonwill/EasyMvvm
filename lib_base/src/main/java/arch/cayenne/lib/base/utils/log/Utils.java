package arch.cayenne.lib.base.utils.log;

import android.app.Application;
import android.util.Log;

import java.util.Objects;

public final class Utils {
    private static Application sApp;

    public static Application getApp() {
        if (sApp != null) return sApp;
        init(UtilsBridge.getApplicationByReflect());
        if (sApp == null) throw new NullPointerException("reflect failed.");
        Log.i("Utils", UtilsBridge.getCurrentProcessName() + " reflect app success.");
        return sApp;
    }

    public static void init(final Application app) {
        if (app == null) {
            Log.e("Utils", "app is null.");
            return;
        }
        if (sApp == null) {
            sApp = app;
            UtilsBridge.init(sApp);
            UtilsBridge.preLoad();
            return;
        }
        if (sApp.equals(app)) return;
        UtilsBridge.unInit(sApp);
        sApp = app;
        UtilsBridge.init(sApp);
    }

    /**
     *  是否编辑模式(预览）
     */
    public static boolean isInEditMode(){
        String brand = android.os.Build.BRAND;
        String model = android.os.Build.MODEL;
        String idea = System.getProperty("idea.active");
        //throw new IllegalArgumentException("brand:"+brand+",model:"+model+",idea:"+idea);
        return Objects.equals(brand, "studio");
    }
}
