package arch.cayenne.module.account.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.popBackStack
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.constants.KeyConfig
import arch.cayenne.module.account.databinding.FragmentPersonalInfoBinding
import arch.cayenne.module.account.databinding.TitleBarPersonalInfoBinding
import arch.cayenne.module.account.ui.adapter.PersonalInfoAdapter
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午4:19
 * @description:
 */
class  PersonalInfoFragment : BaseFragment<PersonalInfoViewModel, FragmentPersonalInfoBinding>() {
    override val vbClass: KClass<FragmentPersonalInfoBinding> = FragmentPersonalInfoBinding::class
    override val vmClass: KClass<PersonalInfoViewModel> = PersonalInfoViewModel::class
    private val titleBarBinding: TitleBarPersonalInfoBinding by lazy {
        TitleBarPersonalInfoBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private var personalInfoAdapter = PersonalInfoAdapter()
    override fun initView(savedInstanceState: Bundle?) {
        val layoutManager = object : GridLayoutManager(context, 4) {
            override fun canScrollVertically() = false
        }
        with(mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root)
            rvPersonalHeadGrid.layoutManager = layoutManager
            val spanCount = 4
            val spacingOutSide = 34.dp2px
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
            personalInfoAdapter.setOnItemClickListener { _ ->
                if (ceNickName.text?.isNotEmpty() == true && (ceNickName.text?.length ?: 0) <= 8) {
                    btnSave.isEnabled = true
                }
            }
            val defaultNickName = mViewModel.getDefaultNickName()
            if (defaultNickName.isEmpty()) {
                ceNickName.isEnabled = true
                btnSave.isEnabled = false
            } else {
                ceNickName.isEnabled = false
                ceNickName.setText(defaultNickName)
                btnSave.isEnabled = mViewModel.getDefaultPosition() != -1
            }
        }
    }

    override fun initListener() {
        with (mBinding) {
            btnSave.clickNoRepeat {
                with (mBinding) {
                    if ((ceNickName.text?.length ?: 0) > 8) {
                        return@clickNoRepeat
                    }
                    if (ceNickName.text.isNullOrEmpty()) {
                        return@clickNoRepeat
                    } else {

                        mViewModel.saveData(
                            ceNickName.text.toString(),
                            personalInfoAdapter.getSelectedResId(),
                            personalInfoAdapter.getSelectedPosition()
                        )
                        val resultBundle = Bundle().apply {
                            putBoolean(KeyConfig.VALUE_SAVE_NICKNAME, true)
                        }

                        // 設定結果，requestKey 必須與監聽器的 key 相同
                        requireActivity().supportFragmentManager.setFragmentResult(KeyConfig.VALUE_NICKNAME_RESULT, resultBundle)
                        // 關閉自己，返回上一頁
                        popBackStack()
                    }
                }
            }
            ceNickName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No action needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // No action needed
                    if (s.isNullOrEmpty()) {
                        tvNickNameModifyOnce.text = resources.getString(R.string.nick_name_modify_once)
                        tvNickNameModifyOnce.setTextColor(SkinnableResourceManager.getColor( requireContext(),arch.cayenne.lib.common.R.color.secondary_text))
                        tvNickNameLength.text = ""
                        btnSave.isEnabled = false
                    } else if (s.length > 8) {
                        tvNickNameModifyOnce.text = resources.getString(R.string.nick_name_cannot_exceed_8_char)
                        tvNickNameModifyOnce.setTextColor(resources.getColor(arch.cayenne.lib.common.R.color.red_team,null))
                        tvNickNameLength.text = resources.getString(R.string.nick_name_char_num, s.length)
                        btnSave.isEnabled = false
                    } else {
                        tvNickNameModifyOnce.text = resources.getString(R.string.nick_name_modify_once)
                        tvNickNameModifyOnce.setTextColor(SkinnableResourceManager.getColor( requireContext(),arch.cayenne.lib.common.R.color.secondary_text))
                        tvNickNameLength.text = resources.getString(R.string.nick_name_char_num, s.length)
                        btnSave.isEnabled = personalInfoAdapter.getSelectedPosition() != -1
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {
                    // No action needed
                }
            })
        }
    }

    override fun createObserver() {
//        TODO("Not yet implemented")
    }
}