package arch.cayenne.module.chat.manager

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.EditText
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.lib.database.entity.ChatConfigBean
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
import arch.cayenne.module.chat.utils.EditTextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


/**
 * @author: wenxi
 * @date: 29/9/25 18:05
 * @description:
 */
class SoftKeyboardManager(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
    private val userDataManager: UserDataManager,
    private val chatConfigDao: ChatConfigDao,
    private val keyBoardListener: SoftKeyBoardMangerListener,
) : DefaultLifecycleObserver {
    var navigationBarHelper: SoftAnimHelper? = null
    val toastLiveData: MutableLiveData<String> = MutableLiveData()

    //软件盘高度
    var softKeyBoardHeight: Int = 0

    //软件盘弹出时间
//    var softKeyBoardDuration: Long = 170L

    //软件盘状态 true 打开 false 关闭
    var softKeyboardStatus: Boolean = false

    //键盘点击的意向
    var clickKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //当前键盘状态
    var currentKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //判断是否开启app后第一次弹出软件盘
    var isFirstOpen: Boolean = true

    //表情键盘高度
    var emojiKeyBoardHeight: Int = 0

    //软件盘是否显示
    var isSoftKeyboardShow = false

    //是否首页键盘显示
    private var isMainSoft = false

    // softFragment decoerview
    private lateinit var rootView: View

    // softFragment 主页
    private lateinit var mainView: View

    // softFragment
    private var etInput: EditText? = null

    //切换动画
    var mainAnim: AnimatorSet? = null

    //main的初始高度
    var originMainHeight: Int = 0


    init {
        lifecycle.addObserver(this)
        getSoftKeyBoardHeight()
        checkFirstOpen()
    }

    fun initView(rootView: View, etInput: EditText, isMain: Boolean) {
        this.rootView = rootView
        this.etInput = etInput
        this.isMainSoft = isMain
        initKeyboardListener()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        hideSoftKeyBoard(4)
        navigationBarHelper = null
    }

    fun initKeyboardListener() {
        navigationBarHelper = SoftAnimHelper(
            rootView,
            lifecycle,
            object : SoftAnimListener {
                override fun setNavigationStatus(hasNavigation: Boolean, navigationHeight: Int) {
                }

                override fun onSoftKeyBoardHide() {
                    isSoftKeyboardShow = false
                    if (clickKeyBoardType == KeyBoardType.SOFT_KEYBOARD) {
                        keyBoardListener.keyboardChangeClick(KeyBoardType.CHAT, 8)
                    }
                }

                override fun onSoftKeyBoardShow(keyboardHeight: Int) {
                    if (isFirstOpen) {
                        setSoftFirstOpen(false)
                    }
                    whenSoftKeyBoardOpen(keyboardHeight)
                }

                override fun secondSoftKeyBoardShow() {

                }
            })
        navigationBarHelper?.setDbKeyBoardHeight(softKeyBoardHeight)
    }


    /**
     *首次检查聊天权限投注额度和余额失败后
     * 每次点击软件盘都查询投注额 根据结果判断是否显示软件盘
     * */
    fun checkSoftKeyBoardBetAmount(
        checkBetAmountValue: CheckBetResultEnum?,
        keyBoardType: KeyBoardType,
        flag: Int = 0
    ) {
        scope.launch {
            when (checkBetAmountValue) {
                CheckBetResultEnum.BET_AMOUNT_INVALID -> {
                    toastLiveData.value = R.string.insufficient_bet_amount.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

                CheckBetResultEnum.BALANCE_INVALID -> {
                    toastLiveData.value = R.string.insufficient_balance.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

                CheckBetResultEnum.SUCCESS -> {
                    addSoftKeyBoardEvent(keyBoardType, flag)
                }

                null -> {
                    toastLiveData.value = R.string.insufficient_bet_amount.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

            }

        }
    }

    private fun softKeyboardChange(value: Boolean, flag: Int) {
        softKeyboardStatus = value
        if (value) {  //显示软件盘状态 it == true  当前软件盘没有收缩状态
            openSoftKeyBoard()
        } else { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
            hideSoftKeyBoard(2)
        }
    }

    /**
     * 判断动画类型
     * */
    private fun getKeyBoardActionType(
        listenerValue: KeyBoardType,
        currentValue: KeyBoardType
    ): KeyboardActionType {

        return when (currentValue) {
            KeyBoardType.CHAT -> {
                return when (listenerValue) {
                    KeyBoardType.EMOJI -> KeyboardActionType.CHAT_TO_EMOJI
                    KeyBoardType.SOFT_KEYBOARD -> KeyboardActionType.CHAT_TO_SOFT
                    KeyBoardType.CHAT -> KeyboardActionType.CHAT_TO_CHAT
                }
            }

            KeyBoardType.SOFT_KEYBOARD -> {
                return when (listenerValue) {
                    KeyBoardType.CHAT -> KeyboardActionType.SOFT_TO_CHAT
                    KeyBoardType.EMOJI -> KeyboardActionType.SOFT_TO_EMOJI
                    KeyBoardType.SOFT_KEYBOARD -> KeyboardActionType.SOFT_TO_SOFT
                }
            }

            KeyBoardType.EMOJI -> {
                return when (listenerValue) {
                    KeyBoardType.CHAT -> KeyboardActionType.EMOJI_TO_CHAT
                    KeyBoardType.SOFT_KEYBOARD -> KeyboardActionType.EMOJI_TO_SOFT
                    KeyBoardType.EMOJI -> KeyboardActionType.NONE
                }
            }

            else -> KeyboardActionType.NONE
        }
    }

    /**
     * app打开后首次弹出软件盘，会有第二次的软件盘高度提醒，如果第二次的高度不对重新调用弹出动画，
     * 并保存第二次的软件盘高度
     * */
    private fun whenSoftKeyBoardOpen(keyboardHeight: Int) {
        isSoftKeyboardShow = true
        if (softKeyBoardHeight == keyboardHeight) {
            return
        }
        mainAnim?.cancel()

        val animationType = getKeyBoardActionType(clickKeyBoardType, currentKeyBoardType)
        when (animationType) {
            KeyboardActionType.CHAT_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }

            KeyboardActionType.EMOJI_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }
            //chatTOsoft
            KeyboardActionType.SOFT_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }
            else -> {}
        }
    }

    var mainDiffer:Int = 0
    fun checkMainHeight():Int {
        val getManHeight = keyBoardListener.getMainHeight()
        mainDiffer = getManHeight - originMainHeight
//        "differ $mainDiffer ${originMainHeight} getManHeight  ${getManHeight}".logd("aaa")
        mainDiffer = if(mainDiffer > 50) mainDiffer else 0
        return mainDiffer
    }

    fun showKeyboardAnimation() {
        val animationType = getKeyBoardActionType(clickKeyBoardType, currentKeyBoardType)
//        "showKeyboardAnimation $animationType $softKeyBoardHeight}".logd("aaa")
        when (animationType) {
            KeyboardActionType.CHAT_TO_CHAT -> keyBoardListener.changeKeyboardUi(KeyBoardType.CHAT)
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> {
                if (softKeyBoardHeight == 0) {//如果没有记录软件盘高度或者app打开后第一次弹出软件盘不急着开启动画先弹出软件盘
                    softKeyboardChange(true, 1)
                    return
                }
                softKeyboardChange(true, 5)
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                })
            }
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> {
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    softKeyboardChange(false, 2)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.CHAT)
                })
            }
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> {
//                isSoftKeyBoardBack = true
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    softKeyboardChange(false, 3)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //展示表情键盘
            KeyboardActionType.CHAT_TO_EMOJI -> {
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //表情键盘切换到软件盘
            KeyboardActionType.EMOJI_TO_SOFT -> {
                if (softKeyBoardHeight == 0) {
                    softKeyboardChange(true, 4)
                    return
                }
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onStart = {
                    softKeyboardChange(true, 6)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
//                    isSoftKeyBoardBack = true
                })
            }
            //表情键盘切换到聊天
            KeyboardActionType.EMOJI_TO_CHAT -> {
                keyBoardListener.startAnim(animationType, getAnimTransY(animationType), onEnd = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.CHAT)
                })
            }

            else -> {}
        }
    }

    private fun getAnimTransY(animationType:KeyboardActionType):Int{
       return when (animationType) {
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> if (isMainSoft) -(softKeyBoardHeight - (62).dp2px) else -(softKeyBoardHeight - checkMainHeight())
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> 0
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> if (isMainSoft) -emojiKeyBoardHeight else -(emojiKeyBoardHeight - mainDiffer)
            //展示表情键盘
            KeyboardActionType.CHAT_TO_EMOJI -> if (isMainSoft) -emojiKeyBoardHeight else -(emojiKeyBoardHeight - checkMainHeight())
            //表情键盘切换到软件盘
            KeyboardActionType.EMOJI_TO_SOFT -> if (isMainSoft) -(softKeyBoardHeight - (62).dp2px) else -(softKeyBoardHeight - mainDiffer)
            //表情键盘切换到聊天
            KeyboardActionType.EMOJI_TO_CHAT -> 0
            else -> 0
        }
    }



    /**
     *打开软件盘
     * */
    fun openSoftKeyBoard() {
        etInput?.let {
            etRequestFocus()
            EditTextUtils.showKeyboard(it.context, it)
        }
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
//        "hideSoftKeyBoard $flag".logd("aaa")
        etInput?.let {
            it.clearFocus()
            EditTextUtils.hideKeyboard(it.context, it)
        }
    }

    fun etRequestFocus() {
        scope.launch {
            etInput?.let {
                it.requestFocus()
//                it.setSelection(it.length())
            }
        }
    }

    /**
     * 获取保存的软件盘高度
     * */
    fun getSoftKeyBoardHeight() {
        softKeyBoardHeight = userDataManager.getValue(UserDataKey.KEY_SOFT_KEYBOARD_HEIGHT, 0)
    }

    /**
     * 检查是否app打开后第一次弹出软件盘
     * */
    private fun checkFirstOpen() {
        scope.launch(Dispatchers.IO) {
            isFirstOpen = (chatConfigDao.getFirst() ?: ChatConfigBean(
                0,
                true
            ).also { chatConfigDao.insertChatConfig(it) }).isFirstOpen
        }
    }

    fun setSoftFirstOpen(value: Boolean) {
        scope.launch(Dispatchers.IO) {
            delay(200) //防止动画还没有延迟，isFistOpen就false
            isFirstOpen = value
            chatConfigDao.updateConfigBean(ChatConfigBean(0, value))
        }
    }

    fun saveUpdateSoftKeyBoardHeight() {
        userDataManager.setKeyValue(UserDataKey.KEY_SOFT_KEYBOARD_HEIGHT, softKeyBoardHeight)
    }

    /**
     * 由于系统特性，当软件盘出现时，再次点击Editext软件盘会消失
     * 消失后会显示
     * */
    fun addSoftKeyBoardEvent(keyBoardType: KeyBoardType, flag: Int = 0) {
        if (keyBoardType == clickKeyBoardType) {
            return
        }
        this.clickKeyBoardType = keyBoardType
    }

    /**
     *更新软件盘显示
     * */
    fun updateKeyBoard() {
        if (currentKeyBoardType == clickKeyBoardType) {
            return
        }
        currentKeyBoardType = clickKeyBoardType
        keyBoardListener.updateChatKeyboardType(currentKeyBoardType)
    }

}