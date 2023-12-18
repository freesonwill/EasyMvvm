package com.xcjh.app.component

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.engagelab.privates.push.api.CustomMessage
import com.xcjh.app.R
import com.xcjh.app.ui.details.MatchDetailActivity

/**
 * 用于演示自定义消息展示
 */
class CustomMessageActivity : Activity() {
    private var tvMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent)
        tvMessage = findViewById(R.id.tv_message)
        onIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
    }

    private fun onIntent(intent: Intent?) {
        try {
            if (intent == null) {
                return
            }
            val customMessage = intent.getParcelableExtra<CustomMessage>("message") ?: return
            Log.d(TAG, "customMessage:$customMessage")
            customMessage.extras.apply {
                val matchId = getString("matchId","")
                val isPureFlow = getString("isPureFlow")
                val matchType = getString("matchType","1")
                val liveId = getString("liveId")
                val anchorId = getString("anchorId")
                MatchDetailActivity.open(matchType = matchType, matchId = matchId, anchorId = anchorId)
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Log.d(TAG, "onIntent failed:" + throwable.message)
        }
    }

    companion object {
        private const val TAG = "push===CustomMessageActivity"
    }
}