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

                val user = User(
                    authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImtJUG5DTU1xanhWbjJ4UFNwTnN2QXpLcHdNSEtvOWo0UmdZUXVJVzFWQ2MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJlYjI1MjZlNy0zZTVlLTQwNmUtODc4Zi1iZmIxZjc2YWYwZmYiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmljYy1jcmlja2V0LmNvbS80YmE5ZjI3Ni0yNDk3LTQ0MGUtYTZiYS1kMDVkNDgzMWI5NjcvdjIuMC8iLCJleHAiOjE3NDEzNjAyMjYsIm5iZiI6MTc0MTM1OTkyNiwic3ViIjoiZGJlMTk4Y2ItMDdiYy00ZTczLThiZDktMGI0YWJlNDY2YTNlIiwiZ2l2ZW5fbmFtZSI6Ikl5YW51IiwiZmFtaWx5X25hbWUiOiJGYWxheWUiLCJuYW1lIjoiSXlhbnUgRmFsYXllIiwiZW1haWxzIjpbIml5YW51b2x1d2FAaW5zb21uaWFsYWJzLmlvIl0sImNvdW50cnkiOiJORyIsImpvYlRpdGxlIjoiIiwidGlkIjoiNGJhOWYyNzYtMjQ5Ny00NDBlLWE2YmEtZDA1ZDQ4MzFiOTY3IiwidGZwIjoiQjJDXzFBX1NpZ25pblNpZ251cCIsIm5vbmNlIjoiODNlYzE0NzAtNWY3NC00ZDE4LWE5MjAtZTY3ZDJkMzI0MWFjIiwic2NwIjoiY2F0YWxvZ3VlLnJlYWQiLCJhenAiOiIzNmRhNjA1NC04NTUyLTQwMTUtYTZiMi1iN2I2OTA2ZmQ0YWIiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3NDEzNTk5MjZ9.poGSF2ChCrQCPOPFTbqtZOzty8V0woWdKJtB6a_LgeAE8FsebZ2NE_WPvE7ssznBkWTziY3MaI9SqRtGUMUR1M1QDT3N5sjn2RIzZv6D2GGBorG6ovQvVHxj0abbvyqphCnz-f9qG9VyOIZM7ncGAgMn-LgHRT2J2--ViXJua04czIbQ8JQDaDXEbAQ4ZVQ0eI8n9m0SsxLcqjiFeLXnIScIBm5n6THNnOzcQj_TLAj3oTXeMvqUrTWKBUscOzDi0xWSmecX1qPkbNo513Ozy8v-2AhhfteYSgznMHNcceVzWzE93GLGaUNOaXYHJ7R81aB_tzjMRKXApxzFbupgXA",
                    name = "",
                    email = "iyanuoluwa@insomnialabs.io"
                )

                IccRecappedActivity.launch(this@SecondActivity, user, onStayInGame = {})
            }

            override fun onNavigateBack() {
                Toast.makeText(this@SecondActivity, "On Navigate back", Toast.LENGTH_LONG).show()
            }

        }

        val user = User(
            authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImtJUG5DTU1xanhWbjJ4UFNwTnN2QXpLcHdNSEtvOWo0UmdZUXVJVzFWQ2MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJlYjI1MjZlNy0zZTVlLTQwNmUtODc4Zi1iZmIxZjc2YWYwZmYiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmljYy1jcmlja2V0LmNvbS80YmE5ZjI3Ni0yNDk3LTQ0MGUtYTZiYS1kMDVkNDgzMWI5NjcvdjIuMC8iLCJleHAiOjE3NDEzNjAyMjYsIm5iZiI6MTc0MTM1OTkyNiwic3ViIjoiZGJlMTk4Y2ItMDdiYy00ZTczLThiZDktMGI0YWJlNDY2YTNlIiwiZ2l2ZW5fbmFtZSI6Ikl5YW51IiwiZmFtaWx5X25hbWUiOiJGYWxheWUiLCJuYW1lIjoiSXlhbnUgRmFsYXllIiwiZW1haWxzIjpbIml5YW51b2x1d2FAaW5zb21uaWFsYWJzLmlvIl0sImNvdW50cnkiOiJORyIsImpvYlRpdGxlIjoiIiwidGlkIjoiNGJhOWYyNzYtMjQ5Ny00NDBlLWE2YmEtZDA1ZDQ4MzFiOTY3IiwidGZwIjoiQjJDXzFBX1NpZ25pblNpZ251cCIsIm5vbmNlIjoiODNlYzE0NzAtNWY3NC00ZDE4LWE5MjAtZTY3ZDJkMzI0MWFjIiwic2NwIjoiY2F0YWxvZ3VlLnJlYWQiLCJhenAiOiIzNmRhNjA1NC04NTUyLTQwMTUtYTZiMi1iN2I2OTA2ZmQ0YWIiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3NDEzNTk5MjZ9.poGSF2ChCrQCPOPFTbqtZOzty8V0woWdKJtB6a_LgeAE8FsebZ2NE_WPvE7ssznBkWTziY3MaI9SqRtGUMUR1M1QDT3N5sjn2RIzZv6D2GGBorG6ovQvVHxj0abbvyqphCnz-f9qG9VyOIZM7ncGAgMn-LgHRT2J2--ViXJua04czIbQ8JQDaDXEbAQ4ZVQ0eI8n9m0SsxLcqjiFeLXnIScIBm5n6THNnOzcQj_TLAj3oTXeMvqUrTWKBUscOzDi0xWSmecX1qPkbNo513Ozy8v-2AhhfteYSgznMHNcceVzWzE93GLGaUNOaXYHJ7R81aB_tzjMRKXApxzFbupgXA",
            name = "",
            email = "iyanuoluwa@insomnialabs.io"
        )

        IccRecappedActivity.launch(context = this,
            env = Env.DEVELOPMENT,
            onStayInGame = {
            Toast.makeText(this@SecondActivity, "onStayInGame", Toast.LENGTH_LONG).show()
        }, onAuthenticate = onAuthenticate)
    }
}