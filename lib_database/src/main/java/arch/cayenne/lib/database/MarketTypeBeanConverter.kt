package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.MarketMenuBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MarketTypeBeanConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromStringToList(value: String): List<MarketMenuBean> {
        val type = object : TypeToken<List<MarketMenuBean>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromListToString(value: List<MarketMenuBean>): String {
        return gson.toJson(value)
    }
}