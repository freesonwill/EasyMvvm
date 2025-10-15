package com.walisport.module.topup.data.entity

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.ui.fragment.CryptoFragment
import com.walisport.module.topup.ui.fragment.FiatFragment
import com.walisport.module.topup.R

enum class TabType(val page: PagerBean) {
    CRYPTO(PagerBean(R.string.crypto_coin.getString()) { CryptoFragment() }),
    FIAT(PagerBean(R.string.fiat_coin.getString()) { FiatFragment() })
}