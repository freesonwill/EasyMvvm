package com.walisport.module.live.data.constants


// 0-默认为2列 1-一列 2-两列 3-三列 4-波胆
enum class StatesArrange(val code: Int, val value: Int) {
    DEFAULT_ARRANGE(0, 2),
    ONE_ARRANGE(1, 1),
    TOW_ARRANGE(2, 2),
    THREE_ARRANGE(3, 3),
    BO_DIAN(4, 3);

    companion object {
        fun getStates(code: Int): StatesArrange? {
            return StatesArrange.entries.find { it.code == code }
        }
    }
}