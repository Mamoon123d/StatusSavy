package com.digital.statussavvy.activity

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import com.digital.statussavvy.R

class OverHome:Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.over_home)
        val frame=findViewById<FrameLayout>(R.id.frame)
        frame.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action== MotionEvent.ACTION_DOWN){
                finish()
            }
            true
        }
    }
}