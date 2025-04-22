package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.skin.widget.SportImageView
import arch.cayenne.lib.skin.widget.SportLinearLayout
import arch.cayenne.lib.skin.widget.SportTextView
import arch.cayenne.module.handicap.databinding.FragmentSimulateBinding
import arch.cayenne.module.handicap.ui.viewmodel.SimulateViewModel
import kotlin.reflect.KClass
import arch.cayenne.module.handicap.R

/**
 * 模拟投注页面
 */

class SimulateFragment : BaseFragment<SimulateViewModel, FragmentSimulateBinding>() {

    override val vbClass: KClass<FragmentSimulateBinding> = FragmentSimulateBinding::class
    override val vmClass: KClass<SimulateViewModel> = SimulateViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.simulate_bet.getString(), {
            findNavController().navigateUp()
        })
    }

    override fun initListener() {
        mBinding.btnNext.setOnClickListener {
            mBinding.viewFlipper.showNext()
        }
        mBinding.btnNextTwo.setOnClickListener {
            mBinding.viewFlipper.showNext()
        }
        mBinding.btnNextThree.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.lineLeftOne.setOnClickListener {
            setRadioButtonChecked(
                true,
                mBinding.ivRightOne,
                mBinding.ivWrongOne,
                mBinding.lineLeftOne,
                mBinding.lineRightOne,
                mBinding.laySubmit,
                mBinding.ivTip,
                mBinding.tvTip,
                mBinding.tvMsgOne,
                getString(R.string.msg_one_left)
            )
        }
        mBinding.lineRightOne.setOnClickListener {
            setRadioButtonChecked(
                false,
                mBinding.ivRightOne,
                mBinding.ivWrongOne,
                mBinding.lineLeftOne,
                mBinding.lineRightOne,
                mBinding.laySubmit,
                mBinding.ivTip,
                mBinding.tvTip,
                mBinding.tvMsgOne,
                getString(R.string.msg_one_right)
            )
        }
        mBinding.lineLeftTwo.setOnClickListener {
            setRadioButtonChecked(
                true,
                mBinding.ivRightTwo,
                mBinding.ivWrongTwo,
                mBinding.lineLeftTwo,
                mBinding.lineRightTwo,
                mBinding.laySubmitTwo,
                mBinding.ivTipTwo,
                mBinding.tvTipTwo,
                mBinding.tvMsgTwo,
                getString(R.string.msg_two_left)
            )
        }
        mBinding.lineRightTwo.setOnClickListener {
            setRadioButtonChecked(
                false,
                mBinding.ivRightTwo,
                mBinding.ivWrongTwo,
                mBinding.lineLeftTwo,
                mBinding.lineRightTwo,
                mBinding.laySubmitTwo,
                mBinding.ivTipTwo,
                mBinding.tvTipTwo,
                mBinding.tvMsgTwo,
                getString(R.string.msg_two_right)
            )
        }
        mBinding.lineLeftThree.setOnClickListener {
            setRadioButtonChecked(
                true,
                mBinding.ivRightThree,
                mBinding.ivWrongThree,
                mBinding.lineLeftThree,
                mBinding.lineRightThree,
                mBinding.laySubmitThree,
                mBinding.ivTipThree,
                mBinding.tvTipThree,
                mBinding.tvMsgThree,
                getString(R.string.msg_three_left)
            )
        }
        mBinding.lineRightThree.setOnClickListener {
            setRadioButtonChecked(
                false,
                mBinding.ivRightThree,
                mBinding.ivWrongThree,
                mBinding.lineLeftThree,
                mBinding.lineRightThree,
                mBinding.laySubmitThree,
                mBinding.ivTipThree,
                mBinding.tvTipThree,
                mBinding.tvMsgThree,
                getString(R.string.msg_three_right)
            )
        }
    }

    private fun setRadioButtonChecked(
        isLeft: Boolean,
        ivLeft: SportImageView,
        ivRight: SportImageView,
        left: SportLinearLayout,
        right: SportLinearLayout,
        submit: SportLinearLayout,
        ivTip: SportImageView,
        tvTip: SportTextView,
        tvMsg: SportTextView,
        strMsg: String
    ) {
        if (isLeft) {
            left.isSelected = true
            right.isSelected = false
            ivLeft.visibility = View.VISIBLE
            ivRight.visibility = View.GONE
            tvTip.text = getString(R.string.tip_right)
            ivTip.background =
                AppCompatResources.getDrawable(mBinding.root.context, R.drawable.icon_right)

        } else {
            left.isSelected = false
            right.isSelected = true
            ivLeft.visibility = View.GONE
            ivRight.visibility = View.VISIBLE
            tvTip.text = getString(R.string.tip_error)
            ivTip.background =
                AppCompatResources.getDrawable(mBinding.root.context, R.drawable.icon_error)
        }
        tvMsg.text = strMsg
        submit.visibility = View.VISIBLE
    }

    override fun createObserver() {

    }
}