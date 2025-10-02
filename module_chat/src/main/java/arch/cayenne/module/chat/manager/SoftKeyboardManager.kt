package arch.cayenne.module.chat.manager

import android.animation.ObjectAnimator
import android.view.View
import android.widget.EditText
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.lib.database.entity.ChatConfigBean
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
import arch.cayenne.module.chat.utils.EditTextUtils
import arch.cayenne.module.chat.utils.softkeyboard.NavigationBarHelper
import arch.cayenne.module.chat.utils.softkeyboard.NavigationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    var emojiKeyBoardHeight: Int = 0
    var mainAnim: ObjectAnimator? = null
    var isSoftKeyboardShow = false
    var navigationBarHelper: NavigationBarHelper? = null
    val toastLiveData: MutableLiveData<String> = MutableLiveData()
    private lateinit var rootView: View
    private lateinit var mainView: View
    private lateinit var etInput: EditText

    init {
        lifecycle.addObserver(this)
        getSoftKeyBoardHeight()
        checkFirstOpen()
    }

    fun initView(rootView:View,mainView:View,etInput:EditText){
        this.rootView = rootView
        this.mainView = mainView
        this.etInput = etInput
        initKeyboardListener()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        hideSoftKeyBoard(4)
        navigationBarHelper = null
    }

    fun initKeyboardListener() {
        navigationBarHelper = NavigationBarHelper(
            rootView,
            lifecycle,
            object : NavigationListener {
                override fun setNavigationStatus(hasNavigation: Boolean, navigationHeight: Int) {
                }

                override fun onSoftKeyBoardHide() {
                    isSoftKeyboardShow = false
                    if (clickKeyBoardType == KeyBoardType.SOFT_KEYBOARD) {
                        keyBoardListener.keyboardChangeClick(KeyBoardType.CHAT, 11)
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


    private fun whenSoftKeyBoardOpen(keyboardHeight: Int) {
        isSoftKeyboardShow = true
        if (softKeyBoardHeight == keyboardHeight) {
            return
        }
        mainAnim?.cancel()
        val animationType = getKeyBoardActionType(
            clickKeyBoardType,
            currentKeyBoardType
        )

        when (animationType) {
            KeyboardActionType.CHAT_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                panelAnimateTo(-softKeyBoardHeight, onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }

            KeyboardActionType.EMOJI_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                panelAnimateTo(-softKeyBoardHeight, onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }

            KeyboardActionType.SOFT_TO_SOFT -> {
                softKeyBoardHeight = keyboardHeight
                panelAnimateTo(-softKeyBoardHeight, onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
                    saveUpdateSoftKeyBoardHeight()
                })
            }

            else -> {}
        }
    }

    private fun panelAnimateTo(offset: Int, onStart: () -> Unit = {}, onEnd: () -> Unit = {}) {
        mainAnim = ObjectAnimator.ofFloat(mainView, "translationY", offset.toFloat())
        mainAnim?.interpolator = FastOutSlowInInterpolator()
        mainAnim?.duration = 170L
        mainAnim?.addListener(onStart = { onStart.invoke() }, onEnd = {
            onEnd.invoke()
        })
        if (isFirstOpen) {
            mainAnim?.startDelay = 200L
        }
        mainAnim?.start()

    }



    fun showKeyboardAnimation() {
        val animationType = getKeyBoardActionType(
            clickKeyBoardType,
            currentKeyBoardType
        )
//        "showKeyboardAnimation $animationType $softKeyBoardHeight}".logd("aaa")
        when (animationType) {
            KeyboardActionType.CHAT_TO_CHAT -> keyBoardListener.changeKeyboardUi(
                KeyBoardType.CHAT
            )
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> {
                if (softKeyBoardHeight == 0) {//如果没有记录软件盘高度或者app打开后第一次弹出软件盘不急着开启动画先弹出软件盘
                    softKeyboardChange(true, 1)
                    return
                }
                softKeyboardChange(true, 1)
                panelAnimateTo(-softKeyBoardHeight, onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                })
            }
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> {
                panelAnimateTo(0, onStart = {
                    softKeyboardChange(false, 2)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.CHAT)
                })
            }
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> {
//                isSoftKeyBoardBack = true
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    softKeyboardChange(false, 3)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //展示表情键盘
            KeyboardActionType.CHAT_TO_EMOJI -> {
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //表情键盘切换到软件盘
            KeyboardActionType.EMOJI_TO_SOFT -> {
                if (softKeyBoardHeight == 0) {
                    softKeyboardChange(true, 4)
                    return
                }
                panelAnimateTo(-softKeyBoardHeight, onStart = {
                    softKeyboardChange(true, 4)
                    keyBoardListener.changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
//                    isSoftKeyBoardBack = true
                })
            }
            //表情键盘切换到聊天
            KeyboardActionType.EMOJI_TO_CHAT -> {
                panelAnimateTo(0, onEnd = {
                    keyBoardListener.changeKeyboardUi(KeyBoardType.CHAT)
                })
            }

            else -> {}
        }
    }


    /**
     * 判断动画类型
     * */
    fun getKeyBoardActionType(
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
        }
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
        } else if (!value) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
            hideSoftKeyBoard(2)
        }
    }

    /**
     *打开软件盘
     * */
    fun openSoftKeyBoard() {
        etRequestFocus()
        EditTextUtils.showKeyboard(etInput.context, etInput)
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
        EditTextUtils.hideKeyboard(etInput.context, etInput)
    }

    fun etRequestFocus() {
        etInput.requestFocus()
        etInput.setSelection(etInput.length())
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