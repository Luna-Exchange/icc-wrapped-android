package com.icc.wrapped

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iccfan.iccwrapped.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textview = findViewById<TextView>(R.id.text_view)
        textview.setOnClickListener {
           val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

    }
}