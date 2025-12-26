package arch.cayenne.lib.http.data

/**
 *图片信息
 * @date: 2025/12/11 11:47
 * @description:
 */
data class AvatarVo(
    val url: String ,//图片URL
    val thumbhash: String//ThumbHash (base64)
)