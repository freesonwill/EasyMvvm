package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.GameSupplierDao
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameSupplierListRepository(
    override val scope: CoroutineScope ,
    private val database: GameDatabase ,
    private val httpClient: HttpClient ,
    private val mockHttpClient: HttpClient ,
    private val socketManager: WebSocketManager ,
    private val preloadResultChange: MutableStateFlow<PreloadEnum> ,
    private val manager: UserDataManager ,
) : BaseRepository() {

    //获取供应商数据
    suspend fun queryGameSuppliers(gameType: Int) = database.supplierDao().querySupplier(gameType)

    //监听供应商数据变化
    suspend fun observeSupplierByGameTypeId(gameType: Int) = database.supplierDao().observeSupplierGameTypeId(gameType)

    fun setSupplierSelectIds(type: Int,ids: List<Int>){
        scope.launch(Dispatchers.IO) {
            val selectedIdSet = ids.toSet()
            // 2. 只查询并更新选中的记录
            var selectedSuppliers = queryGameSuppliers(type)
            selectedSuppliers.forEach{
                if (it.id in selectedIdSet){
                    it.isSelected = 1
                }else{
                    it.isSelected = 0
                }
            }
            database.supplierDao().insert(selectedSuppliers)
        }
    }


}