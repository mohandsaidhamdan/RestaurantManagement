package com.example.restaurantmanagement


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.restaurantmanagement.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottile.playAnimation()

        binding.lottile.animate().setDuration(2000).alpha(1f).withEndAction{

            if (getSharedPreferences("MyShared", Context.MODE_PRIVATE).getBoolean("check", false)){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


    }
}