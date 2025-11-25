package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.order.data.model.OrderAllBean

class GameAllViewModel : BaseViewModel() {

    private val _recordData = MutableLiveData<List<OrderAllBean>>()
    val recordData: LiveData<List<OrderAllBean>> = _recordData

    fun getAllGameList() {
        val tmp0 = OrderAllBean(
            true,
            "04-13",
            "",
            "¥2500.00",
            "¥5840.00",
            "",
            ""
        )
        val tmp1 = OrderAllBean(
            false,
            "抢庄牛牛",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/1.avif",
            "¥2500.00",
            "¥5840.00",
            "WL",
            "2025/04/17 15:52:00"
        )
        val tmp2 = OrderAllBean(
            false,
            "麻将胡了",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/2.avif",
            "¥2500.00",
            "¥5040.00",
            "PG",
            "2025/04/17 15:52:00"
        )
        val tmp4 = OrderAllBean(
            true,
            "03-14",
            "",
            "¥2500.00",
            "¥5840.00",
            "",
            ""
        )
        val tmp5 = OrderAllBean(
            false,
            "抢庄牛牛",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/1.avif",
            "¥2500.00",
            "¥5840.00",
            "WL",
            "2025/04/17 15:52:00"
        )
        val tmp6 = OrderAllBean(
            false,
            "麻将胡了",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/2.avif",
            "¥2500.00",
            "¥5040.00",
            "WL",
            "2025/04/17 15:52:00"
        )
        _recordData.value = listOf(tmp0, tmp1, tmp2, tmp4, tmp5, tmp6)
    }
}