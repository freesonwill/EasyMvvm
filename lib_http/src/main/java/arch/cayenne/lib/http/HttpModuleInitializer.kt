package arch.cayenne.lib.http

import android.content.Context
import arch.cayenne.lib.base.BuildConfig
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.BASE_URL
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.http.interceptor.HeaderInterceptor
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
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
            single(named("wnlApi")) {  HttpClient.Builder("http://co-api.51wnl.com",5000).build()  }
            single(named("3n1")) {  HttpClient.Builder("https://betwavepro.ja700.com/",5000).build()  }
            single(named("mock")) {
                HttpClient.Builder("$BASE_URL/" ,5000)
                    .addInterceptor(HeaderInterceptor(get()))
                    .build()
            }
        })
        return TAG
    }

}