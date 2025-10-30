package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.GameListBean
import plugin.koin.KoinViewModel

@KoinViewModel
class ChatViewPagerViewModel : BaseViewModel() {

    private val _gameList = MutableLiveData<List<GameListBean>>()
    val gameList: LiveData<List<GameListBean>> = _gameList
    fun getGameList(){
      var list:  MutableList<GameListBean> = mutableListOf(
          GameListBean(name = "cessfsdf", money = "100000.00"),
          GameListBean(name = "3ff2f", money = "1042000.00"),
          GameListBean(name = "3323", money = "23245.00"),
          GameListBean(name = "4444", money = "234.00"),
          GameListBean(name = "1123", money = "33.00"),
          GameListBean(name = "1234", money = "1023000.00"),
      )
        _gameList.value = list
    }
}