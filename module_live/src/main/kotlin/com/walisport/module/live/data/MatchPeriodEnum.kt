package com.walisport.module.live.data

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R

enum class MatchPeriodEnum(val code: String, val description: String) {
   PERIOD_1("1001"	, R.string.not_started.getString()),
   PERIOD_2( "1002"	, R.string.first_half.getString()),
   PERIOD_3(" 1003"	, R.string.half_time.getString()),
   PERIOD_4( "1004"	, R.string.second_half.getString()),
   PERIOD_5(" 1005"	, R.string.ft_finish.getString()),
   PERIOD_6( "1006"	, R.string.et_first_half.getString()),
   PERIOD_7(" 1007"	, R.string.et_half_time.getString()),
   PERIOD_8( "1008"	, R.string.et_second_half.getString()),
   PERIOD_9( "1009"	, R.string.et_finish.getString()),
   PERIOD_10( "1010"	, R.string.penalty.getString()),
   PERIOD_11( "1011"	, R.string.finish.getString()),
   PERIOD_12( "1012"	, R.string.awaiting_et.getString()),
   PERIOD_13(  "1013"	, R.string.awaiting_penalty.getString()),
   PERIOD_14(  "1014", R.string.penalty_finish.getString()),
   PERIOD_15(  "1015", R.string.interrupted.getString()),
   PERIOD_16(  "1016"	, R.string.abandoned.getString());
    companion object {
        fun fromCode(code: String): MatchPeriodEnum? {
            return values().find { it.code == code }
        }
    }
}