package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
import arch.cayenne.module.home.enums.LeagueType
import kotlin.reflect.KClass

class EarlyGameListFragment : BaseFragment<EmptyViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    // TODO viewmodel待實作, 串接資料後再依據mvvm架構重構
    private var leagueId: Int = -1
    private var selectedDate: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leagueId = it.getInt(ARG_LEAGUE_ID)
            selectedDate = it.getString(ARG_DATE).orEmpty()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {

            tvHomeGameTitle.text = getString(leagueId?.let { LeagueType.fromId(it)?.titleRes }
                ?: R.string.league_all)

            // 初始化 RecyclerView
//            adapter = GameListAdapter()
            rvHomeGameList.layoutManager = LinearLayoutManager(context)
//            rvHomeGameList.adapter = adapter
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    fun onDateChanged(newDate: String) {
        selectedDate = newDate
        refreshData()
    }

    private fun refreshData() {
        // 根據 leagueId 與 date 更新列表
    }

    companion object {
        private const val ARG_DATE = "arg_date"
        private const val ARG_LEAGUE_ID = "league_id"
        fun newInstance(leagueId: Int, date: String): EarlyGameListFragment {
            return EarlyGameListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LEAGUE_ID, leagueId)
                    putString(ARG_DATE, date)
                }
            }
        }
    }
}
