package com.walisport.module.topup.data.entity

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.ui.fragment.TopUpCryptoFragment
import com.walisport.module.topup.ui.fragment.TopUpFiatFragment
import com.walisport.module.topup.R

enum class TopUpTabType(val page: PagerBean) {
    CRYPTO(PagerBean(R.string.crypto_coin.getString()) { TopUpCryptoFragment() }),
    FIAT(PagerBean(R.string.fiat_coin.getString()) { TopUpFiatFragment() })
}