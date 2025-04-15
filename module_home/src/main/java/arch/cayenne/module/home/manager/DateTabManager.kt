package arch.cayenne.module.home.manager

class DateTabManager {
    private val selectedDateIndexMap = mutableMapOf<Int, Int>() // leagueId to date tab index

    fun setSelectedIndex(leagueId: Int, index: Int) {
        selectedDateIndexMap[leagueId] = index
    }

    fun getSelectedIndex(leagueId: Int): Int {
        return selectedDateIndexMap[leagueId] ?: 0
    }

    fun getDateString(leagueId: Int, apiDates: List<Pair<String, String>>): String {
        val selectedIndex = getSelectedIndex(leagueId)
        return if (selectedIndex == 0) "" else apiDates.getOrNull(selectedIndex - 1)?.first.orEmpty()
    }
}
