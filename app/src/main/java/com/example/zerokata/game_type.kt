package com.example.zerokata

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zerokata.databinding.ActivityGameTypeBinding

var online:Boolean = false

class game_type : AppCompatActivity() {

    private lateinit var viewbind:ActivityGameTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_type)
        viewbind = ActivityGameTypeBinding.inflate(layoutInflater)
        setContentView(viewbind.root)

        viewbind.offlineBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            online = false
            singleuser = false
        }

        viewbind.onlineBtn.setOnClickListener {
          startActivity(Intent(this,join_game::class.java))
            online = true
            singleuser = true
        }
    }
}