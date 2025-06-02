package arch.cayenne.lib.qyplayer.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import arch.cayenne.lib.qyplayer.render.IRenderCallback
import arch.cayenne.lib.qyplayer.render.IRenderView
import com.xxx.qyplayer.log.L

class TextureRenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), IRenderView, TextureView.SurfaceTextureListener {
    private var mRenderCallback: IRenderCallback? = null
    private var mSurface: Surface? = null

    init {
        surfaceTextureListener = this
    }

    override fun addRenderCallback(callback: IRenderCallback) {
        mRenderCallback = callback
    }

    override fun getView(): View = this

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurface = Surface(surface)
        mRenderCallback?.onSurfaceCreate(mSurface!!)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        mRenderCallback?.onSurfaceChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mSurface?.release()
        mRenderCallback?.onSurfaceDestroyed()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        L.d("onSurfaceTextureUpdated")
    }
}