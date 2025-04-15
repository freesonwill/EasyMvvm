package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
import arch.cayenne.module.home.enums.LeagueType
import kotlin.reflect.KClass

class TodayGameListFragment : BaseFragment<EmptyViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    // TODO viewmodel待實作, 串接資料後再依據mvvm架構重構
    private var leagueId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        leagueId = arguments?.getInt(ARG_LEAGUE_ID)
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

    companion object {
        private const val ARG_LEAGUE_ID = "league_id"

        fun newInstance(leagueId: Int): TodayGameListFragment {
            val fragment = TodayGameListFragment()
            val args = Bundle()
            args.putInt(ARG_LEAGUE_ID, leagueId)
            fragment.arguments = args
            return fragment
        }
    }
}