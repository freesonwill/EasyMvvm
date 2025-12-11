package arch.cayenne.lib.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromIntLongMap(map: Map<Int, Long>?): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toIntLongMap(json: String?): Map<Int, Long> {
        if (json.isNullOrEmpty()) return emptyMap()
        val type = object : TypeToken<Map<Int, Long>>() {}.type
        return gson.fromJson(json, type)
    }
}