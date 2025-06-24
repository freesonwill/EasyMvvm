package arch.cayenne.module.handicap.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.handicap.R
import arch.cayenne.module.handicap.databinding.FragmentSimulateBinding
import arch.cayenne.module.handicap.databinding.ItemFlipperBinding
import arch.cayenne.module.handicap.ui.viewmodel.SimulateViewModel
import kotlin.reflect.KClass

/**
 * 模拟投注页面
 */

class SimulateFragment : BaseFragment<SimulateViewModel, FragmentSimulateBinding>() {

    override val vbClass: KClass<FragmentSimulateBinding> = FragmentSimulateBinding::class
    override val vmClass: KClass<SimulateViewModel> = SimulateViewModel::class

    private val args : HandicapFragmentArgs by navArgs()

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
            val itemBinding = ItemFlipperBinding.inflate(LayoutInflater.from(context))
            itemBinding.tvType.text = item.type
            itemBinding.tvQuestion.text = item.question
            itemBinding.tvScore.text = item.score
            itemBinding.tvHomeTeam.text = item.homeName
            itemBinding.tvAwayTeam.text = item.awayName
            itemBinding.tvLeft.text = item.left
            itemBinding.tvRight.text = item.right
            itemBinding.layLeft.background = getLayoutBackground(item.isRight)
            itemBinding.layRight.background = getLayoutBackground(!item.isRight)
            itemBinding.ivLeft.background = getImageBackground(item.isRight)
            itemBinding.ivRight.background = getImageBackground(!item.isRight)
            itemBinding.layLeft.setOnClickListener {
                itemBinding.layLeft.isSelected = true
                itemBinding.layRight.isSelected = false
                itemBinding.tvLeft.isSelected = true
                itemBinding.tvRight.isSelected = false
                itemBinding.ivLeft.visibility = View.VISIBLE
                itemBinding.ivRight.visibility = View.GONE
                itemBinding.tvTip.text = getTipText(item.isRight)
                itemBinding.ivTip.background = getTipBackground(item.isRight)
                itemBinding.tvMsg.text = item.leftMsg
                itemBinding.layContent.visibility = View.VISIBLE
            }
            itemBinding.layRight.setOnClickListener {
                itemBinding.layLeft.isSelected = false
                itemBinding.layRight.isSelected = true
                itemBinding.tvLeft.isSelected = false
                itemBinding.tvRight.isSelected = true
                itemBinding.ivLeft.visibility = View.GONE
                itemBinding.ivRight.visibility = View.VISIBLE
                itemBinding.tvTip.text = getTipText(!item.isRight)
                itemBinding.ivTip.background = getTipBackground(!item.isRight)
                itemBinding.tvMsg.text = item.rightMsg
                itemBinding.layContent.visibility = View.VISIBLE
            }
            if (item.id < size) {
                val tip = getString(R.string.next_question)
                itemBinding.btnNext.text = String.format("%s(%s/%s)", tip, item.id, list.size)
            } else {
                itemBinding.btnNext.text = getString(R.string.go_to_bet)
            }
            itemBinding.btnNext.setOnClickListener {
                if (item.id < size) {
                    itemBinding.layLeft.isSelected = false
                    itemBinding.layRight.isSelected = false
                    itemBinding.ivLeft.visibility = View.GONE
                    itemBinding.ivRight.visibility = View.GONE
                    itemBinding.layContent.visibility = View.GONE
                    mBinding.viewFlipper.showNext()
                } else {
                    sendResult("Drawer", "Close", args.homeId)
                    findNavController().popBackStack(args.homeId, false)
                }
            }
            mBinding.viewFlipper.addView(itemBinding.root)
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
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.selector_submit_right
            )
        else
            SkinnableResourceManager.getDrawable(
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