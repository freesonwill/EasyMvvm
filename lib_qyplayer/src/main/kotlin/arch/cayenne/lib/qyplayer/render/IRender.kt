package arch.cayenne.lib.qyplayer.render

import android.view.Surface
import android.view.View

interface IRenderCallback {

    fun onSurfaceCreate(surface: Surface)

    fun onSurfaceChanged(width: Int, height: Int)

    fun onSurfaceDestroyed()
}

interface IRenderView {

    fun addRenderCallback(callback: IRenderCallback)

    fun getView(): View
}

