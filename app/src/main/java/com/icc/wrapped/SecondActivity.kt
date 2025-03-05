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

        val user = User(
            authToken = "JhbGciOiJSUzI1NiIsImtpZCI6ImtJUG5DTU1xanhWbjJ4UFNwTnN2QXpLcHdNSEtvOWo0UmdZUXVJVzFWQ2MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJlYjI1MjZlNy0zZTVlLTQwNmUtODc4Zi1iZmIxZjc2YWYwZmYiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmljYy1jcmlja2V0LmNvbS80YmE5ZjI3Ni0yNDk3LTQ0MGUtYTZiYS1kMDVkNDgzMWI5NjcvdjIuMC8iLCJleHAiOjE3Mzg0ODU4NDMsIm5iZiI6MTczODQ4NTU0Mywic3ViIjoiZGJlMTk4Y2ItMDdiYy00ZTczLThiZDktMGI0YWJlNDY2YTNlIiwiZ2l2ZW5fbmFtZSI6Ikl5YW51IiwiZmFtaWx5X25hbWUiOiJGYWxheWUiLCJuYW1lIjoiSXlhbnUgRmFsYXllIiwiZW1haWxzIjpbIml5YW51b2x1d2FAaW5zb21uaWFsYWJzLmlvIl0sImNvdW50cnkiOiJORyIsImpvYlRpdGxlIjoiIiwidGlkIjoiNGJhOWYyNzYtMjQ5Ny00NDBlLWE2YmEtZDA1ZDQ4MzFiOTY3IiwidGZwIjoiQjJDXzFBX1NpZ25pblNpZ251cCIsIm5vbmNlIjoiNTZkMDZiM2ItNWVlMS00N2Q1LWJmMzYtOWIwYTVlMDA4NjQzIiwic2NwIjoiY2F0YWxvZ3VlLnJlYWQiLCJhenAiOiIzNmRhNjA1NC04NTUyLTQwMTUtYTZiMi1iN2I2OTA2ZmQ0YWIiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3Mzg0ODU1NDN9.A2MCIHjL-YsfavYVZHwe0b1chN2muEuh1MmJ_Bs3hRLI4RhlBfHl9g1i7UcEAAq6UiNeKmHoYRm9aYWZorEPwg8i676v5bqOKD7ig6Ow9LuEQPCVHaVwP-sGxjaMuH8gk4HZeKIzbP4juBvmniiXoFRVgdaSOM6rPd8GZtyeQuuEAg-TgNXbxgsek6v6U6KqlqW7LF_NMqcxqvL4SfrcMbkv8FYqOcLtDIDADHkDFxjg7LYU8epizVunUCTDoIBMCkrxtaCHO7s0mrL83vVQRgQPI5kPZ6DFihJCDsW4wH9d88Qf8NnXp1UAq13XelAmZbzFzt1xYdA3usZo-1WzJQ" ,
            name = "Iyanu Falaye",
            email = "falaycornelius+04@gmail.com"
        )

        val onAuthenticate = object : OnAuthenticate {
            override fun signIn() {
                val user = User(
                    authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6Imlia2V5MTIzNDUiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJkZXYtc2NhbGUtZnJhbmtmdXJ0LWF3cyIsImlzcyI6Imh0dHBzOi8vZGVsdGF0cmUuY29tL2lzc3VlciIsImV4cCI6MTc0MTE5MTc1NCwiaWF0IjoxNzQxMTg5OTU0LCJuYmYiOjE3NDExODk5NTQsInN1YiI6IjZmNDczNTA0LTFkOTAtNDYxOC1hOWY1LWJmYWZjOGI4ODU1YSIsInByb2ZpbGVfaWQiOiI2ZjQ3MzUwNC0xZDkwLTQ2MTgtYTlmNS1iZmFmYzhiODg1NWEiLCJpZHBfc3ViIjoiOTM1OGU5MTQtMThhMi00MzBmLThjMjMtMWYxZjc0ZTk1YjhkIiwidGVuYW50X2lkIjoiaWNjZGV2IiwianRpIjoiMDk4OTEwMWEtNWMwYi00MTliLWJlMzAtZGNiMzJiYTAxZjExIiwic2lkIjoiNmI5NDRkNWMtYzE2YS00NDg3LWI2MGUtMThkMWJkNDkzNWQwIiwiZGV2aWNlX2lkIjoiMTIyZTk1ODktYmYzMC00MWZkLWJjZjUtYjNkNDUwNThiMjYzIiwiZGV2aWNlX3R5cGUiOiJ3ZWJfYnJvd3NlciIsInJvbGUiOiJ1c2VyIiwic2NvcGUiOiJjYXRhbG9ndWUucmVhZCIsInR5cCI6ImFjY2VzcyIsImF6cCI6IjdkMThjYjY2LTViYWYtNDk3OS04NWMzLTlhNmNjMzdlNjIyZSIsImlkcF9iYWciOnsicHJvdmlkZXIiOiJBREIyQyIsInRmcCI6IkIyQ18xQV9TSUdOSU5TSUdOVVAifX0.rkzxJlWUFaRKYOQNLuL7cwe-D7wjw2xIxe28A2q_kVwaY5yk0TaE2GjFxLiy5EHr8RnUsxAheYlHczYvplkTMAiDX5ejLcj3aiAAVPiRaunCwcK2YEn-SYbPu94LloQx4wMUGdSBFa81pigwTUN2zCpOLhYdNXhd2EpWgmRsoJm0NbKn2M8bx4DfvlZj6AsOOnQwpqwTDANKYfp0J7FIHRlbSfGZbuLL7nhkhYECZ3JHGl5ywkskE9smrYeyA9-bcJ7it-3kDVoDJBWImPOeeEafSiNUyapaLJKa-J_oFu1dmga6eTyS1_PBG9ka2NQlwXebZiiL_jsgPWQBHI3LRg",
                    name = "Oluwole Benson",
                    email = "b.elvis1991@gmail.com"
                )
                IccRecappedActivity.launch(this@SecondActivity, user, onStayInGame = {})
            }

            override fun onNavigateBack() {
                Toast.makeText(this@SecondActivity, "On Navigate back", Toast.LENGTH_LONG).show()
            }

        }

        IccRecappedActivity.launch(context = this,
            env = Env.DEVELOPMENT,
            onStayInGame = {
            Toast.makeText(this@SecondActivity, "onStayInGame", Toast.LENGTH_LONG).show()
        }, onAuthenticate = onAuthenticate)
    }
}