package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import arch.cayenne.lib.common.R
import arch.cayenne.lib.skin.widget.SkinnableProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameRoundProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SkinnableProgressBar(context, attrs) {
    var triggerTime = 2000
    var job: Job? = null
    private var onTriggerListener: (() -> Unit)? = null

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initView(context, attrs, defStyleAttr)
        context.withStyledAttributes(attrs, R.styleable.GameRoundProgressBar) {
            val bgColor = getColor(
                R.styleable.GameRoundProgressBar_backgroundColor,
                Color.WHITE
            )
            val progColor = getColor(
                R.styleable.GameRoundProgressBar_progressColor,
                Color.GRAY
            )
            triggerTime = getInteger(
                R.styleable.GameRoundProgressBar_triggerTime,
                2000
            )
            progressDrawable = RoundProgressDrawable(
                backgroundColor = bgColor,
                progressColor = progColor
            )
            max = triggerTime
            startTimeTrigger()
        }
    }

    private fun startTimeTrigger() {
        progress = 0
        (progressDrawable as RoundProgressDrawable).progress = 0f
        if (job != null) {
            job!!.cancel()
        }
        val startTime = SystemClock.elapsedRealtime()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val elapsed = (SystemClock.elapsedRealtime() - startTime) % max
                if (progress > elapsed) {  //mean (SystemClock.elapsedRealtime() - startTime) > max
                    withContext(Dispatchers.Main) {
                        onTriggerListener?.invoke()
                    }
                }
                progress = elapsed.toInt()
                (progressDrawable as RoundProgressDrawable).progress = progress / max.toFloat()
                delay(10)
            }
        }
    }

    fun setTriggerListener(listener: () -> Unit) {
        onTriggerListener = listener
    }

    fun resetTriggerJob() {
        startTimeTrigger()
    }
}