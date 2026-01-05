package arch.cayenne.module.account.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BASE_URL
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentPersonalInfoBinding
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.helper.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午4:19
 * @description:
 */
class  PersonalInfoFragment : BaseFragment<PersonalInfoViewModel, FragmentPersonalInfoBinding>() {
    override val vbClass: KClass<FragmentPersonalInfoBinding> = FragmentPersonalInfoBinding::class
    override val vmClass: KClass<PersonalInfoViewModel> = PersonalInfoViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            mBinding.titleBar.loadGeneralTitleBar(R.string.personal_info_title, {
                findNavController().navigateUp()
            })
            mBinding.root.touchBackPressed()
        }
    }

    override fun initListener() {
        mBinding.tvName.clickNoRepeat{
            navigate(PersonalInfoFragmentDirections.actionPersonalInfoFragmentToAccountEditNameFragment())
        }

        mBinding.ivAvatar.clickNoRepeat{
            navigate(PersonalInfoFragmentDirections.actionPersonalInfoFragmentToAvatarFragment(""))
        }

        mBinding.imgCopyUserId.clickNoRepeat{
            copyToClipboard(mBinding.tvUserId.text as String?) {
                showToast(R.string.tip_copy_id.getString())
            }
        }

    }

    override suspend fun createObserver() {
        mViewModel.onUserInfoListener.observe(viewLifecycleOwner) {
            if (it != null) {
                mBinding.tvName.text = it.nickname
                mBinding.tvUserId.text = it.Uid.toString()
                if (it.avatar==null||it.avatar.url.isEmpty()) {
                    mBinding.HalfCircle.visibility = android.view.View.GONE
                }else{
                    mBinding.HalfCircle.visibility = android.view.View.VISIBLE
                    val placeholderDrawable = try {
                        ThumbHashUtils.getBitmapFromThumbHash(it.avatar.thumbhash)?.let { bitmap ->
                            BitmapDrawable(resources, bitmap)
                        }
                    } catch (_: Exception) {
                        null
                    }

                    Glide.with(this@PersonalInfoFragment)
                        .load(it.avatar.url.trim())
                        .placeholder(placeholderDrawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(mBinding.ivAvatar)

                }

            }
        }


        mViewModel.showAvatar.observe(viewLifecycleOwner) { filePath ->
            Glide.with(this@PersonalInfoFragment)
                .load(BASE_URL+filePath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mBinding.ivAvatar)
        }
    }
}