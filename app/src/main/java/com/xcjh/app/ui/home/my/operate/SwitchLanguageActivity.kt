package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivitySetUpBinding
import com.xcjh.app.databinding.ActivitySwitchLanguageBinding
import com.xcjh.app.ui.MainActivity
import com.xcjh.base_lib.manager.KtxActivityManger

/**
 * 切换语言
 */
class SwitchLanguageActivity  : BaseActivity<SetUpVm, ActivitySwitchLanguageBinding>() {
    var type:Int=0  //0是中文  1是繁体 2是英文

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()

        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.my_txt_switch_language)

        //获取语种
        val locale = MultiLanguages.getAppLanguage(this)
        //简体中文
        if(LocaleContract.getSimplifiedChineseLocale().equals(locale)||LocaleContract.getChineseLocale().equals(locale)||locale.toString().equals("zh_CN_#Hans")){
            type=0
            mDatabind.ivSelectEn.visibility= View.INVISIBLE
            mDatabind.ivSelectSimplified.visibility= View.VISIBLE
            mDatabind.ivSelectComplex.visibility= View.INVISIBLE

        }else if(LocaleContract.getTraditionalChineseLocale().equals(locale)){//繁体中文
            type=1
            mDatabind.ivSelectEn.visibility= View.INVISIBLE
            mDatabind.ivSelectSimplified.visibility= View.INVISIBLE
            mDatabind.ivSelectComplex.visibility= View.VISIBLE
        }else{
            type=2
            mDatabind.ivSelectEn.visibility= View.VISIBLE
            mDatabind.ivSelectSimplified.visibility= View.INVISIBLE
            mDatabind.ivSelectComplex.visibility= View.INVISIBLE
        }



        //选择英文
        mDatabind.rlSwitchEn.setOnClickListener {
            type=2
            selectLanguage()

        }
        //选择繁体
        mDatabind.rlSwitchClickComplex.setOnClickListener {
            type=1
            selectLanguage()

        }
        //选择中文
        mDatabind.rlSwitchClickSimplified.setOnClickListener {
            type=0
            selectLanguage()

        }

    }

    override fun onBackPressed() {
        setLanguage()

    }


    override fun finishTopClick(view: View?) {
        setLanguage()

    }
    //0是中文  1是繁体 2是英文
    fun selectLanguage(){
        if(type==0){
            mDatabind.ivSelectEn.visibility= View.INVISIBLE
            mDatabind.ivSelectSimplified.visibility= View.VISIBLE
            mDatabind.ivSelectComplex.visibility= View.INVISIBLE
        }else if(type==1){
            mDatabind.ivSelectEn.visibility= View.INVISIBLE
            mDatabind.ivSelectSimplified.visibility= View.INVISIBLE
            mDatabind.ivSelectComplex.visibility= View.VISIBLE
        }else{
            mDatabind.ivSelectEn.visibility= View.VISIBLE
            mDatabind.ivSelectSimplified.visibility= View.INVISIBLE
            mDatabind.ivSelectComplex.visibility= View.INVISIBLE
        }
    }

    fun setLanguage(){
        //获取语种
        val locale = MultiLanguages.getAppLanguage(this)
        var system:Int=0
        if(LocaleContract.getSimplifiedChineseLocale().equals(locale)||LocaleContract.getChineseLocale().equals(locale)||locale.toString().equals("zh_CN_#Hans")){
            system=0
        }else if(LocaleContract.getTraditionalChineseLocale().equals(locale)){
            system=1
        }else{
            system=2
        }


        if(type==system){
            finish()
        }else{
            //  是否需要重启
            var restart = false

            if(type==0){
                // 简体
                restart = MultiLanguages.setAppLanguage(this, LocaleContract.getSimplifiedChineseLocale())
            }else if(type==1){
                // 繁体
                restart = MultiLanguages.setAppLanguage(this, LocaleContract.getTraditionalChineseLocale())
            }else{
                restart = MultiLanguages.setAppLanguage(this, LocaleContract.getEnglishLocale())
            }

            if (restart) {

                Handler().postDelayed({
                    //结束程序
                    KtxActivityManger.finishAllActivity()
                    startNewActivity<MainActivity>()
                    //exitProcess(0)
                }, 600)
            }
        }
    }
}