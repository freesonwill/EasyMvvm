package plugin.koin

import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/4/29 14:59
 * @description:
 */

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.RUNTIME)
annotation class KoinViewModel(
    val binds: Array<KClass<*>> = [],
    val isGets: BooleanArray = []
)
