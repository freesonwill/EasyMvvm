package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString

enum class ShareLinkEnum : ShareBean {
    COPY_ORDER {
        override val title: String
            get() = R.string.title_share_copy_order.getString()
        override val icon: Int
            get() = R.drawable.icon_copy
    },
    COPY_LINK {
        override val title: String
            get() = R.string.title_share_copy_link.getString()
        override val icon: Int
            get() = R.drawable.icon_link
    },
    QUESTION {
        override val title: String
            get() = R.string.title_share_questions.getString()
        override val icon: Int
            get() = R.drawable.icon_question
    }
}