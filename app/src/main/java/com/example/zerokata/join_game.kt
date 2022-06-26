package com.example.zerokata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.example.zerokata.databinding.ActivityJoinGameBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

var iscode = true
var code = "null"
var codefound = false
var checktemp = true
var keyvalue:String = "null"

class join_game : AppCompatActivity() {
    private lateinit var viewbind: ActivityJoinGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)
        viewbind = ActivityJoinGameBinding.inflate(layoutInflater)
        setContentView(viewbind.root)
        viewbind.progressBar.visibility = View.GONE

        viewbind.createBtn.setOnClickListener{
            code = "null"
            codefound = false
            checktemp = true
            keyvalue = "null"
            code = viewbind.enter.text.toString()
            viewbind.createBtn.visibility = View.GONE
            viewbind.joinBtn.visibility = View.GONE
            viewbind.enter.visibility = View.GONE
            viewbind.DontHaveCode.visibility = View.GONE
            viewbind.progressBar.visibility = View.VISIBLE
            if(code != "null" && code != null && code != ""){
                iscode = true
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var check = isvalueavailable(snapshot, code)

                        Handler(Looper.getMainLooper()).postDelayed({
                            if(check == true){
                            viewbind.joinBtn.visibility = View.VISIBLE
                                viewbind.createBtn.visibility = View.VISIBLE
                                viewbind.enter.visibility = View.VISIBLE
                                viewbind.DontHaveCode.visibility = View.VISIBLE
                                viewbind.progressBar.visibility = View.GONE
                                errorMsg("Sorry code already exists try another code")
                            }else{
                                FirebaseDatabase.getInstance().reference.child("codes").push().setValue(code)
                                isvalueavailable(snapshot,code)
                                checktemp = false
                                Handler(Looper.getMainLooper()).postDelayed({
                                    accepted()
                                    errorMsg("Please Don't go back")
                                },300)
                            }
                        },3000)
                    }
                })
            }else {
                viewbind.joinBtn.visibility = View.VISIBLE
                viewbind.createBtn.visibility = View.VISIBLE
                viewbind.enter.visibility = View.VISIBLE
                viewbind.DontHaveCode.visibility = View.VISIBLE
                viewbind.progressBar.visibility = View.GONE
                errorMsg("Enter code properly")
            }
        }

        viewbind.joinBtn.setOnClickListener{
            code = "null"
            codefound = false
            checktemp = true
            keyvalue = "null"
            code = viewbind.enter.text.toString()

            if(code != "null" && code != null && code != ""){
                viewbind.createBtn.visibility = View.GONE
                viewbind.joinBtn.visibility = View.GONE
                viewbind.enter.visibility = View.GONE
                viewbind.DontHaveCode.visibility = View.GONE
                viewbind.progressBar.visibility = View.VISIBLE
                iscode = false
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data:Boolean = isvalueavailable(snapshot, code)

                        Handler(Looper.getMainLooper()).postDelayed({
                          if(data == true){
                              codefound = true
                              accepted()
                              viewbind.joinBtn.visibility = View.VISIBLE
                              viewbind.createBtn.visibility = View.VISIBLE
                              viewbind.enter.visibility = View.VISIBLE
                              viewbind.DontHaveCode.visibility = View.VISIBLE
                              viewbind.progressBar.visibility = View.GONE
                          }else {
                              viewbind.joinBtn.visibility = View.VISIBLE
                              viewbind.createBtn.visibility = View.VISIBLE
                              viewbind.enter.visibility = View.VISIBLE
                              viewbind.DontHaveCode.visibility = View.VISIBLE
                              viewbind.progressBar.visibility = View.GONE
                              errorMsg("Invalid Code")
                          }
                        },2000)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }else{
                errorMsg("Enter Code Properly")
            }
        }
    }

    fun accepted(){
      startActivity(Intent(this,online_game_mode::class.java))
        viewbind.createBtn.visibility = View.VISIBLE
        viewbind.joinBtn.visibility = View.VISIBLE
        viewbind.enter.visibility = View.VISIBLE
        viewbind.DontHaveCode.visibility = View.VISIBLE
        viewbind.progressBar.visibility = View.GONE
    }

    fun errorMsg(value: String){
       Toast.makeText(this,value,Toast.LENGTH_SHORT).show()
    }

    fun isvalueavailable(snapshot: DataSnapshot,code: String): Boolean {
        var data = snapshot.children
        data.forEach{
            var value = it.getValue().toString()
            if(value == code){
                keyvalue = it.key.toString()
                return true
            }
        }
        return false
    }
}