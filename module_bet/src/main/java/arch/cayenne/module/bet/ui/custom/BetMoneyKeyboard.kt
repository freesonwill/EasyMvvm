package arch.cayenne.module.bet.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.RecyclerView.Adapter
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.view.NumberKeyboardView.OnCalculatorClickListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getMaxLength
import arch.cayenne.lib.common.utils.ext.setOnClickOrLongPressListener
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.LayoutBetMoneyKeyboardBinding
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import org.koin.java.KoinJavaComponent.getKoin

/**
 * @date: 2025/11/12 15:20
 * @description: 串关金钱键盘⌨
 */
class BetMoneyKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableConstraintLayout(context, attrs, defStyleAttr) {
        private val TAG = "NumberKeyboardView2"
    private val binding by lazy { LayoutBetMoneyKeyboardBinding.inflate(LayoutInflater.from(context), this,true)  }
    /***************** listener ************/
    private var onClear: (() -> Unit)? = null
    private var onBackClick: (() -> Unit)? = null
    private var onBackLongPressRepeat: (() -> Unit)? = null
    private var onHideKeyboard: (() -> Unit)? = null
    private var onShowKeyboard: (() -> Unit)? = null
    private lateinit var ivFakerView:ImageView
    private var etMoney:EditText? = null
    private var tvMoney:TextView? = null
    private var _serialValue:Int? = null
    val serialValue:Int? get() = _serialValue
    private var removeWhenHide:Boolean = false

