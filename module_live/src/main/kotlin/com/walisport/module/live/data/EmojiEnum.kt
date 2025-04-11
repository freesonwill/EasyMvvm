package com.walisport.module.live.data

import com.walisport.module.live.R


enum class EmojiEnum(val key: String, val resId: Int) {
    Gin("/id=20/", R.drawable.emoji20),
    Smile("/id=47/", R.drawable.emoji47),
    Boring("/id=6/", R.drawable.emoji6),
    Scrowl("/id=41/", R.drawable.emoji41),
    Dizzy("/id=11/", R.drawable.emoji11),
    Duh("/id=14/", R.drawable.emoji14),
    WrySmile("/id=17/", R.drawable.emoji17),
    Laugh("/id=74/", R.drawable.emoji74),
    HandHeart("/id=21/", R.drawable.emoji21),
    Kiss("/id=23/", R.drawable.emoji23),

    //    Squint("emoji49", R.drawable.emoji49),
    Salute("/id=38/", R.drawable.emoji38),
    Respect("/id=35/", R.drawable.emoji35),
    Tricky("/id=60/", R.drawable.emoji60),
    Surprise("/id=53/", R.drawable.emoji53),
    Frown("/id=19/", R.drawable.emoji19),
    Angry("/id=1/", R.drawable.emoji1),
    Tongue("/id=58/", R.drawable.emoji58),
    Drinking("/id=12/", R.drawable.emoji12),
    Omg("/id=31/", R.drawable.emoji31),

    Tired("/id=56/", R.drawable.emoji56),
    Shrunken("/id=43/", R.drawable.emoji43),
    Broken("/id=7/", R.drawable.emoji7),
    Mask ("/id=64/", R.drawable.emoji64),
    Rolling("/id=16/", R.drawable.emoji16),
    Sweats("/id=54/", R.drawable.emoji54),
    Drool("/id=13/", R.drawable.emoji13),

    Smirk("/id=44/", R.drawable.emoji44),
    Sigh("/id=40/", R.drawable.emoji40),
    Facepalm("/id=46/", R.drawable.emoji46),
    Emm("/id=15/", R.drawable.emoji15),
    Intcrom("/id=25/", R.drawable.emoji25),
    Whimper("/id=62/", R.drawable.emoji62),
    Speechless("/id=51/", R.drawable.emoji51),
    Vomit("/id=26/", R.drawable.emoji26),
    Onlooker("/id=32/", R.drawable.emoji32),
    BahR("/id=5/", R.drawable.emoji5),

    BahL("/id=4/", R.drawable.emoji4),
    Slient("/id=45/", R.drawable.emoji45),
    Scold("/id=39/", R.drawable.emoji39),
    Yawn("/id=65/", R.drawable.emoji65),
    Drowsy("/id=69/", R.drawable.emoji69),
    Clap("/id=10/", R.drawable.emoji10),
    Pooh("/id=34/", R.drawable.emoji34),
    Sun("/id=50/", R.drawable.emoji50),

    Terror("/id=55/", R.drawable.emoji55),
    Crazy("/id=8/", R.drawable.emoji8),
    MyBad("/id=29/", R.drawable.emoji29),
    Concern("/id=68/", R.drawable.emoji68),
    ShutUp("/id=22/", R.drawable.emoji22),
    Awakward("/id=3/", R.drawable.emoji3),
    Coffee("/id=67/", R.drawable.emoji67),
    Skeleton("/id=81/", R.drawable.emoji81),
    Pig("/id=75/", R.drawable.emoji75),
    WaterMelon("/id=52/", R.drawable.emoji52),


    Huge("/id=24/", R.drawable.emoji24),
    ThumbsUp("/id=80/", R.drawable.emoji80),
    Fist("/id=70/", R.drawable.emoji70),
    WavingHand("/id=79/", R.drawable.emoji79),
    Salute1("/id=37/", R.drawable.emoji37),
    Shake("/id=30/",R.drawable.emoji30 ),
    Ok("/id=42/", R.drawable.emoji42),
    Rose("/id=36/", R.drawable.emoji36),
    Heart("/id=28/", R.drawable.emoji28),
    Fire("/id=18/", R.drawable.emoji18),

    Awesome("/id=2/", R.drawable.emoji2),
    Packet("/id=33/", R.drawable.emoji33),
    FireWork("/id=71/", R.drawable.emoji71),
    Toasted("/id=57/", R.drawable.emoji57),
    Beer("/id=72/", R.drawable.emoji72),
    Knife("/id=73/", R.drawable.emoji73),
    Poop("/id=76/", R.drawable.emoji76),
    Ballon("/id=66/", R.drawable.emoji66),
    Cache("/id=9/", R.drawable.emoji9),
    Bye("/id=78/", R.drawable.emoji78),

    Goal("/id=101/",R.drawable.emoji101),
    ScordTwice("/id=82/",R.drawable.emoji82),
    HatTrick("/id=83/",R.drawable.emoji83),
    BigFour("/id=84/",R.drawable.emoji84),
    FivePassed("/id=85/",R.drawable.emoji85),
    GoalCelebration("/id=86/",R.drawable.emoji86),
    ChampionDream("/id=87/",R.drawable.emoji87),
    TargeLock("/id=88/",R.drawable.emoji88),
    QuickFighting("/id=89/",R.drawable.emoji89),
    FinalBattle("/id=90/",R.drawable.emoji90),
    GoalCombo("/id=91/",R.drawable.emoji91),
    OnThePitch("/id=92/",R.drawable.emoji92),
    ShotNet("/id=93/",R.drawable.emoji93),
    Courageous("/id=94/",R.drawable.emoji94,),
    Passionate("/id=95/",R.drawable.emoji95,),
    Shocking("/id=96/",R.drawable.emoji96),
    BloodBoiling("/id=97/",R.drawable.emoji97),
    FinalMoment("/id=98/",R.drawable.emoji98),
    SuperPlayer("/id=99/",R.drawable.emoji99),
    Siuuu("/id=100/",R.drawable.emoji100);



    companion object {
        private val _map: MutableMap<String, Int> = mutableMapOf()
        fun getEmojiMap(): Map<String, Int> {
            entries.forEach {
                _map[it.key] = it.resId
            }
            return _map
        }
    }

}