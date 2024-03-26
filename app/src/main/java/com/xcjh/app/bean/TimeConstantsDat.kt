package com.xcjh.app.bean

class TimeConstantsDat {
    companion object {
        //省  年
          var options1Items: List<JsonBean> = ArrayList()
        //市   月
          var options2Items =  ArrayList<ArrayList<String>>()
        //去   日
          var options3Items = ArrayList< ArrayList<ArrayList<String>>>()

        //全部比赛 0全部 1 是足球   2是篮球    3是赛果
        var  options1ItemsAll: List<JsonBean> = ArrayList()
        var options2ItemsAll =  ArrayList<ArrayList<String>>()
        var options3ItemsAll = ArrayList< ArrayList<ArrayList<String>>>()
        //足球比赛
        var   options1ItemsFootball: List<JsonBean> = ArrayList()
        var options2ItemsFootball =  ArrayList<ArrayList<String>>()
        var options3ItemsFootball = ArrayList< ArrayList<ArrayList<String>>>()
        //篮球比赛
        var   options1ItemsBasketball: List<JsonBean> = ArrayList()
        var options2ItemsBasketball =  ArrayList<ArrayList<String>>()
        var options3ItemsBasketball = ArrayList< ArrayList<ArrayList<String>>>()
        //赛果
        var   options1ItemsSaiguo: List<JsonBean> = ArrayList()
        var options2ItemsSaiguo =  ArrayList<ArrayList<String>>()
        var options3ItemsSaiguo = ArrayList< ArrayList<ArrayList<String>>>()
        //记录选择的
        var  saiYi=0
        var  saiEr=0
        var  saiSan=0
        //重置
        var  saiYiNew=0
        var  saiErNew=0
        var  saiSanNew=0
    }
}