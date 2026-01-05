package arch.cayenne.module.account.ui.fragment

import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BASE_URL
import arch.cayenne.lib.common.utils.FileUtils
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentAvatarPreviewBinding
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import arch.cayenne.module.account.ui.viewmodel.AvatarPreviewViewModel
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CUSTOM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

class AvatarPreviewFragment : BaseFragment<AvatarPreviewViewModel, FragmentAvatarPreviewBinding>() {

    override val vbClass: KClass<FragmentAvatarPreviewBinding> = FragmentAvatarPreviewBinding::class
    override val vmClass: KClass<AvatarPreviewViewModel> = AvatarPreviewViewModel::class
    private val personalViewModel: PersonalInfoViewModel by sharedViewModel<PersonalInfoViewModel, PersonalInfoFragment>()

    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val uri = arguments?.getString("uri")
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
            findNavController().navigateUp()
        }
            FileUtils.getBitmapFromUriAsync(
                requireContext(),
                Uri.parse(uri)
            ) {
                mBinding.ivUserAvatar.apply {
                    maxScale = 10f
                    minScale = 0.8f               // ← 0.1f 通常太小，容易出 NaN 或顯示異常
                    setMinimumScaleType(SCALE_TYPE_CUSTOM)
                    mBinding.ivUserAvatar.setBitmaps(it)
                }

//            // 设置模糊背景
//            Glide.with(this)
//                .load(Uri.parse(uri))
//                .transform(BlurTransformation(25, 4))
//                .into(mBinding.backgroundImage)
        }

        mBinding.titleBar.post {
            titleBarBinding.tvTitle.text = getString(R.string.title_preview)
        }

        titleBarBinding.ivRotate.clickNoRepeat {
            mBinding.ivUserAvatar.rotate()
        }
        mBinding.tvSave.isSelected = true
        mBinding.tvSave.isClickable = true

    }

    override fun initListener() {
        mBinding.tvSave.clickNoRepeat {
            saveBitmap()
        }
    }

    override suspend fun createObserver() {
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

    private fun saveBitmap() {
        mBinding.tvSave.isSelected = false
        mBinding.tvSave.isClickable = false
        mBinding.tvSave.text = R.string.account_avatar_up.getString()
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = mBinding.ivUserAvatar.captureCircularContentAsBitmap()
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
                        mBinding.tvSave.isSelected = true
                        mBinding.tvSave.isClickable = true
                        mBinding.tvSave.text = R.string.account_avatar_confirm.getString()
                        Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "无法获取图像", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}