package com.walisport.module.live.data

import com.walisport.module.live.R


enum class EmojiEnum(val key: String, val resId: Int) {
    Gin("emoji20", R.drawable.emoji20),
    Smile("emoji47", R.drawable.emoji47),
    Boring("emoji6", R.drawable.emoji6),
    Scrowl("emoji41", R.drawable.emoji41),
    Dizzy("emoji11", R.drawable.emoji14),
    Duh("emoji14", R.drawable.emoji14),
    Facepalm("emoji17", R.drawable.emoji17),
    Laugh("emoji74", R.drawable.emoji74),
    HandHeart("emoji21", R.drawable.emoji21),
    Heart("empoji23", R.drawable.emoji23),

    //    Smirk("emoji49", R.drawable.emoji49),
    Salute("emoji38", R.drawable.emoji38),
    Respect("emoji35", R.drawable.emoji35),
    Tricky("emoji60", R.drawable.emoji60),
    Surprise("emoji53", R.drawable.emoji53),
    Frown("emoji19", R.drawable.emoji19),
    Angry("emoji1", R.drawable.emoji1),
    Tongue("emoji58", R.drawable.emoji58),
    Drinking("emoji12", R.drawable.emoji12),
    Omg("emoji31", R.drawable.emoji31),

    Tired("emoji56", R.drawable.emoji56),
    Shrunken("emoji43", R.drawable.emoji43),
    Broken("emoji7", R.drawable.emoji7),
    WrySmile("emoji64", R.drawable.emoji64),
    Rolling("emoji16", R.drawable.emoji16),
    Sweats("emoji54", R.drawable.emoji54),
    Drool("emoji13", R.drawable.emoji13),

    Shy("emoji44", R.drawable.emoji44),
    Wow("emoji40", R.drawable.emoji40),
    Smart("emoji46", R.drawable.emoji46),
    Emm("emoji15", R.drawable.emoji15),
    Intcrom("emoji25", R.drawable.emoji25),
    Whimper("emoji62", R.drawable.emoji62),
    Speechless("emoji51", R.drawable.emoji51),
    Kiss("emoji26", R.drawable.emoji26),
    Onlooker("emoji32", R.drawable.emoji32),
    BahR("emoji5", R.drawable.emoji5),

    BahL("emoji4", R.drawable.emoji4),
    Slient("emoji45", R.drawable.emoji45),
    Scold("emoji39", R.drawable.emoji39),
    Yawn("emoji65", R.drawable.emoji65),
    Drowsy("emoji69", R.drawable.emoji69),
    Clap("emoji10", R.drawable.emoji10),
    Pooh("emoji34", R.drawable.emoji34),
    Smoke("emoji50", R.drawable.emoji50),

    Terror("emoji55", R.drawable.emoji55),
    Bue("emoji8", R.drawable.emoji8),
    MyBad("emoji29", R.drawable.emoji29),
    concern("emoji68", R.drawable.emoji68),
    happy("emoji22", R.drawable.emoji22),
    Awakward("emoji3", R.drawable.emoji3),
    coffe("emoji67", R.drawable.emoji67),
    skeleton("emoji101", R.drawable.emoji101),
    pig("emoji75", R.drawable.emoji75),
    sun("emoji52", R.drawable.emoji52),


    Huge("emoji24", R.drawable.emoji24),
    ThumbsUp("emoji80", R.drawable.emoji80),
    FingerHeat("emoji70", R.drawable.emoji70),
    WavingHand("emoji79", R.drawable.emoji79),
    Salute1("emoji37", R.drawable.emoji37),
    Shake("emoji42", R.drawable.emoji42),
    Ok("emoji30", R.drawable.emoji30),
    Rose("emoji36", R.drawable.emoji36),
    Love("emoji28", R.drawable.emoji28),
    Fire("emoji18", R.drawable.emoji18),

    Awesome("emoji2", R.drawable.emoji2),
    Packet("emoji33", R.drawable.emoji33),
    FireWork("emoji71", R.drawable.emoji71),
    Toasted("emoji57", R.drawable.emoji57),
    inGot("emoji72", R.drawable.emoji72),
    Knife("emoji73", R.drawable.emoji73),
    Poop("emoji76", R.drawable.emoji76),
    Ballon("emoji66", R.drawable.emoji66),
    Cache("emoji9", R.drawable.emoji9),
    Wave("emoji78", R.drawable.emoji78);


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