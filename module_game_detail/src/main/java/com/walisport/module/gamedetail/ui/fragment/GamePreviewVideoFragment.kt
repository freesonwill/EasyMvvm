package com.walisport.module.gamedetail.ui.fragment

import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewOutlineProvider
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.gamedetail.databinding.FragmentGamePreviewVideoBinding
import com.walisport.module.gamedetail.ui.viewmodel.GamePreviewImageViewModel
import java.io.IOException
import kotlin.reflect.KClass

class GamePreviewVideoFragment(private val url: String) :
    BaseFragment<GamePreviewImageViewModel, FragmentGamePreviewVideoBinding>(), TextureView.SurfaceTextureListener {
    override val vbClass: KClass<FragmentGamePreviewVideoBinding> = FragmentGamePreviewVideoBinding::class
    override val vmClass: KClass<GamePreviewImageViewModel> = GamePreviewImageViewModel::class

    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.tag = arguments?.getInt(ARG_ADAPTER_POSITION, -1) ?: -1
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, 20.dp2px.toFloat())
                }
            }
            surfaceTextureListener = this@GamePreviewVideoFragment
        }
    }

    override fun initListener() = Unit

    override suspend fun createObserver() = Unit

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
        surface = Surface(p0)
        prepareAndPlay()
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) = Unit

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        surface?.release()
        surface = null
        return true
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) = Unit

    private fun prepareAndPlay() {
        val currentSurface = this.surface ?: return

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setSurface(currentSurface)
                isLooping = true
                prepareAsync()
                setOnPreparedListener {
                    adjustAspectRatio(it.videoWidth, it.videoHeight)
                    it.start()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun adjustAspectRatio(videoWidth: Int, videoHeight: Int) {
        val textureView = mBinding.root as? TextureView ?: return
        if (videoWidth == 0 || videoHeight == 0) return

        val viewWidth = textureView.width
        val viewHeight = textureView.height
        val viewAspectRatio = viewWidth.toFloat() / viewHeight
        val videoAspectRatio = videoWidth.toFloat() / videoHeight

        val matrix = Matrix()
        var scaleX = 1.0f
        var scaleY = 1.0f

        if (videoAspectRatio > viewAspectRatio) {
            scaleY = viewAspectRatio / videoAspectRatio
        } else {
            scaleX = videoAspectRatio / viewAspectRatio
        }

        val pivotPointX = viewWidth / 2f
        val pivotPointY = viewHeight / 2f

        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY)
        textureView.setTransform(matrix)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }



    companion object {
        private const val ARG_ADAPTER_POSITION = "ARG_ADAPTER_POSITION"

        fun newInstance(position: Int, url: String): GamePreviewVideoFragment {
            val fragment = GamePreviewVideoFragment(url)
            fragment.arguments = Bundle().apply {
                putInt(ARG_ADAPTER_POSITION, position)
            }
            return fragment
        }
    }
}