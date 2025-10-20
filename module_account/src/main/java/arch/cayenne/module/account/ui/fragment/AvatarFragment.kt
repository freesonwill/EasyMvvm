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
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvatarFragment : BaseFragment<AvatarViewModel, FragmentAvatarBinding>() {

    override val vbClass: KClass<FragmentAvatarBinding> = FragmentAvatarBinding::class
    override val vmClass: KClass<AvatarViewModel> = AvatarViewModel::class
    private var currentPhotoPath: String? = null
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val camera = 1001 //相机
    private val storage = 1002 //相册

    // 权限数组
    private val PERMISSIONS_REQUEST_CAMERA = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val PERMISSIONS_REQUEST_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val titleBarBinding: TitleBarPreviewAvatarBinding by lazy {
        TitleBarPreviewAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
            findNavController().navigateUp()
        }

        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params
    }

    override fun initListener() {

        mBinding.systemAvatar.clickNoRepeat {
            navigate(AvatarFragmentDirections.actionAvatarFragmentToSystemAvatarFragment())
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

    override suspend fun createObserver() {
        // 初始化拍照结果回调
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
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest, requestCode)
        } else {
            onPermissionsGranted(requestCode)
        }
    }

    fun goToAvatarFragment(imgUrl: String) {
        navigate(AvatarFragmentDirections.actionAvatarFragmentToAvatarPreViewFragment().apply {
            arguments.putString(
                "uri",
                imgUrl
            )
        })
    }

    // 权限请求结果处理
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionsGranted(requestCode)
        } else {
            Toast.makeText(requireContext(), "权限被拒绝，无法使用相机或相册", Toast.LENGTH_SHORT)
                .show()
        }
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
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // 打开相册
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
}