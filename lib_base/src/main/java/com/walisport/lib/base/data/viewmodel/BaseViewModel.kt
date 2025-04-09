package com.walisport.lib.base.data.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

/**
 * @author: zhangsan
 * @date: 2025/3/14 09:51
 * @description:
 */
abstract class BaseViewModel : ViewModel(), KoinComponent {
    //如果有需要的話，把一些相關的元件設定回初始狀態
    open fun reset() {}
}