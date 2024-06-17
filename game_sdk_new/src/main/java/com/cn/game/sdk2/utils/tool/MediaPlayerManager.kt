package com.cn.game.sdk2.utils.tool

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer

object MediaPlayerManager {
    private lateinit var context: Context
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    @SuppressLint("StaticFieldLeak")
    private val audioQueue: MutableList<Int> = mutableListOf()
    private var isPlaying = false

    fun initialize(context: Context) {
        MediaPlayerManager.context = context.applicationContext
        mediaPlayer.setOnCompletionListener {
            playNext()
        }
    }

    fun addAudioToQueue(audioResId: Int) {
        audioQueue.add(audioResId)
        if (!isPlaying) {
            playNext()
        }
    }

    private fun playNext() {
        if (audioQueue.isNotEmpty()) {
            val audioResId = audioQueue.removeAt(0)
            try {
                mediaPlayer.reset()
                val afd = context.resources.openRawResourceFd(audioResId)
                afd?.let {
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    isPlaying = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            isPlaying = false
        }
    }
}