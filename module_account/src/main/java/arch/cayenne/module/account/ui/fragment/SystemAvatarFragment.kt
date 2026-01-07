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
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentSystemAvatarBinding
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import arch.cayenne.module.account.ui.adapter.PersonalInfoAdapter
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

class SystemAvatarFragment : BaseFragment<SystemAvatarViewModel, FragmentSystemAvatarBinding>() {
    override val vbClass: KClass<FragmentSystemAvatarBinding> = FragmentSystemAvatarBinding::class
    override val vmClass: KClass<SystemAvatarViewModel> = SystemAvatarViewModel::class
    private var personalInfoAdapter = PersonalInfoAdapter()
    private val personalViewModel: PersonalInfoViewModel by sharedViewModel<PersonalInfoViewModel, PersonalInfoFragment>()
    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }


    private var avatarCheckable = false
    private var rotationAngle = 90f
    private var imageIndex = -1

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)
        titleBarBinding.ivBack.clickNoRepeat {
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
            val spacingLeft = 10.dp2px
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
                    outRect.right = spacingLeft
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
                imageIndex = data.groupId
                rotationAngle = 0f
                mBinding.ivUserAvatar.setRotationAngle(0f)
                // 加载网络图片并高斯模糊后设置为背景
                Glide.with(this@SystemAvatarFragment)
                    .asBitmap()
                    .load(data.host + data.url.trim())
                    .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            bit: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                        ) {
                            var blurBit = bit
                            // 先放大再高斯模糊，提升磨砂自然度
                            val blurRadius = 25f
                            val blurSize = 1200 // 先放大到更高分辨率
                            val scaledBitmap =
                                Bitmap.createScaledBitmap(blurBit, blurSize, blurSize, true)
                            val rs = android.renderscript.RenderScript.create(requireContext())
                            val input =
                                android.renderscript.Allocation.createFromBitmap(rs, scaledBitmap)
                            val output = android.renderscript.Allocation.createTyped(rs, input.type)
                            val script = android.renderscript.ScriptIntrinsicBlur.create(
                                rs,
                                android.renderscript.Element.U8_4(rs)
                            )
                            script.setRadius(blurRadius)
                            script.setInput(input)
                            script.forEach(output)
                            output.copyTo(scaledBitmap)
                            rs.destroy()
                            // 再缩放回目标尺寸
                            val blurredBitmap = Bitmap.createScaledBitmap(
                                scaledBitmap,
                                blurBit.width,
                                blurBit.height,
                                true
                            )
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
            val index = when (rotationAngle) {
                0f -> 0
                90f -> 1
                180f -> 2
                270f -> 3
                else -> 0
            }
            val systemAvatarBean = mViewModel.systemAvatarList.value?.get(imageIndex * 4 + index)
            if (systemAvatarBean == null) {
                withContext(Dispatchers.Main) {
                    personalInfoAdapter.setIsUpAvatar(true)
                    mBinding.tvSave.isSelected = true
                    mBinding.tvSave.isClickable = true
                    mBinding.tvSave.text = R.string.personal_avatar_save.getString()
                    Toast.makeText(requireContext(), "上传失败", Toast.LENGTH_SHORT).show()
                }
                return@launch
            } else {
                mViewModel.uploadAvatarUrl(systemAvatarBean.url)
            }

        }
    }

    override fun initListener() {
        titleBarBinding.ivRotate.clickNoRepeat {
            if (!avatarCheckable) return@clickNoRepeat
            rotationAngle = (rotationAngle + 90f) % 360f
            mBinding.ivUserAvatar.setRotationAngle((mBinding.ivUserAvatar.getRotationAngle() + 90f) % 360f)
        }
    }

    override suspend fun createObserver() {
        mViewModel.systemAvatarList.observe(viewLifecycleOwner) { list ->
            //对list按groupId排序, 取每个groupId的第一个
            val set = mutableSetOf<Int>()

            val newList = mutableListOf<SystemAvatarBean>()
            list.forEach {
                if (!set.contains(it.groupId)) {
                    newList.add(it)
                    set.add(it.groupId)
                }
            }

            personalInfoAdapter.submitList(newList)
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