package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.skin.widget.SportButton
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
        initFlipper()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    private fun initFlipper() {
        val list = mViewModel.getFlipperData()
        for (item in list) {
            val layout = LayoutInflater.from(context)
                .inflate(R.layout.item_flipper, null) as SportLinearLayout
            val tvType = layout.findViewById<SportTextView>(R.id.tv_type)
            val tvQuestion = layout.findViewById<SportTextView>(R.id.tv_question)
            val tvScore = layout.findViewById<SportTextView>(R.id.tv_score)
            val tvHome = layout.findViewById<SportTextView>(R.id.tv_home_team)
            val tvAway = layout.findViewById<SportTextView>(R.id.tv_away_team)
            val tvLeft = layout.findViewById<SportTextView>(R.id.tv_left)
            val tvRight = layout.findViewById<SportTextView>(R.id.tv_right)
            val ivLeft = layout.findViewById<SportImageView>(R.id.iv_left)
            val ivRight = layout.findViewById<SportImageView>(R.id.iv_right)
            val layLeft = layout.findViewById<SportLinearLayout>(R.id.line_left)
            val layRight = layout.findViewById<SportLinearLayout>(R.id.line_right)
            val layContent = layout.findViewById<SportLinearLayout>(R.id.line_content)
            val ivTip = layout.findViewById<SportImageView>(R.id.iv_tip)
            val tvTip = layout.findViewById<SportTextView>(R.id.tv_tip)
            val tvMsg = layout.findViewById<SportTextView>(R.id.tv_msg)
            val btnNext = layout.findViewById<SportButton>(R.id.btn_next)
            tvType.text = item.type
            tvQuestion.text = item.question
            tvScore.text = item.score
            tvHome.text = item.homeName
            tvAway.text = item.awayName
            tvLeft.text = item.left
            tvRight.text = item.right
            btnNext.text = item.btnText
            layLeft.setOnClickListener {
                layLeft.isSelected = true
                layRight.isSelected = false
                ivLeft.visibility = View.VISIBLE
                ivRight.visibility = View.GONE
                tvTip.text = getString(R.string.tip_right)
                tvMsg.text = item.leftMsg
                ivTip.background =
                    AppCompatResources.getDrawable(mBinding.root.context, R.drawable.icon_right)
                layContent.visibility = View.VISIBLE
            }
            layRight.setOnClickListener {
                layLeft.isSelected = false
                layRight.isSelected = true
                ivLeft.visibility = View.GONE
                ivRight.visibility = View.VISIBLE
                tvTip.text = getString(R.string.tip_error)
                tvMsg.text = item.rightMsg
                ivTip.background =
                    AppCompatResources.getDrawable(mBinding.root.context, R.drawable.icon_error)
                layContent.visibility = View.VISIBLE
            }
            btnNext.setOnClickListener {
                if (item.id < 3) {
                    layLeft.isSelected = false
                    layRight.isSelected = false
                    ivLeft.visibility = View.GONE
                    ivRight.visibility = View.GONE
                    layContent.visibility = View.GONE
                    mBinding.viewFlipper.showNext()
                } else {
                    //第三个页面的按钮后面需要修改为点击跳转投注页
                    findNavController().navigateUp()
                }

            }
            mBinding.viewFlipper.addView(layout)
        }
    }
}