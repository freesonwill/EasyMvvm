package com.walisport.module.live.data.model

enum class LiveBetSlipEnum(val value: Int) {
    UnSettled(value = 0),
    Confirming(value = 1),
    Settled(value = 2),
    Reserve(value = 3),
    Invalid(value = 4);
}