package arch.cayenne.lib.qyplayer.render

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import arch.cayenne.lib.qyplayer.render.IRenderCallback
import arch.cayenne.lib.qyplayer.render.IRenderView

class SurfaceRenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), IRenderView, SurfaceHolder.Callback {
    private var mRenderCallback: IRenderCallback? = null

    init {
        holder.addCallback(this)
    }

    override fun addRenderCallback(callback: IRenderCallback) {
        mRenderCallback = callback
    }

    override fun getView() = this

    override fun surfaceCreated(holder: SurfaceHolder) {
        mRenderCallback?.onSurfaceCreate(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mRenderCallback?.onSurfaceChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        holder.surface?.release()
        mRenderCallback?.onSurfaceDestroyed()
    }
}