package arch.cayenne.lib.base.utils.log;


import android.app.Application;


/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2020/03/19
 *     desc  :
 * </pre>
 */
class UtilsBridge {

    static Application getApplicationByReflect() {
        return UtilsActivityLifecycleImpl.INSTANCE.getApplicationByReflect();
    }
    static void init(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.init(app);
    }
    static void unInit(Application app) {
        UtilsActivityLifecycleImpl.INSTANCE.unInit(app);
    }
    static void preLoad() { preLoad(AdaptScreenUtils.getPreLoadRunnable()); }
    static String getCurrentProcessName() {
        return ProcessUtils.getCurrentProcessName();
    }

    private static void preLoad(final Runnable... runs) {
        for (final Runnable r : runs) {
            ThreadUtils.getCachedPool().execute(r);
        }
    }
}
