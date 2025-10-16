package com.walisport.module.topup.data.entity

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.ui.fragment.WithdrawCryptoFragment
import com.walisport.module.topup.ui.fragment.TopUpFiatFragment

enum class WithdrawTabType(val page: PagerBean) {
    CRYPTO(PagerBean(R.string.crypto_coin.getString()) { WithdrawCryptoFragment() }),
    FIAT(PagerBean(R.string.fiat_coin.getString()) { TopUpFiatFragment() })
}
