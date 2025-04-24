package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VideoSourceBeanConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromStringToList(value: String): List<VideoSourceBean> {
        val type = object : TypeToken<List<VideoSourceBean>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromListToString(value: List<VideoSourceBean>): String {
        return gson.toJson(value)
    }
}