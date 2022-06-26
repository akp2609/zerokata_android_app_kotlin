package com.example.zerokata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zerokata.databinding.ActivityPlayersNumBinding

var singleuser:Boolean = false
class players_num : AppCompatActivity() {


    private lateinit var viewbind:ActivityPlayersNumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_num)
        viewbind = ActivityPlayersNumBinding.inflate(layoutInflater)
        setContentView(viewbind.root)

        viewbind.singlePlayerBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            singleuser = true
        }

        viewbind.multiPlayerBtn.setOnClickListener{
            startActivity(Intent(this,game_type::class.java))
            singleuser = false
        }

    }
}