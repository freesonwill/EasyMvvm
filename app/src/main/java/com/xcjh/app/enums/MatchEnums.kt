package com.xcjh.app.enums


/**
 *    2篮球比赛状态
 *   1 未开赛;2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定;
 */
enum class BasketballStateEnum(var state : Int) {
    /**
     * 1未开赛
     */
    NotStart(1),
    /**
     * 2第一节
     */
    SectionOneSection(2),
    /**
     * 3第一节完
     */
    SectionOneSectionEnd(3),
    /**
     * 4第二节
     */
    SectionTwoSection(4),
    /**
     * 5 第二节完
     */
    SectionTwoSectionEnd(5),
    /**
     * 6第三节
     */
    SectionThreeSection(6),

    /**
     * 7 第三节完
     */
    SectionThreeSectionEnd(7),
    /**
     *8 第四节
     */
    SectionFourSection(8),
    /**
     *9 加时
     */
    Overtime(9),
    /**
     *10 完场
     */
    Completion(10),
    /**
     *11 中断
     */
    Interrupt(11),

    /**
     *12 取消
     */
    Cancel(12),
    /**
     *13 延期
     */
    Delay(13),
    /**
     *14 腰斩
     */
    WaistChopping(14),
    /**
     *15 待定
     */
    Undetermined(15),
}


/**
 * 1足球状态
 * 0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
 * 1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
 */
enum class SoccerStateEnum(var state : Int){
    /**
     * 0 比赛异常，
     */
    MatchAbnormal(0),
    /**
     * 1未开赛
     */
    NotStart(1),

    /**
     * 2 上半场
     */
    FirstHalf(2),
    /**
     * 3 中场
     */
    Midfield(3),
    /**
     * 4 下半场;
     */
    SecondHalf(4),
    /**
     *5 加时赛
     */
    Overtime(5),
    /**
     *7 点球决战
     */
    PenaltyKick(7),
    /**
     *8 完场;
     */
    Completion(8),
    /**
     *9 推迟
     */
    PutOff(9),
    /**
     *10 中断
     */
    Interrupt(10),
    /**
     *11 腰斩
     */
    WaistChopping(11),
    /**
     *12 取消
     */
    Cancel(12),
    /**
     *13 待定
     */
    Undetermined(13),
}

