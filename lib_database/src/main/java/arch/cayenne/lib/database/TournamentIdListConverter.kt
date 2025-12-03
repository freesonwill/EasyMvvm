package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TournamentIdListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromStringToList(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromListToString(value: List<Int>): String {
        return gson.toJson(value)
    }
}