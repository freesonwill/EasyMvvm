package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.databinding.FragmentEmptyBinding
import kotlin.reflect.KClass

/**
 * @date: 2025/10/4 14:47
 * @description:
 */
class EmptyFragment : BaseFragment<EmptyViewModel, FragmentEmptyBinding>() {
    override val vbClass: KClass<FragmentEmptyBinding>
        get() = FragmentEmptyBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private var title: String? = null
    fun setTitle(title: String) {
        this.title = title
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.text = title
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}