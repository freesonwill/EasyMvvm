package arch.cayenne.lib.database

import androidx.room.TypeConverter
import arch.cayenne.lib.database.entity.WalletBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WalletListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromWalletList(value: List<WalletBean>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWalletList(value: String): List<WalletBean> {
        val type = object : TypeToken<List<WalletBean>>() {}.type
        return gson.fromJson(value, type)
    }
}