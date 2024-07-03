package com.xcjh.app.view

import android.content.Context
import android.view.View
import android.widget.TextView
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.app.R
import com.xcjh.app.bean.AnchorListBean
import com.xcjh.app.databinding.DialogSignalListBinding
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 弹出框
 */
class SignalPopupList (context: Context, var  anchorList: List<AnchorListBean>) : BottomPopupView(context)  {
    private lateinit var mDatabind: DialogSignalListBinding

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_signal_list
    }

    override fun onCreate() {
        super.onCreate()
        mDatabind = DialogSignalListBinding.bind(findViewById<View>(R.id.rlRoot))
        mDatabind.tvCancel.clickNoRepeat {
            dismiss()
        }
        var pos = 0
        if (anchorList != null) {
            for ((i, item) in anchorList.withIndex()) {
                if (item.isSelect) {
                    pos = i
                    break
                }
            }
        }

        mDatabind.rcvSignal.setup {
            addType<AnchorListBean> { R.layout.item_signal }

            onBind {
                val model = getModel<AnchorListBean>()
                findView<TextView>(R.id.tvContent).apply {
                    if (model.isSelect) {
                        this.setTextColor(context.getColor(R.color.c_34a853))
                        paint?.isFakeBoldText = true
                    } else {
                        this.setTextColor(context.getColor(R.color.c_37373d))
                        paint?.isFakeBoldText = false
                    }
                    this.text =
                        if (model.pureFlow) model.nickName else model.nickName.ifEmpty {
                            context.getString(R.string.anchor) + (modelPosition + 1)
                        }
                }
            }
            onClick(R.id.lltItem) {
                val model = getModel<AnchorListBean>()
//                action.invoke(model, modelPosition)
                signalPopupListListener?.onSelect(model, modelPosition)
                 dismiss()
            }
        }.models = anchorList
        mDatabind.rcvSignal.scrollToPosition(pos)

    }

    override fun dismiss() {
        signalPopupListListener?.onDisappear()
        super.dismiss()
    }
    var signalPopupListListener: SignalPopupListListener?=null
    interface  SignalPopupListListener{

        fun onSelect(anchor :AnchorListBean, poe: Int)

        //整个关闭的回调
        fun  onDisappear()
    }
}