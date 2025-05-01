package arch.cayenne.module.home.test.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

/**
 * @author: zhangsan
 * @date: 2025/4/29 15:06
 * @description:
 */
@KoinViewModel
class Test1ViewModel : BaseViewModel() {

}

@KoinViewModel(binds = [BaseViewModel::class])
class Test2ViewModel(val name:String) : BaseViewModel() {

}

@KoinViewModel(isGet = [false,true])
class Test3ViewModel(val name:String,model: Test1ViewModel) : BaseViewModel() {
}

@KoinViewModel(binds = [BaseViewModel::class],isGet = [true,false])
class Test4ViewModel(val name:String,model: Test1ViewModel) : BaseViewModel() {

}

