package com.walisport.module.live.ui

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import com.bumptech.glide.Glide
import com.walisport.module.live.databinding.FragmentMatchStatusBinding
import com.walisport.module.live.ui.viewmodel.MatchStatusViewModel
import kotlin.reflect.KClass


/**
 * 比赛状态页面， 用在比赛没有进行时（未开赛，已结束等状态）的信息展示
 */
class MatchStatusFragment : BaseFragment<MatchStatusViewModel, FragmentMatchStatusBinding>() {
    override val vbClass: KClass<FragmentMatchStatusBinding> = FragmentMatchStatusBinding::class
    override val vmClass: KClass<MatchStatusViewModel> = MatchStatusViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
    }


    override fun initListener() {

    }

    override fun createObserver() {
        with(mViewModel) {
            homeTeamName.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.tvHomeTeam.text = it
                }
            }

            homeTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.common.R.color.color_333A45)
                        .error(arch.cayenne.lib.common.R.color.color_333A45)
                        .into(mBinding.ivHomeTeam)
                }
            }

            homeHistoryVs.observe(viewLifecycleOwner) {
//                "homeList:$it".logd("scoreIssue")
                mBinding.homeHistory.setData(it)
            }

            awayTeamName.observe(viewLifecycleOwner) {
                it?.let { mBinding.tvAwayTeam.text = it }
            }

            awayTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.common.R.color.color_333A45)
                        .error(arch.cayenne.lib.common.R.color.color_333A45)
                        .into(mBinding.ivAwayTeam)
                }
            }

            awayHistoryVs.observe(viewLifecycleOwner) {
//                "awayList:$it".logd("scoreIssue")
                mBinding.awayHistory.setData(it)
            }

            titleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.tvTitle.text = it }
            }

            titleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.tvTitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }

            titleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.tvTitle.setTextColor(it.getColor()) }
            }

            subTitleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.tvSubtitle.text = it }
            }

            subTitleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.tvSubtitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }
            subTitleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.tvSubtitle.setTextColor(it.getColor()) }
            }


        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }


    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    companion object {
        const val TAG = "MatchStatusFragment"
    }


}