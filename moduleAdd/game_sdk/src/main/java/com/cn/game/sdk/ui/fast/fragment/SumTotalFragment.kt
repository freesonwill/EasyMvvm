package com.cn.game.sdk.ui.fast.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.getSystemService
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.bean.ComputeDefault
import com.cn.game.sdk.bean.ComputeSingle
import com.cn.game.sdk.bean.ComputeSum
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.databinding.FragmentSumTotalBinding
import com.cn.game.sdk.enums.NOTES_ENUM
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKView
import com.xcjh.base_lib.utils.dp2px
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 总和
 */
class SumTotalFragment : BaseGameFragment<SumTotalVm, FragmentSumTotalBinding>() {
    /**
     * 4
     */
    lateinit var showSumSiMoney: MoneyOKView
    /**
     * 5
     */
    lateinit var showSumWuMoney: MoneyOKView
    /**
     * 6
     */
    lateinit var showSumLiuMoney: MoneyOKView
    /**
     * 7
     */
    lateinit var showSumQiMoney: MoneyOKView
    /**
     * 8
     */
    lateinit var showSumBaMoney: MoneyOKView
    /**
     * 9
     */
    lateinit var showSumJiuMoney: MoneyOKView
    /**
     * 10
     */
    lateinit var showSumShiMoney: MoneyOKView
    /**
     * 11
     */
    lateinit var showSumShiYiMoney: MoneyOKView
    /**
     * 12
     */
    lateinit var showSumShiErMoney: MoneyOKView
    /**
     * 13
     */
    lateinit var showSumShiSanMoney: MoneyOKView
    /**
     * 14
     */
    lateinit var showSumShiSiMoney: MoneyOKView
    /**
     * 15
     */
    lateinit var showSumShiWuMoney: MoneyOKView
    /**
     *  16
     */
    lateinit var showSumShiLiuMoney: MoneyOKView
    /**
     *17
     */
    lateinit var showSumShiQiMoney: MoneyOKView




