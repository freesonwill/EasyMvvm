package arch.cayenne.module.account.ui.fragment

import android.Manifest
import android.companion.CompanionDeviceManager.RESULT_OK
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.ui.viewmodel.AvatarViewModel
import arch.cayenne.module.account.databinding.FragmentAvatarBinding
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.module.account.databinding.TitleBarPreviewAvatarBinding
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import arch.cayenne.lib.common.utils.FileUtils
import arch.cayenne.lib.common.utils.ThumbHashUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP

class AvatarFragment : BaseFragment<AvatarViewModel, FragmentAvatarBinding>() {

    override val vbClass: KClass<FragmentAvatarBinding> = FragmentAvatarBinding::class
    override val vmClass: KClass<AvatarViewModel> = AvatarViewModel::class
    private val personalViewModel: PersonalInfoViewModel by sharedViewModel<PersonalInfoViewModel, PersonalInfoFragment>()
    private var currentPhotoPath: String? = null
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // 这里只会返回单个 Uri
    }
    companion object {
        const val CHANGE_FILE_PATH = "CHANGE_FILE_PATH"
    }
    private val camera = 1001 //相机
    private val storage = 1002 //相册
    private var filePath: String? = null
    // 权限数组
    private val PERMISSIONS_REQUEST_CAMERA = arrayOf(
        Manifest.permission.CAMERA,
    )

    private val PERMISSIONS_REQUEST_STORAGE =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

    private val titleBarBinding: TitleBarPreviewAvatarBinding by lazy {
        TitleBarPreviewAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // 权限全部通过
            onPermissionsGranted(
                if (permissions.keys.containsAll(PERMISSIONS_REQUEST_CAMERA.toList())
                ) camera else storage
            )
        } else {
            Toast.makeText(requireContext(), "权限被拒绝，无法使用相机或相册", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
            findNavController().navigateUp()
        }

        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params
        showPersonalAvatar()
    }

    override fun initListener() {

        mBinding.systemAvatar.clickNoRepeat {
            navigate(AvatarFragmentDirections.actionAvatarFragmentToSystemAvatarFragment())
        }

        titleBarBinding.ivMore.clickNoRepeat {
            showAvatarDialog()
        }
        //相册
        mBinding.photo.clickNoRepeat {
            permissions(PERMISSIONS_REQUEST_STORAGE, storage)
        }

        //相机
        mBinding.camera.clickNoRepeat {
            permissions(PERMISSIONS_REQUEST_CAMERA, camera)
        }
    }
    private fun showAvatarDialog() {
        AvatarDialogFragment().apply {
            setOnItemClickListener(object : AvatarDialogFragment.OnClickListener {
                override fun onClickConfirm() {

                }

                override fun onClickAvtarDelete() {

                }
            })
        }.show(childFragmentManager)
    }
    fun showAvatar() {
        val bitmap = FileUtils.loadLocalBitmap(requireContext())
        mBinding.ivUserAvatar.maxScale = 1f
        mBinding.ivUserAvatar.minScale = 1f
        mBinding.ivUserAvatar.setMinimumScaleType(SCALE_TYPE_CENTER_CROP)
        bitmap?.let { mBinding.ivUserAvatar.setImage(ImageSource.cachedBitmap(it)) }
    }

    override suspend fun createObserver() {
        observeResult<Bundle>(CHANGE_FILE_PATH) {
            val newArgs: AvatarFragmentArgs = AvatarFragmentArgs.fromBundle(it)
            this.filePath = newArgs.filePath
            filePath?.let {
                showAvatar()
            }
        }
        // 初始化拍照结果回调AvatarPreviewFragment
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        goToAvatarFragment(uri.toString())
                    } ?: currentPhotoPath?.let { path ->
                        val uri = Uri.fromFile(File(path))
                        goToAvatarFragment(uri.toString())
                    }
                }
            }

        // 初始化相册选择回调
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    goToAvatarFragment(it.toString())
                }
            }
    }

    // 请求权限
    fun permissions(permissions: Array<String>, requestCode: Int) {
        //
        permissionLauncher.launch(permissions)
    }

    fun goToAvatarFragment(imgUrl: String) {
        navigate(AvatarFragmentDirections.actionAvatarFragmentToAvatarPreViewFragment().apply {
            arguments.putString(
                "uri",
                imgUrl
            )
        })
    }


    // 权限通过后的操作
    private fun onPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            camera -> openCamera()
            storage -> openGallery()
        }
    }

    // 打开相机
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: Exception) {
                null
            }

            photoFile?.also {
                val photoURI: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        it
                    )
                } else {
                    Uri.fromFile(it)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    // 创建临时图片文件
    @Throws(Exception::class)
    private fun createImageFile(): File {
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_AVATAR_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    fun showPersonalAvatar(){

        personalViewModel.onUserInfoListener.value.apply {
            this?.avatar.let { avatar ->
                val placeholderDrawable = try {
                    ThumbHashUtils.getBitmapFromThumbHash(avatar?.thumbhash)?.let { bitmap ->
                        BitmapDrawable(resources, bitmap)
                    }
                } catch (_: Exception) {
                    null
                }

                Glide.with(this@AvatarFragment)
                    .downloadOnly()  // 只下载，不解码成 Bitmap
                    .load(avatar?.url?.trim())
                    .placeholder(placeholderDrawable)
                    .into(object : CustomTarget<File>() {
                        override fun onResourceReady(
                            resource: File,
                            transition: Transition<in File>?
                        ) {
                            mBinding.ivUserAvatar.setImage(ImageSource.uri(Uri.fromFile(resource)))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 清理时调用
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                        }
                    })

            }
        }
    }

    // 打开相册
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
}