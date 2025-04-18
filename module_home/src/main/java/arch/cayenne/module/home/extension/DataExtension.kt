package arch.cayenne.module.home.extension

fun String.getHomeScore(): String {
    return if (this.contains(":")) this.substringBefore(":").trim() else ""
}

fun String.getAwayScore(): String {
    return if (this.contains(":")) this.substringAfter(":").trim() else ""
}