    // 每个 Keyboard 独立 ViewModel
    private var mViewModel: ComboBetMoneyKeyboardDialogViewModel? = null
    private val viewModelStore by lazy {
        ViewModelStore()
    }
    private val vmFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ComboBetMoneyKeyboardDialogViewModel(getKoin().get<BalanceRepository>()) as T
            }
        }
    }

    init {
        binding.btnCollapse.clickNoRepeat {
            hideKeyboard()
        }
        binding.btnBack.setOnClickOrLongPressListener (onClick = {
            this.onBackClick?.invoke()
        }, onLongPressRepeat = {
            this.onBackLongPressRepeat?.invoke()
        })
        binding.btnClear.clickNoRepeat {
            this.onClear?.invoke()
        }
        //固定numberKeyboard宽高，否则root做宽高动画的时候会出现显示错误
        binding.numberKeyboard.apply {
            layoutParams.width = 288.dp2px
            layoutParams.height = 210.dp2px
        }
    }

    /**
     * 创建遮罩
     */
    private fun createMaskView(){
        if(!::ivFakerView.isInitialized) {
            ivFakerView = ImageView(context).apply {
                id = arch.cayenne.lib.common.R.id.iv_faker_view
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
        destroyMaskView()
        val broLp = this@BetMoneyKeyboard.layoutParams as LayoutParams
        val lp = LayoutParams(0, 0).apply {
            topMargin = broLp.topMargin
            topToTop = broLp.topToTop
            topToBottom= broLp.topToBottom
            startToStart = broLp.startToStart
            endToEnd = broLp.endToEnd
        }
        (parent as ConstraintLayout).addView(ivFakerView, lp)
    }

    private fun destroyMaskView(){
        (ivFakerView.parent as? ViewGroup)?.removeView(ivFakerView)
    }

    fun bind(
        viewLifecycleOwner:LifecycleOwner,
        serialValue: Int,
        ed:EditText,
        tvMoney:TextView,
        removeWhenHide:Boolean,
        currentMoney: Long,
        minNumber: Long,
        maxNumber: Long,
        onMoneyChange:(serialValue:Int,money:Long)->Unit
    ){
        this._serialValue = serialValue
        this.etMoney = ed
        this.tvMoney = tvMoney
        this.removeWhenHide = removeWhenHide
        this.mViewModel = ViewModelProvider(viewModelStore, vmFactory)[ComboBetMoneyKeyboardDialogViewModel::class.java]

        initKeyboard(currentMoney,minNumber,maxNumber)
        initView()
        createObserver(viewLifecycleOwner, onMoneyChange)
        initListener()
    }

    private fun unBind(){
        this._serialValue = null
        this.etMoney = null
        this.removeWhenHide = false
        this.mViewModel = null
        this.viewModelStore.clear()
    }

    private fun initKeyboard(currentMoney: Long,minNumber: Long, maxNumber: Long) {
        if (minNumber != -1L && maxNumber != -1L) {
            mViewModel!!.setNumberLimit(minNumber, maxNumber)
        }
        if (currentMoney != 0L) {
            mViewModel!!.setNumber(currentMoney.getMoney())
        }
    }

    private fun initView(){
        binding.numberKeyboard.setOtherTextSize(13f)
        mViewModel!!.setMaxLength(etMoney!!.getMaxLength())
    }

    private fun createObserver(viewLifecycleOwner:LifecycleOwner,onMoneyChange:(serialValue:Int,money:Long)->Unit){
        val mViewModel = this.mViewModel ?: return
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            val etMoney = this.etMoney!!
            etMoney.setText(it)
            val length = it.length
            etMoney.setSelection(length)
            onMoneyChange.invoke(serialValue!!,it.toMoney())
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            etMoney!!.hint = R.string.et_money_hint.getString(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            it.msg?.let { msg ->
                showToast(msg)
            }
        }
        mViewModel.onCurrencyListener.observe(viewLifecycleOwner) {
            tvMoney!!.text = CurrencySymbols.getSymbol(it)
        }
    }

    private fun initListener() {
        val mViewModel = this.mViewModel ?: return
        setOnBackListener(onClick = {
            mViewModel.backNumber()
        }, onLongPressRepeat = {
            mViewModel.backNumber()
        })
        setOnClearListener {
            mViewModel.clearNumber()
        }
        setOnCalculatorClickListener(object : OnCalculatorClickListener {
            override fun onNumberClick(number: Int) {
                mViewModel.addNumber(number)
            }

            override fun onDotClick() {
                mViewModel.setDot()
            }

            override fun onOtherClick() {
                mViewModel.setMaxMoney()
            }

            override fun getOtherText(): String {
                return R.string.btn_max.getString()
            }
        })
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unBind()
    }

    fun hideKeyboard(duration: Long = 150) {
        if(!isAttachedToWindow) return
        collapseView(this,duration){
            if(removeWhenHide) (parent as? ViewGroup)?.removeView(this)
        }
        onHideKeyboard?.invoke()
    }

    fun showKeyBoard(duration: Long = 150,onEnd:(()->Unit)? = null){
        if(!isAttachedToWindow) return
        expandView(this,duration,onEnd)
        onShowKeyboard?.invoke()
    }

    fun isHideKeyboard():Boolean {
        return !binding.root.isVisible
    }

    private fun collapseView(view: View,duration:Long,onEnd: (() -> Unit)? = null) {
        val startV = view.let {
            it.measure(0,0)
            it.measuredHeight
        }
        val endV = 0
        val animator = ValueAnimator.ofInt(startV, endV).apply {
            view.doOnDetach {
                cancel()
            }
        }
        animator.duration = duration
        animator.interpolator = PathInterpolator(0.785f, 0.135f, 0.15f, 0.86f)

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            view.layoutParams = view.layoutParams.apply { height = animatedValue }
        }
        animator.doOnStart {
            view.layoutParams = view.layoutParams.apply { height = startV }
        }

        animator.doOnEnd {
            view.layoutParams = view.layoutParams.apply { height = endV }
            onEnd?.invoke()
        }

        animator.start()
    }

    private fun expandView(view: View, duration:Long, onEnd:(()->Unit)? = null) {
        // 先設為 0 高度，逐步展開
        val startV = 0
        val endV = view.let {
            it.measure(0,0)
            it.measuredHeight
        }
        val animator = ValueAnimator.ofInt(startV, endV).apply {
            view.doOnDetach {
                cancel()
            }
        }
        animator.duration = duration
        animator.interpolator = PathInterpolator(0.785f, 0.135f, 0.15f, 0.86f)

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            view.layoutParams = view.layoutParams.apply { height = animatedValue }
        }

        animator.doOnStart {
            view.layoutParams = view.layoutParams.apply { height = startV }
        }

        animator.doOnEnd  {
            view.layoutParams = view.layoutParams.apply { height = endV }
            onEnd?.invoke()
        }
        animator.start()
    }

    fun setRvQuickAmountAdapter(adapter: Adapter<*>){
        binding.rvQuickAmount.adapter = adapter
    }

    fun setOnHideKeyboardListener(lis:(() -> Unit)?){
        this.onHideKeyboard = lis
    }

    fun setOnShowKeyboardListener(lis:(() -> Unit)?){
        this.onShowKeyboard = lis
    }

    fun setOnCalculatorClickListener(lis:OnCalculatorClickListener){
        binding.numberKeyboard.setOnCalculatorClickListener(lis)
    }

    fun setOnBackListener(onClick: (() -> Unit)? = null,
                          onLongPressRepeat: (() -> Unit)? = null
    ) {
        this.onBackClick = onClick
        this.onBackLongPressRepeat = onLongPressRepeat
    }

    fun setOnClearListener(lis:(() -> Unit)?){
        this.onClear = lis
    }
}