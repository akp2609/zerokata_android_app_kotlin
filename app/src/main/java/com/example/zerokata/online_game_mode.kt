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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.zerokata.databinding.ActivityOnlineGameModeBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlin.system.exitProcess

var ismymove = iscode

class online_game_mode : AppCompatActivity() {

    private lateinit var viewbind: ActivityOnlineGameModeBinding

    lateinit var game_theme_sound: MediaPlayer

    var player1_win_count:Int = 0
    var player2_win_count:Int = 0

    var player1_cells = ArrayList<Int>()

    var player2_cells = ArrayList<Int>()
    var empty_cells = ArrayList<Int>()
    var active_user = 1

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure!")
        builder.setMessage("Do you want to exit the game?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id->
            game_theme_sound.release()
            removeCode()
            if (iscode){
                FirebaseDatabase.getInstance().reference.child("data").removeValue()
            }
            exitProcess(0)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener{
                dialog,id->
            dialog.cancel()
        })
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game_mode)
        viewbind = ActivityOnlineGameModeBinding.inflate(layoutInflater)
        setContentView(viewbind.root)

        game_theme_sound = MediaPlayer.create(this,R.raw.main_game_theme_song)
        game_theme_sound.start()

        viewbind.resetOnButton.setOnClickListener{
            reset()
        }

        FirebaseDatabase.getInstance().reference.child("data").child(code).addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var data = snapshot.value

                if(ismymove == true){
                    ismymove = false
                    moveonline(data.toString(), ismymove)
                }else{
                    ismymove = true
                    moveonline(data.toString(), ismymove)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                reset()
                errorMsg("Game Reset")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun Boxclicked(view: View) {
        if (ismymove) {
            val but = view as Button
            var cellid = 0
            when (but.id) {
                R.id.block_on_00 -> cellid = 1
                R.id.block_on_01 -> cellid = 2
                R.id.block_on_02 -> cellid = 3
                R.id.block_on_10 -> cellid = 4
                R.id.block_on_11 -> cellid = 5
                R.id.block_on_12 -> cellid = 6
                R.id.block_on_20 -> cellid = 7
                R.id.block_on_21 -> cellid = 8
                R.id.block_on_22 -> cellid = 9
                else->{
                    cellid = 0
                }
            }
            playerturn = false
            Handler(Looper.getMainLooper()).postDelayed(Runnable { playerturn = true }, 600)
            playnow(but, cellid)
            updateDatabase(cellid)
        }else{
            Toast.makeText(this, "Wait for your turn!", Toast.LENGTH_SHORT).show()
        }
    }


    fun playnow(cur_btn: Button, selected_cell: Int) {
        game_theme_sound.release()
        val game_sound_effect = MediaPlayer.create(this, R.raw.win_sound_effect)
        game_sound_effect.start()

        cur_btn.text = "X"
        viewbind.turnText.setText(R.string.turn_2)
        cur_btn.setTextColor(Color.parseColor("#ED6237"))
        player1_cells.add((selected_cell))
        empty_cells.add((selected_cell))
        cur_btn.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed(Runnable { game_sound_effect.release() },400)
        val winner = check_winner()

        game_theme_sound = MediaPlayer.create(this,R.raw.main_game_theme_song)
        game_theme_sound.start()
    }




    fun moveonline(data: String, move: Boolean) {

        if (move) {
            var buttonselected: Button?
            buttonselected = when (data.toInt()) {
                1 -> viewbind.blockOn00
                2 -> viewbind.blockOn01
                3 -> viewbind.blockOn02
                4 -> viewbind.blockOn10
                5 -> viewbind.blockOn11
                6 -> viewbind.blockOn12
                7 -> viewbind.blockOn20
                8 -> viewbind.blockOn21
                9 -> viewbind.blockOn22
                else -> {
                    viewbind.resetOnButton
                }
            }
            buttonselected.text = "O"
            viewbind.turnText.setText(R.string.turn_1)
            buttonselected.setTextColor(Color.parseColor("#D22BB804"))
            player2_cells.add(data.toInt())
            empty_cells.add(data.toInt())

            //Handler().postDelayed(Runnable { audio.pause() } , 500)

            buttonselected.isEnabled = false
            check_winner()
        }
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


                val mdialog_win = LayoutInflater.from(this).inflate(R.layout.winning_custome,null)
                val build = AlertDialog.Builder(this)
                build.setView(mdialog_win)
                build.setTitle("Game Over!")
                build.setMessage("Player 1 have won the game.."+"\n\n"+"Do you want to play again")
                val m_alert = build.show()

                mdialog_win.findViewById<Button>(R.id.play_again_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    reset()
                    win_sound_effect.release()
                }

                mdialog_win.findViewById<Button>(R.id.exit_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    removeCode()
                    win_sound_effect.release()
                    exitProcess(1)
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




                val win_sound_effect = MediaPlayer.create(this,R.raw.game_winninng_sound)
                win_sound_effect.start()


                val mdialog_win = LayoutInflater.from(this).inflate(R.layout.winning_custome,null)
                val build = AlertDialog.Builder(this)
                build.setView(mdialog_win)
                build.setTitle("Game Over!")
                build.setMessage("Player 2 have won the game.."+"\n\n"+"Do you want to play again")
                val m_alert = build.show()

                mdialog_win.findViewById<Button>(R.id.play_again_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    reset()
                    win_sound_effect.release()
                }

                mdialog_win.findViewById<Button>(R.id.exit_win_btn).setOnClickListener {
                    m_alert.dismiss()
                    removeCode()
                    win_sound_effect.release()
                    exitProcess(1)
                }

                Handler(Looper.getMainLooper()).postDelayed(Runnable { win_sound_effect.release() },5000)

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
                removeCode()
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
                1 -> viewbind.blockOn00
                2 -> viewbind.blockOn01
                3 -> viewbind.blockOn02
                4 -> viewbind.blockOn10
                5 -> viewbind.blockOn11
                6 -> viewbind.blockOn12
                7 -> viewbind.blockOn20
                8 -> viewbind.blockOn21
                9 -> viewbind.blockOn22
                else->{
                    viewbind.resetOnButton
                }
            }
            buttonselected.isEnabled = true
            buttonselected.text = ""
            viewbind.player1Text.setText(getString(R.string.player1_text,player1_win_count))
            viewbind.player2Text.setText(getString(R.string.player2_text,player2_win_count))
            ismymove = iscode

            if(iscode){
                FirebaseDatabase.getInstance().reference.child("data").child(code).removeValue()
            }

        }
    }

    fun updateDatabase(cur_cell : Int){
        FirebaseDatabase.getInstance().reference.child("data").child(code).push().setValue(cur_cell)
    }

    fun removeCode() {
        if (iscode) {
            FirebaseDatabase.getInstance().reference.child("codes").child(keyvalue).removeValue()
        }
    }

    fun buttondisable() {
        for (i in 1..9) {
            val buttonSelected = when (i) {
                1 -> viewbind.blockOn00
                2 -> viewbind.blockOn01
                3 -> viewbind.blockOn02
                4 -> viewbind.blockOn10
                5 -> viewbind.blockOn11
                6 -> viewbind.blockOn12
                7 -> viewbind.blockOn20
                8 -> viewbind.blockOn21
                9 -> viewbind.blockOn22
                else->{
                    viewbind.resetOnButton
                }
            }
            if (buttonSelected.isEnabled == true)
                buttonSelected.isEnabled = false
        }
    }


    fun buttoncelldisable(){
        empty_cells.forEach{
            val buttonselected = when(it){
                1 -> viewbind.blockOn00
                2 -> viewbind.blockOn01
                3 -> viewbind.blockOn02
                4 -> viewbind.blockOn10
                5 -> viewbind.blockOn11
                6 -> viewbind.blockOn12
                7 -> viewbind.blockOn20
                8 -> viewbind.blockOn21
                9 -> viewbind.blockOn22
                else->{
                    viewbind.resetOnButton
                }
            }
            if(buttonselected.isEnabled == true)
                buttonselected.isEnabled = false
        }
    }

    fun errorMsg(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    fun disablereset(){
        viewbind.resetOnButton.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed(Runnable { viewbind.resetOnButton.isEnabled = true},3000)
    }

}