package arch.cayenne.module.betslip.data.constants

enum class BetSlipEnum(val value: Int) {
    UnSettled(value = 3),
    Confirming(value = 1),
    Settled(value = 4),
    Reserve(value = 5),
    Invalid(value = 2);
}