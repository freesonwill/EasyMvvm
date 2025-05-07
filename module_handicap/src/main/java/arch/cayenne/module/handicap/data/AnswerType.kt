package arch.cayenne.module.handicap.data

enum class AnswerType(val type: Int) {
    WIN_ALL(1),
    LOSE_ALL(2),
    WIN_HALF(3),
    LOSE_HALF(4),
    SEED_MONEY(5)
}