package arch.cayenne.lib.common.data.constants

enum class QuickAmountEnum(val value: Int) {
    ONE_HUNDRED(100),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000),
    EIGHT_THOUSAND(8000),
    TEN_THOUSAND(10000);

    fun getAmount(): Long {
        return (value * 100).toLong()
    }
}

enum class QuickAmountKeyboardEnum {
    SINGLE,
    COMBO,
    EARLY_SETTLE
}