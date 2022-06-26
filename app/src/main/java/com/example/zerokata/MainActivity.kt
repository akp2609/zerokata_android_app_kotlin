package com.example.zerokata

import android.content.DialogInterface
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.zerokata.databinding.ActivityMainBinding
import kotlin.system.exitProcess

var playerturn:Boolean = true

class MainActivity : AppCompatActivity() {

    private lateinit var viewbind: ActivityMainBinding
    lateinit var game_theme_sound:MediaPlayer

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure!")
        builder.setMessage("Do you want to exit the game?")
        builder.setPositiveButton("Yes",DialogInterface.OnClickListener{dialog,id->
            game_theme_sound.release()
           super.onBackPressed()
        })
        builder.setNegativeButton("No",DialogInterface.OnClickListener{
            dialog,id->
            dialog.cancel()
        })
        builder.show()
    }

    override fun onPause() {
        super.onPause()
        game_theme_sound.release()
    }

    override fun onResume() {
        super.onResume()
        game_theme_sound = MediaPlayer.create(this,R.raw.main_game_theme_song)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewbind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbind.root)

        game_theme_sound = MediaPlayer.create(this,R.raw.main_game_theme_song)
        game_theme_sound.start()

        viewbind.resetButton.setOnClickListener {
            reset()
        }
    }

    var player1_win_count:Int = 0
    var player2_win_count:Int = 0

    fun Boxclicked(view: View) {
        if (playerturn) {
            val but = view as Button
            var cellid = 0
            when (but.id) {
                R.id.block_00 -> cellid = 1
                R.id.block_01 -> cellid = 2
                R.id.block_02 -> cellid = 3
                R.id.block_10 -> cellid = 4
                R.id.block_11 -> cellid = 5
                R.id.block_12 -> cellid = 6
                R.id.block_20 -> cellid = 7
                R.id.block_21 -> cellid = 8
                R.id.block_22 -> cellid = 9
            }
            playerturn = false
            Handler(Looper.getMainLooper()).postDelayed(Runnable { playerturn = true }, 600)
            playnow(but, cellid)
        }
    }

    var player1_cells = ArrayList<Int>()

    var player2_cells = ArrayList<Int>()
    var empty_cells = ArrayList<Int>()
    var active_user = 1


    fun playnow(cur_btn: Button, selected_cell: Int) {
        game_theme_sound.pause()
        val game_sound_effect = MediaPlayer.create(this, R.raw.win_sound_effect)
        if(active_user == 1){
            game_sound_effect.start()
            cur_btn.text = "X"
            cur_btn.setTextColor(Color.parseColor("#ED6237"))
            player1_cells.add((selected_cell))
            empty_cells.add((selected_cell))
            cur_btn.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed(Runnable { game_sound_effect.release() },400)
            val winner = check_winner()
            if(winner == 1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset()},2000)
            }else if(singleuser){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { robot() },1000)
            }else
                active_user = 2

        }else {
            cur_btn.text = "o"
            game_sound_effect.start()
            cur_btn.setTextColor(Color.parseColor("#5EF434"))
            active_user = 1
            player2_cells.add((selected_cell))
            empty_cells.add(selected_cell)
            game_sound_effect.start()
            cur_btn.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed(Runnable { game_sound_effect.release() },400)
            val winner = check_winner()
            if(winner == 1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset() },2000)
            }
        }
        game_theme_sound.start()
    }

    fun check_winner(): Int{
        val win_sound_effect = MediaPlayer.create(this,R.raw.game_winninng_sound)
        if ((player1_cells.contains(1) && player1_cells.contains(2) && player1_cells.contains(3)) || (player1_cells.contains(
                1
            ) && player1_cells.contains(4) && player1_cells.contains(7)) ||
            (player1_cells.contains(3) && player1_cells.contains(6) && player1_cells.contains(9)) || (player1_cells.contains(
                7
            ) && player1_cells.contains(8) && player1_cells.contains(9)) ||
            (player1_cells.contains(4) && player1_cells.contains(5) && player1_cells.contains(6)) || (player1_cells.contains(
                1
            ) && player1_cells.contains(5) && player1_cells.contains(9)) ||
            player1_cells.contains(3) && player1_cells.contains(5) && player1_cells.contains(7) || (player1_cells.contains(2) && player1_cells.contains(
                5
            ) && player1_cells.contains(8))
        ){
            player1_win_count += 1
            buttondisable()
            win_sound_effect.start()
            disablereset()
            Handler(Looper.getMainLooper()).postDelayed(Runnable { win_sound_effect.release() },5000)

            if(singleuser == false && online == false){

                val mdialog_win = LayoutInflater.from(this).inflate(R.layout.winning_custome,null)
                val build = AlertDialog.Builder(this)
                build.setView(mdialog_win)
                build.setTitle("Congratulations!")
                build.setMessage("Player 1 have won the game.."+"\n\n"+"Do you want to play again")
                val m_alert = build.show()

                mdialog_win.findViewById<Button>(R.id.play_again_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    reset()
                    win_sound_effect.release()
                }

                mdialog_win.findViewById<Button>(R.id.exit_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    win_sound_effect.release()
                    exitProcess(1)
                }

            }else{
            val mdialog_win = LayoutInflater.from(this).inflate(R.layout.winning_custome,null)
            val build = AlertDialog.Builder(this)
            build.setView(mdialog_win)
            build.setTitle("Congratulations!")
            build.setMessage("You have won the game.."+"\n\n"+"Do you want to play again")
            val m_alert = build.show()

            mdialog_win.findViewById<Button>(R.id.play_again_win_btn).setOnClickListener {
                m_alert.dismiss()
                reset()
                win_sound_effect.release()
            }

            mdialog_win.findViewById<Button>(R.id.exit_win_btn).setOnClickListener {
                m_alert.dismiss()
                win_sound_effect.release()
                exitProcess(1)
            }
            }
            return 1

        }else if ((player2_cells.contains(1) && player2_cells.contains(2) && player2_cells.contains(3)) || (player2_cells.contains(
                1
            ) && player2_cells.contains(4) && player2_cells.contains(7)) ||
            (player2_cells.contains(3) && player2_cells.contains(6) && player2_cells.contains(9)) || (player2_cells.contains(
                7
            ) && player2_cells.contains(8) && player2_cells.contains(9)) ||
            (player2_cells.contains(4) && player2_cells.contains(5) && player2_cells.contains(6)) || (player2_cells.contains(
                1
            ) && player2_cells.contains(5) && player2_cells.contains(9)) ||
            player2_cells.contains(3) && player2_cells.contains(5) && player2_cells.contains(7) || (player2_cells.contains(2) && player2_cells.contains(
                5
            ) && player2_cells.contains(8))
        ){
          player2_win_count += 1
            buttondisable()
            disablereset()


            if(singleuser == false && online == false){

                val win_sound_effect = MediaPlayer.create(this,R.raw.game_winninng_sound)
                win_sound_effect.start()


                val mdialog_win = LayoutInflater.from(this).inflate(R.layout.winning_custome,null)
                val build = AlertDialog.Builder(this)
                build.setView(mdialog_win)
                build.setTitle("Congratulations!")
                build.setMessage("Player 2 have won the game.."+"\n\n"+"Do you want to play again")
                val m_alert = build.show()

                mdialog_win.findViewById<Button>(R.id.play_again_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    reset()
                    win_sound_effect.release()
                }

                mdialog_win.findViewById<Button>(R.id.exit_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    win_sound_effect.release()
                    exitProcess(1)
                }

                Handler(Looper.getMainLooper()).postDelayed(Runnable { win_sound_effect.release() },5000)

            }else{

                val loose_sound = MediaPlayer.create(this,R.raw.loose_audio)

                loose_sound.start()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { loose_sound.release() },6000)

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.loose_alert,null)
            val builder = AlertDialog.Builder(this)
            builder.setView(mDialogView)
            builder.setTitle("Sorry! You lost")
            val malert = builder.show()
            mDialogView.findViewById<Button>(R.id.play_again_loose_btn).setOnClickListener {
                malert.dismiss()
                reset()
                loose_sound.release()
            }
            mDialogView.findViewById<Button>(R.id.exit_loose_btn).setOnClickListener{
                malert.dismiss()
                loose_sound.release()
                exitProcess(1)
            }
            }

            return 1

        }else if (empty_cells.contains(1) && empty_cells.contains(2) && empty_cells.contains(3) && empty_cells.contains(4) && empty_cells.contains(5) && empty_cells.contains(6) && empty_cells.contains(7) &&
            empty_cells.contains(8) && empty_cells.contains(9)
        ){

            val mdialog_inflate = LayoutInflater.from(this).inflate(R.layout.draw_custom,null)
            val builder = AlertDialog.Builder(this)
            builder.setView(mdialog_inflate)
            val malert = builder.show()
            mdialog_inflate.findViewById<Button>(R.id.play_again_draw_btn).setOnClickListener {
                malert.dismiss()
                reset()
            }
            mdialog_inflate.findViewById<Button>(R.id.exit_draw_btn).setOnClickListener {
                malert.dismiss()
                exitProcess(1)
            }
            return 1
        }

    return 0
    }


    fun reset(){

        player1_cells.clear()
        player2_cells.clear()
        empty_cells.clear()
        active_user = 1
        for(i in 1..9){
            var buttonselected: Button?
            buttonselected = when(i){
                1 -> viewbind.block00
                2 -> viewbind.block01
                3 -> viewbind.block02
                4 -> viewbind.block10
                5 -> viewbind.block11
                6 -> viewbind.block12
                7 -> viewbind.block20
                8 -> viewbind.block21
                9 -> viewbind.block22
                else->{
                    viewbind.resetButton
                }
            }
            buttonselected.isEnabled = true
            buttonselected.text = ""
            viewbind.player1Text.setText(getString(R.string.player1_text,player1_win_count))
            viewbind.player2Text.setText(getString(R.string.player2_text,player2_win_count))

        }
    }

    fun robot(){
      val rnd  = (1..9).random()
        if(empty_cells.contains(rnd)){
            robot()
        }else{
            val buttonselected: Button?
            buttonselected = when(rnd){
                1 -> viewbind.block00
                2 -> viewbind.block01
                3 -> viewbind.block02
                4 -> viewbind.block10
                5 -> viewbind.block11
                6 -> viewbind.block12
                7 -> viewbind.block20
                8 -> viewbind.block21
                9 -> viewbind.block22
                else -> {
                    viewbind.resetButton
                }
            }
            empty_cells.add((rnd))
            val game_audio = MediaPlayer.create(this,R.raw.win_sound_effect)
            game_audio.start()
            Handler(Looper.getMainLooper()).postDelayed(Runnable { game_audio.release() },500)
            buttonselected.text = "o"
            buttonselected.setTextColor(Color.parseColor("#5EF434"))
            player2_cells.add(rnd)
            buttonselected.isEnabled = false
            var checkwinner:Int = check_winner()
            if(checkwinner == 1){
                Handler(Looper.getMainLooper()).postDelayed(Runnable { reset() },2000)
            }
        }
    }

    fun buttondisable(){
        for (i in 1..9){
            val buttonselected = when(i){
                1 -> viewbind.block00
                2 -> viewbind.block01
                3 -> viewbind.block02
                4 -> viewbind.block10
                5 -> viewbind.block11
                6 -> viewbind.block12
                7 -> viewbind.block20
                8 -> viewbind.block21
                9 -> viewbind.block22
                else -> {
                    viewbind.resetButton
                }
            }
            if(buttonselected.isEnabled == true)
                buttonselected.isEnabled = false
        }
    }

    fun disablereset(){

        viewbind.resetButton.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed(Runnable { viewbind.resetButton.isEnabled = true },2200)

    }

}