package arch.cayenne.module.account.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.FileUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentAvatarPreviewBinding
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import arch.cayenne.module.account.ui.viewmodel.AvatarPreviewViewModel
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.module.account.ui.fragment.AvatarFragment.Companion.CHANGE_FILE_PATH

class AvatarPreviewFragment : BaseFragment<AvatarPreviewViewModel, FragmentAvatarPreviewBinding>() {

    override val vbClass: KClass<FragmentAvatarPreviewBinding> = FragmentAvatarPreviewBinding::class
    override val vmClass: KClass<AvatarPreviewViewModel> = AvatarPreviewViewModel::class
    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val uri = arguments?.getString("uri")
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
            findNavController().navigateUp()
        }
        mBinding.ivUserAvatar.post {
            mBinding.ivUserAvatar.maxScale = 10f
            mBinding.ivUserAvatar.minScale = 1f
            mBinding.ivUserAvatar.setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
            mBinding.ivUserAvatar.setBitmaps(FileUtils.getBitmapFromUri(requireContext(),Uri.parse(uri)))
//            // 设置模糊背景
//            Glide.with(this)
//                .load(Uri.parse(uri))
//                .transform(BlurTransformation(25, 4))
//                .into(mBinding.backgroundImage)
        }

        mBinding.titleBar.post {
            titleBarBinding.tvTitle.text = getString(R.string.title_preview)
        }

        titleBarBinding.ivRotate.clickNoRepeat{
            mBinding.ivUserAvatar.rotate()
        }
    }

    override fun initListener() {
        mBinding.tvSave.clickNoRepeat {
            saveBitmap()
        }
    }

    override suspend fun createObserver() {

    }

    private fun saveBitmap() {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = mBinding.ivUserAvatar.captureCircularContentAsBitmap()
            if (bitmap != null) {
                val filePath = FileUtils.saveBitmapToFile(requireContext(),bitmap)
                withContext(Dispatchers.Main) {
                    // 回收 Bitmap
                    bitmap.recycle()
                    if (filePath != null) {
                        val result = Bundle().apply {
                            putString("filePath", filePath)
                        }
                        sendResult(CHANGE_FILE_PATH, result)
                        navigateUp()
                    } else {
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