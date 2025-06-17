package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.OrderSelectionBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BetSlipTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSelectionList(value: List<OrderSelectionBean>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSelectionList(value: String): List<OrderSelectionBean> {
        val type = object : TypeToken<List<OrderSelectionBean>>() {}.type
        return gson.fromJson(value, type)
    }
}