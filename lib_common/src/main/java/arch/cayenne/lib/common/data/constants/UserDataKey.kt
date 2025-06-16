package arch.cayenne.lib.common.data.constants

enum class UserDataKey(val key: String) {

    KEY_SKIN("Skin"),
    KEY_ODDS("Odds"),
    KEY_LANGUAGE("Language"),
    KEY_BETTING("Betting"),
    KEY_FAVORITE("Favorite"),
    KEY_UID("UID"),
    KEY_TOKEN("Token"),
    KEY_BETSLIP_DETAIL("BetSlipDetail"),
    KEY_RECORD("Record"), //搜索历史

    KEY_SYSTEM_BET("System_Goal_Bet"),
    KEY_SYSTEM_FAV("System_Goal_Fav"),
    KEY_SYSTEM_ALL("System_Goal_All"),
    KEY_KICK_BET("System_Kick_Bet"),
    KEY_KICK_FAV("System_Kick_Fav"),
    KEY_KICK_ALL("System_Kick_All"),
    KEY_APP_BET("App_Goal_Bet"),
    KEY_APP_FAV("App_Goal_Fav"),
    KEY_APP_ALL("App_Goal_All"),
    KEY_PERSONAL_INFO_NICKNAME("PersonalInfo_NickName"),
    KEY_PERSONAL_INFO_RES_ID("PersonalInfo_ResId"),
    KEY_PERSONAL_INFO_POSITION("PersonalInfo_Position"),
}