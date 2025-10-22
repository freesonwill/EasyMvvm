package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.bumptech.glide.Glide
import com.google.android.material.shape.MaterialShapeDrawable
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.databinding.FragmentGamePreviewImageBinding
import com.walisport.module.gamedetail.ui.viewmodel.GamePreviewImageViewModel
import kotlin.reflect.KClass

class GamePreviewImageFragment(private val url: String) :
    BaseFragment<GamePreviewImageViewModel, FragmentGamePreviewImageBinding>() {
    override val vbClass: KClass<FragmentGamePreviewImageBinding> = FragmentGamePreviewImageBinding::class
    override val vmClass: KClass<GamePreviewImageViewModel> = GamePreviewImageViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.tag = arguments?.getInt(ARG_ADAPTER_POSITION, -1) ?: -1
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.apply {
            background = MaterialShapeDrawable().apply { setCornerSize(20.dp2px.toFloat()) }
            clipToOutline = true
            Glide.with(requireContext()).load(R.mipmap.img_game_preview).into(this)
            // todo 待串接真實資料
            // Glide.with(requireContext()).load(url).into(this)
        }
    }

    override fun initListener() = Unit

    override suspend fun createObserver() = Unit

    companion object {
        private const val ARG_ADAPTER_POSITION = "ARG_ADAPTER_POSITION"

        fun newInstance(position: Int, url: String): GamePreviewImageFragment {
            val fragment = GamePreviewImageFragment(url)
            fragment.arguments = Bundle().apply {
                putInt(ARG_ADAPTER_POSITION, position)
            }
            return fragment
        }
    }
}