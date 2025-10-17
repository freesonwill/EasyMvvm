package arch.cayenne.module.account.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import arch.cayenne.module.account.databinding.FragmentSystemAvatarBinding
import arch.cayenne.module.account.ui.adapter.PersonalInfoAdapter
import kotlin.reflect.KClass
import arch.cayenne.module.account.R
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
class SystemAvatarFragment : BaseFragment<SystemAvatarViewModel, FragmentSystemAvatarBinding>() {
    override val vbClass: KClass<FragmentSystemAvatarBinding> = FragmentSystemAvatarBinding::class
    override val vmClass: KClass<SystemAvatarViewModel> = SystemAvatarViewModel::class
    private var personalInfoAdapter = PersonalInfoAdapter()

    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root){
            findNavController().navigateUp()
        }
        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params
        val layoutManager = object : GridLayoutManager(context, 4) {
            override fun canScrollVertically() = false
        }
        with(mBinding) {
            rvPersonalHeadGrid.layoutManager = layoutManager
            val spanCount = 4
            val spacingTop = 16.dp2px
            val spacingBottom = 24.dp2px
            rvPersonalHeadGrid.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == RecyclerView.NO_POSITION) return
                    outRect.left = spacingTop
                    val row = position / spanCount
                    outRect.top = if (row == 0) { spacingTop } else { 0 }
                    outRect.bottom = if (row == 0) {spacingBottom} else { spacingTop }
                }
            })
            rvPersonalHeadGrid.adapter = personalInfoAdapter
            (rvPersonalHeadGrid?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            personalInfoAdapter.submitList(mViewModel.getPersonalInfoData())
            personalInfoAdapter.setSelectedPosition(mViewModel.getDefaultPosition())
            personalInfoAdapter.setOnItemClickListener { data ->
                mBinding.ivUserAvatar.setImageResource(data.resId)
//                if (ceNickName.text?.isNotEmpty() == true && (ceNickName.text?.length ?: 0) <= nickNameMaxLength) {
//                    btnSave.isEnabled = true
//                }
            }
            val defaultNickName = mViewModel.getDefaultNickName()
//            if (defaultNickName.isEmpty()) {
//                ceNickName.isEnabled = true
//                btnSave.isEnabled = false
//            } else {
//                ceNickName.isEnabled = false
//                ceNickName.setText(defaultNickName)
//                btnSave.isEnabled = mViewModel.getDefaultPosition() != -1
//            }
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

}