package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.data.repo.DrawerContentRepository
import com.walisport.module.business.common.repo.BalanceRepository
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.data.NotificationBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class DrawerContentViewModel: BaseViewModel() {
    private val repository: DrawerContentRepository by inject()
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val messageMainRepository: MessageMainRepository by inject { parametersOf(viewModelScope) }
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType
    private var cursorId: Long = 0L
    private val cursorType: Int = 0
    private val _notificationBean = MutableLiveData<Event<List<NotificationBean>>>()
    val notificationBean: LiveData<Event<List<NotificationBean>>> = _notificationBean
    private val balanceRepository: BalanceRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<InfoBean?>() }

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            skinManager.skinFlow.collect {
                _selectedSkinType.value = Event(it)
            }
        }
        //監聽系统消息列表
        viewModelScope.launch {
            messageMainRepository.observeLatestMessage().collect {
                val temp = it.map { item ->
                    NotificationBean(
                        id = item.id,
                        type = item.type,
                        state = item.status,
                        title = item.title,
                        content = item.content,
                        createTime = item.time
                    )
                }
                _notificationBean.value = Event(temp)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeInfo().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
    }
    fun getDefaultResId(): Int {
        return repository.getDefaultResId()
    }
    fun getDefaultNickName(): String {
        return repository.getDefaultNickName()
    }
    //获取系统消息列表
    fun getMessageList() {
        cursorId = 0L
        val result = messageMainRepository.getMessageList(cursorId, cursorType)
        result.let {
            if (result.isNotEmpty()) {
                cursorId = result.last().id
            }
        }
    }
}