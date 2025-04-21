package arch.cayenne.lib.common.utils.ext

fun String.getHomeScore(): String {
    return if (this.contains(":")) this.substringBefore(":").trim() else ""
}

fun String.getAwayScore(): String {
    return if (this.contains(":")) this.substringAfter(":").trim() else ""
}
