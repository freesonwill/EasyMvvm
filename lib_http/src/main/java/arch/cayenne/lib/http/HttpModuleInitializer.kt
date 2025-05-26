package arch.cayenne.lib.http

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module


/**
 * @author: zhangsan
 * @date: 2025/5/26 12:35
 * @description: HttpModule模块初始化入口
 */
class HttpModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        "$TAG create ....".logd(TAG)
        loadKoinModules(module {
            single {  HttpClient.Builder("http://co-api.51wnl.com",5000).build()  }
        })
        return TAG
    }

}