    private var animators: MutableList<ObjectAnimator> = mutableListOf()
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        showSumSiMoney=MoneyOKView(requireContext())
        showSumSiMoney.tag = "showSumSiMoney"
        showSumSiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumSiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumSiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumWuMoney=MoneyOKView(requireContext())
        showSumWuMoney.tag = "showSumWuMoney"
        showSumWuMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumWuMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumWuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumLiuMoney=MoneyOKView(requireContext())
        showSumLiuMoney.tag = "showSumLiuMoney"
        showSumLiuMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumLiuMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumLiuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumQiMoney=MoneyOKView(requireContext())
        showSumQiMoney.tag = "showSumQiMoney"
        showSumQiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumQiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumQiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumBaMoney=MoneyOKView(requireContext())
        showSumBaMoney.tag = "showSumBaMoney"
        showSumBaMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumBaMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumBaMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumJiuMoney=MoneyOKView(requireContext())
        showSumJiuMoney.tag = "showSumJiuMoney"
        showSumJiuMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumJiuMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumJiuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiMoney=MoneyOKView(requireContext())
        showSumShiMoney.tag = "showSumShiMoney"
        showSumShiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiYiMoney=MoneyOKView(requireContext())
        showSumShiYiMoney.tag = "showSumShiYiMoney"
        showSumShiYiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiYiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiYiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiErMoney=MoneyOKView(requireContext())
        showSumShiErMoney.tag = "showSumShiErMoney"
        showSumShiErMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiErMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiErMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiSanMoney=MoneyOKView(requireContext())
        showSumShiSanMoney.tag = "showSumShiSanMoney"
        showSumShiSanMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiSanMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiSanMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiSiMoney=MoneyOKView(requireContext())
        showSumShiSiMoney.tag = "showSumShiSiMoney"
        showSumShiSiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiSiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiSiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiWuMoney=MoneyOKView(requireContext())
        showSumShiWuMoney.tag = "showSumShiWuMoney"
        showSumShiWuMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiWuMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiWuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiLiuMoney=MoneyOKView(requireContext())
        showSumShiLiuMoney.tag = "showSumShiLiuMoney"
        showSumShiLiuMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiLiuMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiLiuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSumShiQiMoney=MoneyOKView(requireContext())
        showSumShiQiMoney.tag = "showSumShiQiMoney"
        showSumShiQiMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvSumClickFour.indexOfChild(showSumShiQiMoney) != -1) {
                        mDatabind.rvSumClickFour.removeView(showSumShiQiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功
                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })
        clickShow()

    }



    /**
     * 退出页面的时候要清空这些数据
     */
    fun closeActivity(){
        animators.forEach { it.cancel() }
        animators.clear()

    }

    /**
     * 删除投注 1111 就保留上一次的确定的钱,isEmpty是否清空临时的钱，如果是点击的叉叉就要清空~~如果是勾勾就不用清除
     */
    fun deleteBet(isEmpty:Boolean=true){
        //删除临时钱
        if(isEmpty){
            ComputeSum.sumTotalSi.moneyTemporary=0
            ComputeSum.sumTotalWu.moneyTemporary=0
            ComputeSum.sumTotalLiu.moneyTemporary=0
            ComputeSum.sumTotalQi.moneyTemporary=0
            ComputeSum.sumTotalBa.moneyTemporary=0
            ComputeSum.sumTotalJiu.moneyTemporary=0
            ComputeSum.sumTotalShi.moneyTemporary=0
            ComputeSum.sumTotalShiYi.moneyTemporary=0
            ComputeSum.sumTotalShiEr.moneyTemporary=0
            ComputeSum.sumTotalShiSan.moneyTemporary=0
            ComputeSum.sumTotalShiSi.moneyTemporary=0
            ComputeSum.sumTotalShiWu.moneyTemporary=0
            ComputeSum.sumTotalShiLiu.moneyTemporary=0
            ComputeSum.sumTotalShiQi.moneyTemporary=0

        }
        //删除没有确定钱的坐标
        if(ComputeSum.sumTotalSi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalSi.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalWu.moneyOkEmpty<=0) {
            ComputeSum.sumTotalWu.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalLiu.moneyOkEmpty<=0) {
            ComputeSum.sumTotalLiu.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalQi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalQi.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalBa.moneyOkEmpty<=0) {
            ComputeSum.sumTotalBa.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalJiu.moneyOkEmpty<=0) {
            ComputeSum.sumTotalJiu.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShi.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiYi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiYi.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiEr.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiEr.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiSan.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiSan.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiSi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiSi.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiWu.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiWu.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiLiu.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiLiu.viewXYTemporary = intArrayOf(0, 0)
        }
        if(ComputeSum.sumTotalShiQi.moneyOkEmpty<=0) {
            ComputeSum.sumTotalShiQi.viewXYTemporary = intArrayOf(0, 0)
        }




        //
        if(ComputeSum.sumTotalSi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumSiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumSiMoney)
            }
        }else{
            showSumSiMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalWu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumWuMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumWuMoney)
            }
        }else{
            showSumWuMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalLiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumLiuMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumLiuMoney)
            }
        }else{
            showSumLiuMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalQi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumQiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumQiMoney)
            }
        }else{
            showSumQiMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalBa.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumBaMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumBaMoney)
            }
        }else{
            showSumBaMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalJiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumJiuMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumJiuMoney)
            }
        }else{
            showSumJiuMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalShi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiMoney)
            }
        }else{
            showSumShiMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalShiYi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiYiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiYiMoney)
            }
        }else{
            showSumShiYiMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalShiEr.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiErMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiErMoney)
            }
        }else{
            showSumShiErMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalShiSan.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSanMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiSanMoney)
            }
        }else{
            showSumShiSanMoney.hiddenTop()
        }

        if(ComputeSum.sumTotalShiSi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiSiMoney)
            }
        }else{
            showSumShiSiMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalShiWu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiWuMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiWuMoney)
            }
        }else{
            showSumShiWuMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalShiLiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiLiuMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiLiuMoney)
            }
        }else{
            showSumShiLiuMoney.hiddenTop()
        }
        if(ComputeSum.sumTotalShiQi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rlHomeRoot.indexOfChild(showSumShiQiMoney) != -1) {
                mDatabind.rlHomeRoot.removeView(showSumShiQiMoney)
            }
        }else{
            showSumShiQiMoney.hiddenTop()
        }

    }


    /**
     * 从新设置一下每个注区显示投注的钱
     */
    fun setAllShowViewMoney(){
        showSumSiMoney.setShowMoney(ComputeSum.sumTotalSi.moneyOkEmpty)
        showSumWuMoney.setShowMoney(ComputeSum.sumTotalWu.moneyOkEmpty)
        showSumLiuMoney.setShowMoney(ComputeSum.sumTotalLiu.moneyOkEmpty)
        showSumQiMoney.setShowMoney(ComputeSum.sumTotalQi.moneyOkEmpty)
        showSumBaMoney.setShowMoney(ComputeSum.sumTotalBa.moneyOkEmpty)
        showSumJiuMoney.setShowMoney(ComputeSum.sumTotalJiu.moneyOkEmpty)
        showSumShiMoney.setShowMoney(ComputeSum.sumTotalShi.moneyOkEmpty)
        showSumShiYiMoney.setShowMoney(ComputeSum.sumTotalShiYi.moneyOkEmpty)
        showSumShiErMoney.setShowMoney(ComputeSum.sumTotalShiEr.moneyOkEmpty)
        showSumShiSanMoney.setShowMoney(ComputeSum.sumTotalShiSan.moneyOkEmpty)
        showSumShiSiMoney.setShowMoney(ComputeSum.sumTotalShiSi.moneyOkEmpty)
        showSumShiWuMoney.setShowMoney(ComputeSum.sumTotalShiWu.moneyOkEmpty)
        showSumShiLiuMoney.setShowMoney(ComputeSum.sumTotalShiLiu.moneyOkEmpty)
        showSumShiQiMoney.setShowMoney(ComputeSum.sumTotalShiQi.moneyOkEmpty)



    }

    /**
     * 得到开奖信息后删除所有的在界面上的注码
     */
    fun closeBetting(){
        if (mDatabind.rlHomeRoot.indexOfChild(showSumSiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumSiMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumWuMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumWuMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumLiuMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumLiuMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumQiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumQiMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumBaMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumBaMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumJiuMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumJiuMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiYiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiYiMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiErMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiErMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSanMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiSanMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiSiMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiWuMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiWuMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiLiuMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiLiuMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiQiMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showSumShiQiMoney)
        }

    }


    /**
     * 中奖区域闪烁的动画
     *isAnimation true  是执行动画   flase是取消动画
     */
    fun fadeOut(isAnimation:Boolean,inPrizeList:ArrayList<InPrizeBean> =ArrayList<InPrizeBean>()){
        if(isAnimation){
            inPrizeList.forEach {
                if(it.inPrizType== NOTES_ENUM.GQTNumber4.num){
                    mDatabind.ivSumFlashSi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashSi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                } else  if(it.inPrizType== NOTES_ENUM.GQTNumber5.num){
                    mDatabind.ivSumFlashWu.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashWu, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber6.num){
                    mDatabind.ivSumFlashLiu.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashLiu, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber7.num){
                    mDatabind.ivSumFlashQi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashQi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber8.num){
                    mDatabind.ivSumFlashBa.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashBa, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber9.num){
                    mDatabind.ivSumFlashJiu.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashJiu, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber10.num){
                    mDatabind.ivSumFlashShi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber11.num){
                    mDatabind.ivSumFlashShiYi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiYi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber12.num){
                    mDatabind.ivSumFlashShiEr.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiEr, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber13.num){
                    mDatabind.ivSumFlashShiSan.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiSan, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber14.num){
                    mDatabind.ivSumFlashShiSi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiSi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber15.num){
                    mDatabind.ivSumFlashShiWu.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiWu, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber16.num){
                    mDatabind.ivSumFlashShiLiu.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiLiu, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.GQTNumber17.num){
                    mDatabind.ivSumFlashShiQi.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSumFlashShiQi, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }
            }

            // 开始动画
            animators.forEach { it.start() }

        }else{
            mDatabind.ivSumFlashSi.visibility= View.GONE
            mDatabind.ivSumFlashWu.visibility= View.GONE
            mDatabind.ivSumFlashLiu.visibility= View.GONE
            mDatabind.ivSumFlashQi.visibility= View.GONE
            mDatabind.ivSumFlashBa.visibility= View.GONE
            mDatabind.ivSumFlashJiu.visibility= View.GONE
            mDatabind.ivSumFlashShi.visibility= View.GONE
            mDatabind.ivSumFlashShiYi.visibility= View.GONE
            mDatabind.ivSumFlashShiEr.visibility= View.GONE
            mDatabind.ivSumFlashShiSan.visibility= View.GONE
            mDatabind.ivSumFlashShiSi.visibility= View.GONE
            mDatabind.ivSumFlashShiWu.visibility= View.GONE
            mDatabind.ivSumFlashShiLiu.visibility= View.GONE
            mDatabind.ivSumFlashShiQi.visibility= View.GONE

            animators.forEach { it.cancel() }
            animators.clear()
        }


    }


    /**
     * inPrizeList   中奖的区域
     * pressureIn    压中的区域
     */
    fun flicker(inPrizeList:ArrayList<InPrizeBean>, pressureIn:ArrayList<InPrizeBean>){
        closeBetting()
        //中奖区域
        var inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.GQTNumber4.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.GQTNumber9.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.GQTNumber10.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.GQTNumber11.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.GQTNumber12.num
        inPrizeList.add(inPrizeBean)
        fadeOut(true,inPrizeList)
        //压中
        for (i in 0 until  inPrizeList.size) {
            if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber4.num&& ComputeSum.sumTotalSi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber4.num
                pressure.money=(ComputeSum.sumTotalSi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber5.num&& ComputeSum.sumTotalWu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber5.num
                pressure.money=(ComputeSum.sumTotalWu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber6.num&& ComputeSum.sumTotalLiu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber6.num
                pressure.money=(ComputeSum.sumTotalLiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber7.num&& ComputeSum.sumTotalQi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber7.num
                pressure.money=(ComputeSum.sumTotalQi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber8.num&& ComputeSum.sumTotalBa .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber8.num
                pressure.money=(ComputeSum.sumTotalBa.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber9.num&& ComputeSum.sumTotalJiu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber9.num
                pressure.money=(ComputeSum.sumTotalJiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber10.num&& ComputeSum.sumTotalShi.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber10.num
                pressure.money=(ComputeSum.sumTotalShi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber11.num&& ComputeSum.sumTotalShiYi.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber11.num
                pressure.money=(ComputeSum.sumTotalShiYi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber12.num&& ComputeSum.sumTotalShiEr.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber12.num
                pressure.money=(ComputeSum.sumTotalShiEr.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber13.num&& ComputeSum.sumTotalShiSan.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber13.num
                pressure.money=(ComputeSum.sumTotalShiSan.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber14.num&& ComputeSum.sumTotalShiSi.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber14.num
                pressure.money=(ComputeSum.sumTotalShiSi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber15.num&& ComputeSum.sumTotalShiWu.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber15.num
                pressure.money=(ComputeSum.sumTotalShiWu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber16.num&& ComputeSum.sumTotalShiLiu.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber16.num
                pressure.money=(ComputeSum.sumTotalShiLiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.GQTNumber17.num&& ComputeSum.sumTotalShiQi.moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.GQTNumber17.num
                pressure.money=(ComputeSum.sumTotalShiQi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }


        }

        //显示数据
        for (i in 0 until  pressureIn.size) {
            if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber4.num){
                 //计算钱
                showSumSiMoney.setShowMoney(pressureIn[i].money)
                showSumSiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumSiMoney, params)
                showSumSiMoney.translationX = ComputeSum.sumTotalSi.viewXYTemporary[0].toFloat()
                showSumSiMoney.translationY =  ComputeSum.sumTotalSi.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber5.num){
                //计算钱
                showSumWuMoney.setShowMoney(pressureIn[i].money)
                showSumWuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumWuMoney, params)
                showSumWuMoney.translationX = ComputeSum.sumTotalWu.viewXYTemporary[0].toFloat()
                showSumWuMoney.translationY =  ComputeSum.sumTotalWu.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber6.num){
                //计算钱
                showSumLiuMoney.setShowMoney(pressureIn[i].money)
                showSumLiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumLiuMoney, params)
                showSumLiuMoney.translationX = ComputeSum.sumTotalLiu.viewXYTemporary[0].toFloat()
                showSumLiuMoney.translationY =  ComputeSum.sumTotalLiu.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber7.num){
                //计算钱
                showSumQiMoney.setShowMoney(pressureIn[i].money)
                showSumQiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumQiMoney, params)
                showSumQiMoney.translationX = ComputeSum.sumTotalQi.viewXYTemporary[0].toFloat()
                showSumQiMoney.translationY =  ComputeSum.sumTotalQi.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber8.num){
                //计算钱
                showSumBaMoney.setShowMoney(pressureIn[i].money)
                showSumBaMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumBaMoney, params)
                showSumBaMoney.translationX = ComputeSum.sumTotalBa.viewXYTemporary[0].toFloat()
                showSumBaMoney.translationY =  ComputeSum.sumTotalBa.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber9.num){
                //计算钱
                showSumJiuMoney.setShowMoney(pressureIn[i].money)
                showSumJiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumJiuMoney, params)
                showSumJiuMoney.translationX = ComputeSum.sumTotalJiu.viewXYTemporary[0].toFloat()
                showSumJiuMoney.translationY =  ComputeSum.sumTotalJiu.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber10.num){
                //计算钱
                showSumShiMoney.setShowMoney(pressureIn[i].money)
                showSumShiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiMoney, params)
                showSumShiMoney.translationX = ComputeSum.sumTotalShi.viewXYTemporary[0].toFloat()
                showSumShiMoney.translationY =  ComputeSum.sumTotalShi.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber11.num){
                //计算钱
                showSumShiYiMoney.setShowMoney(pressureIn[i].money)
                showSumShiYiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiYiMoney, params)
                showSumShiYiMoney.translationX = ComputeSum.sumTotalShiYi.viewXYTemporary[0].toFloat()
                showSumShiYiMoney.translationY =  ComputeSum.sumTotalShiYi.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber12.num){
                //计算钱
                showSumShiErMoney.setShowMoney(pressureIn[i].money)
                showSumShiErMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiErMoney, params)
                showSumShiErMoney.translationX = ComputeSum.sumTotalShiEr.viewXYTemporary[0].toFloat()
                showSumShiErMoney.translationY =  ComputeSum.sumTotalShiEr.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber13.num){
                //计算钱
                showSumShiSanMoney.setShowMoney(pressureIn[i].money)
                showSumShiSanMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiSanMoney, params)
                showSumShiSanMoney.translationX = ComputeSum.sumTotalShiSan.viewXYTemporary[0].toFloat()
                showSumShiSanMoney.translationY =  ComputeSum.sumTotalShiSan.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber14.num){
                //计算钱
                showSumShiSiMoney.setShowMoney(pressureIn[i].money)
                showSumShiSiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiSiMoney, params)
                showSumShiSiMoney.translationX = ComputeSum.sumTotalShiSi.viewXYTemporary[0].toFloat()
                showSumShiSiMoney.translationY =  ComputeSum.sumTotalShiSi.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber15.num){
                //计算钱
                showSumShiWuMoney.setShowMoney(pressureIn[i].money)
                showSumShiWuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiWuMoney, params)
                showSumShiWuMoney.translationX = ComputeSum.sumTotalShiWu.viewXYTemporary[0].toFloat()
                showSumShiWuMoney.translationY =  ComputeSum.sumTotalShiWu.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber16.num){
                //计算钱
                showSumShiLiuMoney.setShowMoney(pressureIn[i].money)
                showSumShiLiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiLiuMoney, params)
                showSumShiLiuMoney.translationX = ComputeSum.sumTotalShiLiu.viewXYTemporary[0].toFloat()
                showSumShiLiuMoney.translationY =  ComputeSum.sumTotalShiLiu.viewXYTemporary[1].toFloat()
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.GQTNumber17.num){
                //计算钱
                showSumShiQiMoney.setShowMoney(pressureIn[i].money)
                showSumShiQiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showSumShiQiMoney, params)
                showSumShiQiMoney.translationX = ComputeSum.sumTotalShiQi.viewXYTemporary[0].toFloat()
                showSumShiQiMoney.translationY =  ComputeSum.sumTotalShiQi.viewXYTemporary[1].toFloat()
            }



        }

        deletePreviousRound()

    }

    /**
     * 删除上一轮的数据
     */
    fun  deletePreviousRound(){
        ComputeSum.sumTotalSi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalSi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalSi.moneyTemporary= 0
        ComputeSum.sumTotalSi.moneyOkEmpty= 0

        ComputeSum.sumTotalWu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalWu.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalWu.moneyTemporary= 0
        ComputeSum.sumTotalWu.moneyOkEmpty= 0

        ComputeSum.sumTotalLiu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalLiu.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalLiu.moneyTemporary= 0
        ComputeSum.sumTotalLiu.moneyOkEmpty= 0

        ComputeSum.sumTotalQi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalQi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalQi.moneyTemporary= 0
        ComputeSum.sumTotalQi.moneyOkEmpty= 0

        ComputeSum.sumTotalBa.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalBa.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalBa.moneyTemporary= 0
        ComputeSum.sumTotalBa.moneyOkEmpty= 0

        ComputeSum.sumTotalJiu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalJiu.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalJiu.moneyTemporary= 0
        ComputeSum.sumTotalJiu.moneyOkEmpty= 0

        ComputeSum.sumTotalShi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShi.moneyTemporary= 0
        ComputeSum.sumTotalShi.moneyOkEmpty= 0

        ComputeSum.sumTotalShiYi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiYi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiYi.moneyTemporary= 0
        ComputeSum.sumTotalShiYi.moneyOkEmpty= 0

        ComputeSum.sumTotalShiEr.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiEr.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiEr.moneyTemporary= 0
        ComputeSum.sumTotalShiEr.moneyOkEmpty= 0

        ComputeSum.sumTotalShiSan.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiSan.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiSan.moneyTemporary= 0
        ComputeSum.sumTotalShiSan.moneyOkEmpty= 0

        ComputeSum.sumTotalShiSi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiSi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiSi.moneyTemporary= 0
        ComputeSum.sumTotalShiSi.moneyOkEmpty= 0

        ComputeSum.sumTotalShiWu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiWu.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiWu.moneyTemporary= 0
        ComputeSum.sumTotalShiWu.moneyOkEmpty= 0

        ComputeSum.sumTotalShiLiu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiLiu.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiLiu.moneyTemporary= 0
        ComputeSum.sumTotalShiLiu.moneyOkEmpty= 0

        ComputeSum.sumTotalShiQi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSum.sumTotalShiQi.viewXYLast= intArrayOf(0, 0)
        ComputeSum.sumTotalShiQi.moneyTemporary= 0
        ComputeSum.sumTotalShiQi.moneyOkEmpty= 0

    }

    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     *
     */
    private fun clickAnimationIsHidden(num:Int ){
        if(num!=4){
            showSumSiMoney.hiddenTop()
        }
        if(num!=5){
            showSumWuMoney.hiddenTop()
        }
        if(num!=6){
            showSumLiuMoney.hiddenTop()
        }
        if(num!=7){
            showSumQiMoney.hiddenTop()
        }
        if(num!=8){
            showSumBaMoney.hiddenTop()
        }

        if(num!=9){
            showSumJiuMoney.hiddenTop()
        }
        if(num!=10){
            showSumShiMoney.hiddenTop()
        }
        if(num!=11){
            showSumShiYiMoney.hiddenTop()
        }
        if(num!=12){
            showSumShiErMoney.hiddenTop()
        }
        if(num!=13){
            showSumShiSanMoney.hiddenTop()
        }
        if(num!=14){
            showSumShiSiMoney.hiddenTop()
        }

        if(num!=15){
            showSumShiWuMoney.hiddenTop()
        }

        if(num!=16){
            showSumShiLiuMoney.hiddenTop()
        }

        if(num!=17){
            showSumShiQiMoney.hiddenTop()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun   clickShow(){

        mDatabind.rvSumClickFour.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        var y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickFour.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }
                        }
                        if(ComputeSum.sumTotalSi.viewXYTemporary [0]==0&& ComputeSum.sumTotalSi.viewXYTemporary[1]==0){
//                            ComputeSum.sumTotalWu.viewXYTemporary[0]=rax.toInt()
                            ComputeSum.sumTotalSi.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickFour.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalSi.viewXYTemporary[1]=newY.toInt()-requireContext().dp2px(52)
                        }

                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(4)
                        showSumSiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalSi.moneyTemporary= ComputeSum.sumTotalSi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumSiMoney.setShowMoney(ComputeSum.sumTotalSi.moneyTemporary+ ComputeSum.sumTotalSi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumSiMoney) != -1) {
                            val location = IntArray(2)
                            showSumSiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumSiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumSiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumSiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumSiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumSiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]

                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumSiMoney.ivShowBg)

                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumSiMoney, params)
                            showSumSiMoney.translationX =  ComputeSum.sumTotalSi.viewXYTemporary[0].toFloat()
                            showSumSiMoney.translationY =  ComputeSum.sumTotalSi.viewXYTemporary[1].toFloat()

                        }



                    }


                }



            }




            false // 返回 true 表示事件已经被处理
        }
        /**
         * 五
         */
        mDatabind.vSumClickFive.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.vSumClickFive.getLocationOnScreen(location)

                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                             if (y < viewBounds.top) {
                                 newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                 newY=   viewBounds.bottom.toFloat()
                            } else {
                                 newY=  y
                             }

                        }


                        if(ComputeSum.sumTotalWu.viewXYTemporary [0]==0&& ComputeSum.sumTotalWu.viewXYTemporary[1]==0){
//                            ComputeSum.sumTotalWu.viewXYTemporary[0]=rax.toInt()
                            ComputeSum.sumTotalWu.viewXYTemporary[0]=location[0]+mDatabind.vSumClickFive.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalWu.viewXYTemporary[1]=newY.toInt()-requireContext().dp2px(52)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(5)
                        showSumWuMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalWu.moneyTemporary= ComputeSum.sumTotalWu.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumWuMoney.setShowMoney(ComputeSum.sumTotalWu.moneyTemporary+ ComputeSum.sumTotalWu.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumWuMoney) != -1) {
                            val location = IntArray(2)
                            showSumWuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumWuMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumWuMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumWuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumWuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumWuMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumWuMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalWu.viewXYTemporary[0]==0&&ComputeSum.sumTotalWu.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumWuMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalWu.viewXYTemporary[0]= showSumWuMoney.left
                                        ComputeSum.sumTotalWu.viewXYTemporary[1]=showSumWuMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumWuMoney, params)

                            showSumWuMoney.translationX =  ComputeSum.sumTotalWu.viewXYTemporary[0].toFloat()
                            showSumWuMoney.translationY =  ComputeSum.sumTotalWu.viewXYTemporary[1].toFloat()
                        }



                    }

                }

                }

            false // 返回 true 表示事件已经被处理
        }

        /**
         *六
         */
        mDatabind.vSumClickSix.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.vSumClickSix.getLocationOnScreen(location)

                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }


                        if(ComputeSum.sumTotalLiu.viewXYTemporary [0]==0&& ComputeSum.sumTotalLiu.viewXYTemporary[1]==0){
//                            ComputeSum.sumTotalWu.viewXYTemporary[0]=rax.toInt()
                            ComputeSum.sumTotalLiu.viewXYTemporary[0]=location[0]+mDatabind.vSumClickSix.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalLiu.viewXYTemporary[1]=newY.toInt()-requireContext().dp2px(52)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(6)
                        showSumLiuMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalLiu.moneyTemporary= ComputeSum.sumTotalLiu.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumLiuMoney.setShowMoney(ComputeSum.sumTotalLiu.moneyTemporary+ ComputeSum.sumTotalLiu.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumLiuMoney) != -1) {
                            val location = IntArray(2)
                            showSumLiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumLiuMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumLiuMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumLiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumLiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumLiuMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumLiuMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalLiu.viewXYTemporary[0]==0&&ComputeSum.sumTotalLiu.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumLiuMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalLiu.viewXYTemporary[0]= showSumLiuMoney.left
                                        ComputeSum.sumTotalLiu.viewXYTemporary[1]=showSumLiuMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumLiuMoney, params)

                            showSumLiuMoney.translationX =  ComputeSum.sumTotalLiu.viewXYTemporary[0].toFloat()
                            showSumLiuMoney.translationY =  ComputeSum.sumTotalLiu.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *7
         */
        mDatabind.vSumClickSeven.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.vSumClickSeven.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }

                        if(ComputeSum.sumTotalQi.viewXYTemporary [0]==0&& ComputeSum.sumTotalQi.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalQi.viewXYTemporary[0]=location[0]+mDatabind.vSumClickSeven.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalQi.viewXYTemporary[1]=newY.toInt()-requireContext().dp2px(52)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(7)
                        showSumQiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalQi.moneyTemporary= ComputeSum.sumTotalQi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumQiMoney.setShowMoney(ComputeSum.sumTotalQi.moneyTemporary+ ComputeSum.sumTotalQi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumQiMoney) != -1) {
                            val location = IntArray(2)
                            showSumQiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumQiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumQiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumQiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumQiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumQiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumQiMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalQi.viewXYTemporary[0]==0&&ComputeSum.sumTotalQi.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumQiMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalQi.viewXYTemporary[0]= showSumQiMoney.left
                                        ComputeSum.sumTotalQi.viewXYTemporary[1]=showSumQiMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumQiMoney, params)

                            showSumQiMoney.translationX =  ComputeSum.sumTotalQi.viewXYTemporary[0].toFloat()
                            showSumQiMoney.translationY =  ComputeSum.sumTotalQi.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *8
         */
        mDatabind.vSumClickEight.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.vSumClickEight.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }

                        if(ComputeSum.sumTotalBa.viewXYTemporary [0]==0&& ComputeSum.sumTotalBa.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalBa.viewXYTemporary[0]=location[0]+mDatabind.vSumClickEight.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalBa.viewXYTemporary[1]=newY.toInt()-requireContext().dp2px(52)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(8)
                        showSumBaMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalBa.moneyTemporary= ComputeSum.sumTotalBa.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumBaMoney.setShowMoney(ComputeSum.sumTotalBa.moneyTemporary+ ComputeSum.sumTotalBa.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumBaMoney) != -1) {
                            val location = IntArray(2)
                            showSumBaMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumBaMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumBaMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumBaMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumBaMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumBaMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumBaMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalBa.viewXYTemporary[0]==0&&ComputeSum.sumTotalBa.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumBaMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalBa.viewXYTemporary[0]= showSumBaMoney.left
                                        ComputeSum.sumTotalBa.viewXYTemporary[1]=showSumBaMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumBaMoney, params)

                            showSumBaMoney.translationX =  ComputeSum.sumTotalBa.viewXYTemporary[0].toFloat()
                            showSumBaMoney.translationY =  ComputeSum.sumTotalBa.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *9
         */
        mDatabind.rvSumClickNine.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickNine.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }

                        if(ComputeSum.sumTotalJiu.viewXYTemporary [0]==0&& ComputeSum.sumTotalJiu.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalJiu.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickNine.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalJiu.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(8)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(9)
                        showSumJiuMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalJiu.moneyTemporary= ComputeSum.sumTotalJiu.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumJiuMoney.setShowMoney(ComputeSum.sumTotalJiu.moneyTemporary+ ComputeSum.sumTotalJiu.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumJiuMoney) != -1) {
                            val location = IntArray(2)
                            showSumJiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumJiuMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumJiuMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumJiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumJiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumJiuMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumJiuMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalJiu.viewXYTemporary[0]==0&&ComputeSum.sumTotalJiu.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumJiuMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalJiu.viewXYTemporary[0]= showSumJiuMoney.left
                                        ComputeSum.sumTotalJiu.viewXYTemporary[1]=showSumJiuMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumJiuMoney, params)

                            showSumJiuMoney.translationX =  ComputeSum.sumTotalJiu.viewXYTemporary[0].toFloat()
                            showSumJiuMoney.translationY =  ComputeSum.sumTotalJiu.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *10
         */
        mDatabind.rvSumClickTen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickTen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }

                        if(ComputeSum.sumTotalShi.viewXYTemporary [0]==0&& ComputeSum.sumTotalShi.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShi.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickTen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShi.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(8)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(10)
                        showSumShiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShi.moneyTemporary= ComputeSum.sumTotalShi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiMoney.setShowMoney(ComputeSum.sumTotalShi.moneyTemporary+ ComputeSum.sumTotalShi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShi.viewXYTemporary[0]==0&&ComputeSum.sumTotalShi.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShi.viewXYTemporary[0]= showSumShiMoney.left
                                        ComputeSum.sumTotalShi.viewXYTemporary[1]=showSumShiMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiMoney, params)

                            showSumShiMoney.translationX =  ComputeSum.sumTotalShi.viewXYTemporary[0].toFloat()
                            showSumShiMoney.translationY =  ComputeSum.sumTotalShi.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *11
         */
        mDatabind.rvSumClickEleven.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickEleven.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }

                        if(ComputeSum.sumTotalShiYi.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiYi.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiYi.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickEleven.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiYi.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(8)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(11)
                        showSumShiYiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiYi.moneyTemporary= ComputeSum.sumTotalShiYi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiYiMoney.setShowMoney(ComputeSum.sumTotalShiYi.moneyTemporary+ ComputeSum.sumTotalShiYi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiYiMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiYiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiYiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiYiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiYiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiYiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiYiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiYiMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiYi.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiYi.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiYiMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiYi.viewXYTemporary[0]= showSumShiYiMoney.left
                                        ComputeSum.sumTotalShiYi.viewXYTemporary[1]=showSumShiYiMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiYiMoney, params)

                            showSumShiYiMoney.translationX =  ComputeSum.sumTotalShiYi.viewXYTemporary[0].toFloat()
                            showSumShiYiMoney.translationY =  ComputeSum.sumTotalShiYi.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *12
         */
        mDatabind.rvSumClickTwelve.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickTwelve.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiEr.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiEr.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiEr.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickTwelve.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiEr.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(8)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(12)
                        showSumShiErMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiEr.moneyTemporary= ComputeSum.sumTotalShiEr.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiErMoney.setShowMoney(ComputeSum.sumTotalShiEr.moneyTemporary+ ComputeSum.sumTotalShiEr.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiErMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiErMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiErMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiErMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiErMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiErMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiErMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiErMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiEr.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiEr.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiErMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiEr.viewXYTemporary[0]= showSumShiErMoney.left
                                        ComputeSum.sumTotalShiEr.viewXYTemporary[1]=showSumShiErMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiErMoney, params)

                            showSumShiErMoney.translationX =  ComputeSum.sumTotalShiEr.viewXYTemporary[0].toFloat()
                            showSumShiErMoney.translationY =  ComputeSum.sumTotalShiEr.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }


         /**
         *13
         */
        mDatabind.rvSumClickThirteen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickThirteen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiSan.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiSan.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiSan.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickThirteen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiSan.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(8)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(13)
                        showSumShiSanMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiSan.moneyTemporary= ComputeSum.sumTotalShiSan.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiSanMoney.setShowMoney(ComputeSum.sumTotalShiSan.moneyTemporary+ ComputeSum.sumTotalShiSan.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSanMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiSanMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiSanMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiSanMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiSanMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiSanMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiSanMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiSanMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiSan.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiSan.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiSanMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiSan.viewXYTemporary[0]= showSumShiSanMoney.left
                                        ComputeSum.sumTotalShiSan.viewXYTemporary[1]=showSumShiSanMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiSanMoney, params)

                            showSumShiSanMoney.translationX =  ComputeSum.sumTotalShiSan.viewXYTemporary[0].toFloat()
                            showSumShiSanMoney.translationY =  ComputeSum.sumTotalShiSan.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }

        /**
         *14
         */
        mDatabind.rvSumClickFourteen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickFourteen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiSi.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiSi.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiSi.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickFourteen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiSi.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(68)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(14)
                        showSumShiSiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiSi.moneyTemporary= ComputeSum.sumTotalShiSi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiSiMoney.setShowMoney(ComputeSum.sumTotalShiSi.moneyTemporary+ ComputeSum.sumTotalShiSi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiSiMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiSiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiSiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiSiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiSiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiSiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiSiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiSiMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiSi.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiSi.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiSiMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiSi.viewXYTemporary[0]= showSumShiSiMoney.left
                                        ComputeSum.sumTotalShiSi.viewXYTemporary[1]=showSumShiSiMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiSiMoney, params)

                            showSumShiSiMoney.translationX =  ComputeSum.sumTotalShiSi.viewXYTemporary[0].toFloat()
                            showSumShiSiMoney.translationY =  ComputeSum.sumTotalShiSi.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }
        /**
         *15
         */
        mDatabind.rvSumClickFifteen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickFifteen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiWu.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiWu.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiWu.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickFifteen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiWu.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(68)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(15)
                        showSumShiWuMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiWu.moneyTemporary= ComputeSum.sumTotalShiWu.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiWuMoney.setShowMoney(ComputeSum.sumTotalShiWu.moneyTemporary+ ComputeSum.sumTotalShiWu.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiWuMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiWuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiWuMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiWuMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiWuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiWuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiWuMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiWuMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiWu.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiWu.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiWuMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiWu.viewXYTemporary[0]= showSumShiWuMoney.left
                                        ComputeSum.sumTotalShiWu.viewXYTemporary[1]=showSumShiWuMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiWuMoney, params)

                            showSumShiWuMoney.translationX =  ComputeSum.sumTotalShiWu.viewXYTemporary[0].toFloat()
                            showSumShiWuMoney.translationY =  ComputeSum.sumTotalShiWu.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }

        /**
         *16
         */
        mDatabind.rvSumClickSixteen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickSixteen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiLiu.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiLiu.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiLiu.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickSixteen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiLiu.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(68)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(16)
                        showSumShiLiuMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiLiu.moneyTemporary= ComputeSum.sumTotalShiLiu.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiLiuMoney.setShowMoney(ComputeSum.sumTotalShiLiu.moneyTemporary+ ComputeSum.sumTotalShiLiu.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiLiuMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiLiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiLiuMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiLiuMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiLiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiLiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiLiuMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiLiuMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiLiu.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiLiu.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiLiuMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiLiu.viewXYTemporary[0]= showSumShiLiuMoney.left
                                        ComputeSum.sumTotalShiLiu.viewXYTemporary[1]=showSumShiLiuMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiLiuMoney, params)

                            showSumShiLiuMoney.translationX =  ComputeSum.sumTotalShiLiu.viewXYTemporary[0].toFloat()
                            showSumShiLiuMoney.translationY =  ComputeSum.sumTotalShiLiu.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }

        /**
         *17
         */
        mDatabind.rvSumClickSeventeen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    //先判断余额是否够这次 并且扣取钱
                    if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                        val x = event.x
                        val y = event.y

                        val location = IntArray(2)
                        mDatabind.rvSumClickSeventeen.getLocationOnScreen(location)
                        val edgeSizeInPixels =requireContext().dp2px(30) // 将5dp转换为像素值
                        val viewBounds = Rect()
                        v.getDrawingRect(viewBounds)
                        viewBounds.inset(edgeSizeInPixels, edgeSizeInPixels)
                        var newY:Float=0f
                        newY=y
                        if (!viewBounds.contains(x.toInt(), y.toInt())) {
//                           // 点击事件超出了边缘范围，调整点击位置到距离边缘5dp的位置
//                             val adjustedX =    if (x < viewBounds.left) viewBounds.left else if (x > viewBounds.right) viewBounds.right else x
                            if (y < viewBounds.top) {
                                newY=  viewBounds.top.toFloat()
                            }  else  if (y > viewBounds.bottom) {
                                newY=   viewBounds.bottom.toFloat()
                            } else {
                                newY=  y
                            }

                        }
                        if(ComputeSum.sumTotalShiQi.viewXYTemporary [0]==0&& ComputeSum.sumTotalShiQi.viewXYTemporary[1]==0){
                            ComputeSum.sumTotalShiQi.viewXYTemporary[0]=location[0]+mDatabind.rvSumClickSeventeen.width/2-requireContext().dp2px(30)
                            ComputeSum.sumTotalShiQi.viewXYTemporary[1]=newY.toInt()+requireContext().dp2px(68)
                        }
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        clickAnimationIsHidden(17)
                        showSumShiQiMoney.showTop()
                        //计算钱
                        ComputeSum.sumTotalShiQi.moneyTemporary= ComputeSum.sumTotalShiQi.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showSumShiQiMoney.setShowMoney(ComputeSum.sumTotalShiQi.moneyTemporary+ ComputeSum.sumTotalShiQi.moneyOkEmpty)

                        //动画位置
                        if (mDatabind.rlHomeRoot.indexOfChild(showSumShiQiMoney) != -1) {
                            val location = IntArray(2)
                            showSumShiQiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]
                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)
                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSumShiQiMoney.ivShowBg )

                        } else {
                            val viewTreeObserver = showSumShiQiMoney.viewTreeObserver
                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // 确保只监听一次
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        showSumShiQiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                    } else {
                                        showSumShiQiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    }

                                    // 获取视图在屏幕上的绝对位置
                                    val location = IntArray(2)
                                    showSumShiQiMoney.getLocationOnScreen(location)
                                    val xOnScreen = location[0]
                                    val yOnScreen = location[1]


                                    //通过显示的控件得到相对于屏幕的位置
                                    var  rax=xOnScreen
                                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSumShiQiMoney.ivShowBg)
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeSum.sumTotalShiQi.viewXYTemporary[0]==0&&ComputeSum.sumTotalShiQi.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showSumShiQiMoney.getLocationInWindow(location)

                                        ComputeSum.sumTotalShiQi.viewXYTemporary[0]= showSumShiQiMoney.left
                                        ComputeSum.sumTotalShiQi.viewXYTemporary[1]=showSumShiQiMoney.top
                                    }
                                }
                            })

                            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            mDatabind.rlHomeRoot.addView(showSumShiQiMoney, params)

                            showSumShiQiMoney.translationX =  ComputeSum.sumTotalShiQi.viewXYTemporary[0].toFloat()
                            showSumShiQiMoney.translationY =  ComputeSum.sumTotalShiQi.viewXYTemporary[1].toFloat()
                        }



                    }

                }

            }

            false // 返回 true 表示事件已经被处理
        }

    }
}