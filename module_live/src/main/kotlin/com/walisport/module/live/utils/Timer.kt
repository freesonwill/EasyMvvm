package com.walisport.module.live.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Timer(private val initialTimeInSeconds: Long) {
    private var isRunning = false
    private var elapsedTime = initialTimeInSeconds
    private var job: Job? = null

    fun start(coroutineScope: CoroutineScope, onTick: (String) -> Unit) {
        if (isRunning) return
        isRunning = true

        job = coroutineScope.launch {
            while (isRunning && elapsedTime > 0) {
                val minutes = elapsedTime / 60
                val seconds = elapsedTime % 60
                onTick(String.format("%02d:%02d", minutes, seconds))
                delay(1000)
                elapsedTime++
            }
            if (elapsedTime <= 0) {
                onTick("00:00")
                stop()
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }

    fun reset() {
        stop()
        elapsedTime = initialTimeInSeconds
    }

    fun getCurrentTime(): String {
        val minutes = elapsedTime / 60
        val seconds = elapsedTime % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}