package arch.cayenne.module.account.ui.fragment

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.FileUtils
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import arch.cayenne.module.account.databinding.FragmentSystemAvatarBinding
import arch.cayenne.module.account.ui.adapter.PersonalInfoAdapter
import kotlin.reflect.KClass
import arch.cayenne.module.account.R
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SystemAvatarFragment : BaseFragment<SystemAvatarViewModel, FragmentSystemAvatarBinding>() {
    override val vbClass: KClass<FragmentSystemAvatarBinding> = FragmentSystemAvatarBinding::class
    override val vmClass: KClass<SystemAvatarViewModel> = SystemAvatarViewModel::class
    private var personalInfoAdapter = PersonalInfoAdapter()
    private val personalViewModel: PersonalInfoViewModel by sharedViewModel<PersonalInfoViewModel, PersonalInfoFragment>()
    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }


    private var avatarCheckable = false
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
            findNavController().navigateUp()
        }

        mBinding.tvSave.clickNoRepeat {
            personalInfoAdapter.setIsUpAvatar(false)
            saveBitmap()
        }
        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params
        val layoutManager = object : GridLayoutManager(context, 5) {
            override fun canScrollVertically() = false
        }
        with(mBinding) {
            rvPersonalHeadGrid.layoutManager = layoutManager
            val spanCount = 5
            val spacingTop = 0.dp2px
            val spacingBottom = 15.dp2px
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
                    outRect.top = if (row == 0) {
                        spacingTop
                    } else {
                        0
                    }
                    outRect.bottom = if (row == 0) {
                        spacingBottom
                    } else {
                        spacingTop
                    }
                }
            })
            rvPersonalHeadGrid.adapter = personalInfoAdapter
            (rvPersonalHeadGrid?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            personalInfoAdapter.setOnItemClickListener { data ->
                avatarCheckable = true
                mBinding.ivUserAvatar.setRotationAngle(0f)
                // 加载网络图片并高斯模糊后设置为背景
                Glide.with(this@SystemAvatarFragment)
                    .asBitmap()
                    .load(data.host + data.url.trim())
                    .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                        override fun onResourceReady(bit: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                            var blurBit = bit
                            // 先放大再高斯模糊，提升磨砂自然度
                            val blurRadius = 25f
                            val blurSize = 1200 // 先放大到更高分辨率
                            val scaledBitmap = Bitmap.createScaledBitmap(blurBit, blurSize, blurSize, true)
                            val rs = android.renderscript.RenderScript.create(requireContext())
                            val input = android.renderscript.Allocation.createFromBitmap(rs, scaledBitmap)
                            val output = android.renderscript.Allocation.createTyped(rs, input.type)
                            val script = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs))
                            script.setRadius(blurRadius)
                            script.setInput(input)
                            script.forEach(output)
                            output.copyTo(scaledBitmap)
                            rs.destroy()
                            // 再缩放回目标尺寸
                            val blurredBitmap = Bitmap.createScaledBitmap(scaledBitmap, blurBit.width, blurBit.height, true)
                            mBinding.backgroundImage.setImageBitmap(blurredBitmap)
                            mBinding.ivUserAvatar.setImageBitmap(bit)
                        }
                        override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                        }
                    })

            }
        }
        mBinding.tvSave.isSelected = true
        mBinding.tvSave.isClickable = true
    }

    override fun initData() {
        mViewModel.getPersonalInfoData()

        super.initData()
    }

    private fun saveBitmap() {
        mBinding.tvSave.isSelected = false
        mBinding.tvSave.isClickable = false
        mBinding.tvSave.text = R.string.account_avatar_up.getString()
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = mBinding.ivUserAvatar.getRotatedBitmap()
            if (bitmap != null) {
                val filePath = FileUtils.saveBitmapToFile(requireContext(), bitmap)
                withContext(Dispatchers.Main) {
                    // 回收 Bitmap
                    bitmap.recycle()
                    if (filePath != null) {
                        mViewModel.uploadAvatar(filePath)
//                        val result = Bundle().apply {
//                            putString("filePath", filePath)
//                        }
//                        sendResult(CHANGE_FILE_PATH, result)
//                        navigateUp()
                    } else {
                        personalInfoAdapter.setIsUpAvatar(true)
                        mBinding.tvSave.isSelected = true
                        mBinding.tvSave.isClickable = true
                        mBinding.tvSave.text = R.string.personal_avatar_save.getString()
                        Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    personalInfoAdapter.setIsUpAvatar(true)
                    Toast.makeText(requireContext(), "无法获取图像", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun initListener() {
        titleBarBinding.ivRotate.clickNoRepeat {
            if (!avatarCheckable) return@clickNoRepeat
            mBinding.ivUserAvatar.setRotationAngle((mBinding.ivUserAvatar.getRotationAngle() + 90f) % 360f)
        }
    }

    override suspend fun createObserver() {
        mViewModel.systemAvatarList.observe(viewLifecycleOwner) { list ->
            personalInfoAdapter.submitList(list)
        }
        mViewModel.uploadResult.observe(viewLifecycleOwner) { success ->
            if (success.isNotEmpty()) {
                Toast.makeText(requireContext(), "上传成功", Toast.LENGTH_SHORT).show()
                personalViewModel.setUploadResul(success)
                navigateUp()
            } else {
                Toast.makeText(requireContext(), "上传失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

}