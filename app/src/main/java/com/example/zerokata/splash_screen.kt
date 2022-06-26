package com.example.zerokata

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toolbar
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatButton
import com.example.zerokata.databinding.ActivitySplashScreenBinding

class splash_screen : AppCompatActivity() {

    var vid:VideoView? = null
    var splashbtn:AppCompatButton? = null
    private lateinit var viewbind: ActivitySplashScreenBinding

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(1)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_splash_screen)

        if (!isTaskRoot()
            && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
            && getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish()
            return
        }

        viewbind = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(viewbind.root)

        viewbind.playBtn.setOnClickListener{
            startActivity(Intent(this,players_num::class.java))
            finish()
        }

        vid = findViewById(R.id.video_splash)
        splashbtn = findViewById(R.id.play_btn)

        val path = "android.resource://" + packageName + "/" + R.raw.cross_vid
        val uri = Uri.parse(path)
        vid!!.setVideoURI(uri)
        vid!!.start()

        vid!!.setOnCompletionListener {
            if(isFinishing){
                true
            }
            startActivity(Intent(this,players_num::class.java))
            finish()
        }


    }
}