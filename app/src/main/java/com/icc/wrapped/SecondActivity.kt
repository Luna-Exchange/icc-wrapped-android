package com.icc.wrapped

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.icc.iccwrapped.Env
import com.icc.iccwrapped.IccRecappedActivity
import com.icc.iccwrapped.OnAuthenticate
import com.icc.iccwrapped.User
import com.iccfan.iccwrapped.R

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val onAuthenticate = object : OnAuthenticate {
            override fun signIn() {

                val user = User("", "", "")

                IccRecappedActivity.launch(this@SecondActivity,
                    user,
                    env = Env.PRODUCTION, onStayInGame = {})
            }

            override fun onNavigateBack() {
                Toast.makeText(this@SecondActivity, "On Navigate back", Toast.LENGTH_LONG).show()
            }

        }

        val user = User("", "", "")

        IccRecappedActivity.launch(context = this,
            env = Env.PRODUCTION,
            onStayInGame = {
            Toast.makeText(this@SecondActivity, "onStayInGame", Toast.LENGTH_LONG).show()
        }, onAuthenticate = onAuthenticate,
            onDestroyCalled = {
                Toast.makeText(this@SecondActivity, "onDestroy", Toast.LENGTH_LONG).show()
            })
    }
}