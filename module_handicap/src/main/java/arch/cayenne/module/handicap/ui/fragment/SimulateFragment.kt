package arch.cayenne.module.handicap.ui.fragment

import android.graphics.drawable.Drawable
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
        val size = list.size
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
            layLeft.background = getLayoutBackground(item.isRight)
            layRight.background = getLayoutBackground(!item.isRight)
            ivLeft.background = getImageBackground(item.isRight)
            ivRight.background = getImageBackground(!item.isRight)
            layLeft.setOnClickListener {
                layLeft.isSelected = true
                layRight.isSelected = false
                ivLeft.visibility = View.VISIBLE
                ivRight.visibility = View.GONE
                tvTip.text = getTipText(item.isRight)
                ivTip.background = getTipBackground(item.isRight)
                tvMsg.text = item.leftMsg
                layContent.visibility = View.VISIBLE
            }
            layRight.setOnClickListener {
                layLeft.isSelected = false
                layRight.isSelected = true
                ivLeft.visibility = View.GONE
                ivRight.visibility = View.VISIBLE
                tvTip.text = getTipText(!item.isRight)
                ivTip.background = getTipBackground(!item.isRight)
                tvMsg.text = item.rightMsg
                layContent.visibility = View.VISIBLE
            }
            if (item.id < size) {
                val tip = getString(R.string.next_question)
                btnNext.text = String.format("%s(%s/%s)", tip, item.id, list.size)
            } else {
                btnNext.text = getString(R.string.go_to_bet)
            }
            btnNext.setOnClickListener {
                if (item.id < size) {
                    layLeft.isSelected = false
                    layRight.isSelected = false
                    ivLeft.visibility = View.GONE
                    ivRight.visibility = View.GONE
                    layContent.visibility = View.GONE
                    mBinding.viewFlipper.showNext()
                } else {
                    //后面需要修改为点击跳转投注页
                    findNavController().navigateUp()
                }
            }
            mBinding.viewFlipper.addView(layout)
        }
    }

    private fun getImageBackground(
        isRight: Boolean
    ): Drawable? {
        return if (isRight)
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.icon_answer_right
            )
        else
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.icon_answer_error
            )
    }

    private fun getLayoutBackground(
        isRight: Boolean
    ): Drawable? {
        return if (isRight)
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.selector_submit_right
            )
        else
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.selector_submit_wrong
            )
    }

    private fun getTipBackground(
        isRight: Boolean
    ): Drawable? {
        return if (isRight)
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.icon_right
            )
        else
            AppCompatResources.getDrawable(
                mBinding.root.context,
                R.drawable.icon_error
            )
    }

    private fun getTipText(isRight: Boolean): String {
        return if (isRight) getString(R.string.tip_right) else getString(R.string.tip_error)
    }
}