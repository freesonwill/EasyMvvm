package com.xcjh.app.view

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.cling.entity.ClingDevice
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.app.R
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.databinding.ItemNewsListBinding
import com.xcjh.app.databinding.ItemSelectProjectionBinding
import com.xcjh.base_lib.utils.view.clickNoRepeat

class PopupSelectProjection(context: Context) : BottomPopupView(context)  {
    var rcvRecommend:RecyclerView?=null
    var loading: LottieAnimationView?=null
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_select_projection
    }


    override fun onCreate() {
        super.onCreate()

        rcvRecommend=findViewById<RecyclerView>(R.id.rcvRecommend)
        loading=findViewById<LottieAnimationView>(R.id.loading)
        var txtDialogCancel=findViewById<AppCompatTextView>(R.id.txtDialogCancel)
        txtDialogCancel.clickNoRepeat{
            popupSelectProjectionListener!!.clickClose()
            dismiss()
        }
        rcvRecommend!!.linear().setup {
            addType<ClingDevice>(R.layout.item_select_projection)
            onBind {
                when (itemViewType) {
                    R.layout.item_select_projection -> {
                        var bindingItem=getBinding<ItemSelectProjectionBinding>()
                        var  bean=_data as ClingDevice
                        bindingItem.txtSelectTxt.text=bean.name

                    }

                }

            }
            R.id.txtSelectTxt.onClick {
                var  bean=_data as ClingDevice
                popupSelectProjectionListener!!.clickDevice(bean)
            }

        }.addModels(arrayListOf())


    }

    fun setDate(list:ArrayList<ClingDevice>){
        if(loading!=null&&list.size>0){
            if( rcvRecommend!!.models!=null){
                rcvRecommend!!.mutable.clear()
            }
            rcvRecommend!!.visibility=View.VISIBLE
            rcvRecommend!!.addModels(list)
            rcvRecommend!!.bindingAdapter.notifyDataSetChanged()

        }

        if(loading!=null){
            loading!!.visibility=View.GONE
        }


    }

    override fun dismiss() {
        popupSelectProjectionListener?.onDisappear()
        super.dismiss()
    }

    var popupSelectProjectionListener: PopupSelectProjectionListener?=null

    //点击事件
    interface  PopupSelectProjectionListener{
        //手动关闭
        fun  clickClose()
        //点击设备
        fun  clickDevice(date :ClingDevice)
        //整个关闭的回调
        fun  onDisappear()
    }
}