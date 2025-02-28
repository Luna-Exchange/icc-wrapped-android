package com.icc.wrapped

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icc.iccwrapped.Env
import com.icc.iccwrapped.IccWrappedActivity
import com.icc.iccwrapped.OnAuthenticate
import com.icc.iccwrapped.User
import com.iccfan.iccwrapped.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = User(
            authToken = "JhbGciOiJSUzI1NiIsImtpZCI6ImtJUG5DTU1xanhWbjJ4UFNwTnN2QXpLcHdNSEtvOWo0UmdZUXVJVzFWQ2MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJlYjI1MjZlNy0zZTVlLTQwNmUtODc4Zi1iZmIxZjc2YWYwZmYiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmljYy1jcmlja2V0LmNvbS80YmE5ZjI3Ni0yNDk3LTQ0MGUtYTZiYS1kMDVkNDgzMWI5NjcvdjIuMC8iLCJleHAiOjE3Mzg0ODU4NDMsIm5iZiI6MTczODQ4NTU0Mywic3ViIjoiZGJlMTk4Y2ItMDdiYy00ZTczLThiZDktMGI0YWJlNDY2YTNlIiwiZ2l2ZW5fbmFtZSI6Ikl5YW51IiwiZmFtaWx5X25hbWUiOiJGYWxheWUiLCJuYW1lIjoiSXlhbnUgRmFsYXllIiwiZW1haWxzIjpbIml5YW51b2x1d2FAaW5zb21uaWFsYWJzLmlvIl0sImNvdW50cnkiOiJORyIsImpvYlRpdGxlIjoiIiwidGlkIjoiNGJhOWYyNzYtMjQ5Ny00NDBlLWE2YmEtZDA1ZDQ4MzFiOTY3IiwidGZwIjoiQjJDXzFBX1NpZ25pblNpZ251cCIsIm5vbmNlIjoiNTZkMDZiM2ItNWVlMS00N2Q1LWJmMzYtOWIwYTVlMDA4NjQzIiwic2NwIjoiY2F0YWxvZ3VlLnJlYWQiLCJhenAiOiIzNmRhNjA1NC04NTUyLTQwMTUtYTZiMi1iN2I2OTA2ZmQ0YWIiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3Mzg0ODU1NDN9.A2MCIHjL-YsfavYVZHwe0b1chN2muEuh1MmJ_Bs3hRLI4RhlBfHl9g1i7UcEAAq6UiNeKmHoYRm9aYWZorEPwg8i676v5bqOKD7ig6Ow9LuEQPCVHaVwP-sGxjaMuH8gk4HZeKIzbP4juBvmniiXoFRVgdaSOM6rPd8GZtyeQuuEAg-TgNXbxgsek6v6U6KqlqW7LF_NMqcxqvL4SfrcMbkv8FYqOcLtDIDADHkDFxjg7LYU8epizVunUCTDoIBMCkrxtaCHO7s0mrL83vVQRgQPI5kPZ6DFihJCDsW4wH9d88Qf8NnXp1UAq13XelAmZbzFzt1xYdA3usZo-1WzJQ" ,
            name = "Iyanu Falaye",
            email = "iyanuoluwa@insomnialabs.io"
        )

        val onAuthenticate = object : OnAuthenticate {
            override fun signIn() {
                val user = User(
                    authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImtJUG5DTU1xanhWbjJ4UFNwTnN2QXpLcHdNSEtvOWo0UmdZUXVJVzFWQ2MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJlYjI1MjZlNy0zZTVlLTQwNmUtODc4Zi1iZmIxZjc2YWYwZmYiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmljYy1jcmlja2V0LmNvbS80YmE5ZjI3Ni0yNDk3LTQ0MGUtYTZiYS1kMDVkNDgzMWI5NjcvdjIuMC8iLCJleHAiOjE3Mzg0ODU4NDMsIm5iZiI6MTczODQ4NTU0Mywic3ViIjoiZGJlMTk4Y2ItMDdiYy00ZTczLThiZDktMGI0YWJlNDY2YTNlIiwiZ2l2ZW5fbmFtZSI6Ikl5YW51IiwiZmFtaWx5X25hbWUiOiJGYWxheWUiLCJuYW1lIjoiSXlhbnUgRmFsYXllIiwiZW1haWxzIjpbIml5YW51b2x1d2FAaW5zb21uaWFsYWJzLmlvIl0sImNvdW50cnkiOiJORyIsImpvYlRpdGxlIjoiIiwidGlkIjoiNGJhOWYyNzYtMjQ5Ny00NDBlLWE2YmEtZDA1ZDQ4MzFiOTY3IiwidGZwIjoiQjJDXzFBX1NpZ25pblNpZ251cCIsIm5vbmNlIjoiNTZkMDZiM2ItNWVlMS00N2Q1LWJmMzYtOWIwYTVlMDA4NjQzIiwic2NwIjoiY2F0YWxvZ3VlLnJlYWQiLCJhenAiOiIzNmRhNjA1NC04NTUyLTQwMTUtYTZiMi1iN2I2OTA2ZmQ0YWIiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3Mzg0ODU1NDN9.A2MCIHjL-YsfavYVZHwe0b1chN2muEuh1MmJ_Bs3hRLI4RhlBfHl9g1i7UcEAAq6UiNeKmHoYRm9aYWZorEPwg8i676v5bqOKD7ig6Ow9LuEQPCVHaVwP-sGxjaMuH8gk4HZeKIzbP4juBvmniiXoFRVgdaSOM6rPd8GZtyeQuuEAg-TgNXbxgsek6v6U6KqlqW7LF_NMqcxqvL4SfrcMbkv8FYqOcLtDIDADHkDFxjg7LYU8epizVunUCTDoIBMCkrxtaCHO7s0mrL83vVQRgQPI5kPZ6DFihJCDsW4wH9d88Qf8NnXp1UAq13XelAmZbzFzt1xYdA3usZo-1WzJQ" ,
                    name = "Iyanu Falaye",
                    email = "iyanuoluwa@insomnialabs.io"
                )
                IccWrappedActivity.launch(this@MainActivity, user, onStayInGame = {})
            }

            override fun onNavigateBack() {
                Toast.makeText(this@MainActivity, "On Navigate back", Toast.LENGTH_LONG).show()
            }

        }

        IccWrappedActivity.launch(context = this, user = user, env = Env.DEVELOPMENT, onStayInGame = {
        }, onAuthenticate = onAuthenticate)
    }
}