package com.walisport.module.topup.data.entity

data class CryptoConfigBean(
    val id: Int,
    val name: String,
    val addressList: List<CryptoAddressBean>
)

data class CryptoAddressBean(
    val chain: String,
    val address: String
)