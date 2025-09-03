package arch.cayenne.lib.common.data.constants

sealed class LoginEnum(val code: Int) {
    object SUCCESSFUL:LoginEnum(-1)
    object NOT_SUCCESSFUL:LoginEnum(1)
    data class API_FAILURE(val error:String?):LoginEnum(2)
}
