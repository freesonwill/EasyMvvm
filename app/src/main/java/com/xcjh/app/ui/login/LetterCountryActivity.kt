package com.xcjh.app.ui.login

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.CityModel
import com.xcjh.app.databinding.ActivityLettercountryBinding
import com.xcjh.app.databinding.ItemCityBinding
import com.xcjh.app.databinding.ItemCityLetterBinding
import com.xcjh.app.view.SideBarLayout
import com.xcjh.base_lib.Constants.Companion.PHONE_CODE

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
            .navigationBarColor(R.color.c_ffffff)
            .navigationBarDarkIcon(true)
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
                    binding.name1.text = matchBeanNew.cnname
                    Glide.with(context).load(matchBeanNew.label).override(100).thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true)
                        .into(binding.ivgq)
                    binding.code.text = matchBeanNew.code
                    binding.name1.setOnClickListener {
                        PHONE_CODE = matchBeanNew.code
                        finish()
                    }
                    binding.code.setOnClickListener {
                        PHONE_CODE = matchBeanNew.code
                        finish()
                    }
                }
            }


        }
        //    initMaps()
        mDatabind.indexBar.setSideBarLayout(SideBarLayout.OnSideBarLayoutListener { word -> //根据自己业务实现
            var ss = CityModel.CityLetter(word)
            val indexOf = mDatabind.rec.models?.indexOf(ss) ?: -1
            if (indexOf != -1) {
                (mDatabind.rec.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    indexOf,
                    0
                )
            }
        })
        mViewModel.getCountrys()
    }

    override fun createObserver() {

        mViewModel.countrys.observe(this) {
            if (it.isNotEmpty()) {

                val locations = it
                val gson = Gson()
                val initialLocations = locations.groupBy { it.shortName }
                    .map { (initial, list) ->
                        CityModel(initial, list.map { location ->
                            CityModel.City(
                                "+" + location.dialingCode,
                                location.cn,
                                location.en,
                                location.shortName,
                                location.icon
                            )
                        })
                    }
                initialLocations.size
                val sortedCities = initialLocations.sortedBy { it.initial }
                sortedCities.forEach {
                    models.add(CityModel.CityLetter(it.initial)) // 转换为支持悬停的数据模型
                    models.addAll(it.list)
                }

                mDatabind.rec.models = models
                mDatabind.indexBar.setNewLetter(mLetters.toMutableList())


            } else {

            }

        }

    }

    private fun initMaps() {
        // 解析Json数据
//        val newstringBuilder = StringBuilder()
//        var inputStream: InputStream? = null
//        try {
//            inputStream = resources.assets.open("JHAreaCode.json")
//            val isr = InputStreamReader(inputStream)
//            val reader = BufferedReader(isr)
//            var jsonLine: String?
//            while (reader.readLine().also { jsonLine = it } != null) {
//                newstringBuilder.append(jsonLine)
//            }
//            reader.close()
//            isr.close()
//            inputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            // LogUtil.("得到数据chuck==$e")
//        }
//        val str = newstringBuilder.toString()
//        str.length
//        //LogUtil.d("得到数据==$str")
//        val gson = Gson()
//        val locations = gson.fromJson(str, Array<LetterBeann>::class.java)
//        str.length
//        val initialLocations = locations.groupBy { getPinyinFirstLetter(it.name) }
//            .map { (initial, list) ->
//                CityModel(initial, list.map { location ->
//                    var county = getGQ(location.abbreviate)
//                    CityModel.City(
//                        "+" + location.areaCode,
//                        county + "  " + location.name,
//                        "",
//                        "${location.name}${location.areaCode}"
//                    )
//                })
//            }
//        initialLocations.size
//        val sortedCities = initialLocations.sortedBy { it.initial }
//        sortedCities.forEach {
//            models.add(CityModel.CityLetter(it.initial)) // 转换为支持悬停的数据模型
//            models.addAll(it.list)
//        }

//        mDatabind.rec.models = models
//        mDatabind.indexBar.setNewLetter(mLetters.toMutableList())


    }

//    fun getFirstChar(chineseString: String): String {
//        val segment = chineseToPinyin(chineseString)
//        val firstChar = segment[0]
//        return firstChar.toUpperCase().toString()
//    }
//
//    fun chineseToPinyin(chineseString: String): String {
//        val pinyinList: MutableList<Pinyin> = HanLP.convertToPinyinList(chineseString)
//        val stringBuilder = StringBuilder()
//        for (pinyin in pinyinList) {
//            stringBuilder.append(pinyin.pinyinWithoutTone)
//        }
//        return stringBuilder.toString()
//    }

//    fun getPinyinFirstLetter(chineseString: String): String {
//
//        return getFirstChar(chineseString)
//
//    }

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