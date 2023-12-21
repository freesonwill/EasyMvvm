package com.xcjh.app.ui.login

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.dictionary.py.Pinyin
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.CityModel
import com.xcjh.app.bean.FriendListBean
import com.xcjh.app.bean.InitialLocation
import com.xcjh.app.bean.LetterBeann
import com.xcjh.app.bean.Location
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivityLettercountryBinding
import com.xcjh.app.databinding.ItemChatPicRightBinding
import com.xcjh.app.databinding.ItemCityBinding
import com.xcjh.app.databinding.ItemCityLetterBinding
import com.xcjh.app.databinding.ItemSchAllBinding
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.SideBarLayout
import com.xcjh.base_lib.Constants.Companion.PHONE_CODE
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/***
 * 选择国家和地区
 */

class LetterCountryActivity : BaseActivity<LoginVm, ActivityLettercountryBinding>() {
    private val models = mutableListOf<Any>()
    private val mLetters = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "#"
    )
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.str_choosecountry)

        mDatabind.rec.setup {

            addType<CityModel.CityLetter>(R.layout.item_city_letter)
            addType<CityModel.City>(R.layout.item_city)
            onBind {
                if (_data is CityModel.CityLetter) {
                    var binding = getBinding<ItemCityLetterBinding>()
                    var matchBeanNew = _data as CityModel.CityLetter
                    binding.tvname.text = matchBeanNew.letter
                } else {
                    var binding = getBinding<ItemCityBinding>()
                    var matchBeanNew = _data as CityModel.City
                    binding.name1.text = matchBeanNew.name
                    binding.code.text = matchBeanNew.code
                    binding.name1.setOnClickListener {
                        PHONE_CODE =matchBeanNew.code
                        finish()
                    }
                    binding.code.setOnClickListener {
                        PHONE_CODE =matchBeanNew.code
                        finish()
                    }
                }
            }


        }
        initMaps()
        mDatabind.indexBar.setSideBarLayout(SideBarLayout.OnSideBarLayoutListener { word -> //根据自己业务实现
            var ss=CityModel.CityLetter(word)
            val indexOf = mDatabind.rec.models?.indexOf(ss) ?: -1
            if (indexOf != -1) {
                (mDatabind.rec.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(indexOf, 0)
            }
        })
    }


    private fun initMaps() {
        // 解析Json数据
        val newstringBuilder = StringBuilder()
        var inputStream: InputStream? = null
        try {
            inputStream = resources.assets.open("JHAreaCode.json")
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var jsonLine: String?
            while (reader.readLine().also { jsonLine = it } != null) {
                newstringBuilder.append(jsonLine)
            }
            reader.close()
            isr.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // LogUtil.("得到数据chuck==$e")
        }
        val str = newstringBuilder.toString()
        str.length
        //LogUtil.d("得到数据==$str")
        val gson = Gson()
        val locations = gson.fromJson(str, Array<LetterBeann>::class.java)

        val initialLocations = locations.groupBy { getPinyinFirstLetter(it.name) }
            .map { (initial, list) ->
                CityModel(initial, list.map { location ->
                    var county = getGQ(location.abbreviate)
                    CityModel.City(
                        "+" + location.areaCode,
                        county + "  " + location.name,
                        "",
                        "${location.name}${location.areaCode}"
                    )
                })
            }
        val sortedCities = initialLocations.sortedBy { it.initial }
        sortedCities.forEach {
            models.add(CityModel.CityLetter(it.initial)) // 转换为支持悬停的数据模型
            models.addAll(it.list)
        }

        mDatabind.rec.models = models
        mDatabind.indexBar.setNewLetter(mLetters.toMutableList())


    }

    fun getFirstChar(chineseString: String): String {
        val segment = chineseToPinyin(chineseString)
        val firstChar = segment[0]
        return firstChar.toUpperCase().toString()
    }

    fun chineseToPinyin(chineseString: String): String {
        val pinyinList: MutableList<Pinyin> = HanLP.convertToPinyinList(chineseString)
        val stringBuilder = StringBuilder()
        for (pinyin in pinyinList) {
            stringBuilder.append(pinyin.pinyinWithoutTone)
        }
        return stringBuilder.toString()
    }

    fun getPinyinFirstLetter(chineseString: String): String {

        return getFirstChar(chineseString)

    }

    fun getGQ(country: String): String {
        try {
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar =
                Character.codePointAt(country, 0) - asciiOffset + flagOffset
            val secondChar =
                Character.codePointAt(country, 1) - asciiOffset + flagOffset
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        } catch (e: Exception) {
            return ""
        }

    }
}