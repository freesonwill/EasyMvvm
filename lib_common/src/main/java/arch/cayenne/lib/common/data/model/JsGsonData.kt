package arch.cayenne.lib.common.data.model

/**
 * @author: wenxi
 * @date: 11/12/25 17:13
 * @description:
 */
data class JSResponseData(val type:String,val params: JSResponseParam)
data class JSResponseParam(val isExpand:Boolean? = null,val pageName:String? = null,val gameId:Int? = null,val file:String? = null)
