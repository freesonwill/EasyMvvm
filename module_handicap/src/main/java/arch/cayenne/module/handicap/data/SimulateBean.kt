package arch.cayenne.module.handicap.data

data class SimulateBean(
    val id: Long,
    val type: String,     //类型
    val question: String, //问题
    val score: String,    //比分
    val homeName: String, //主队名称
    val awayName: String, //客队名称
    val left: String,     //左边答案
    val right: String,    //右边答案
    val isRight: Boolean, //true左边正确，false右边正确
    val leftMsg: String,  //点击左边答案的提示
    val rightMsg: String  //点击右边答案的提示
)
