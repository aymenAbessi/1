package com.example.readitonce

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var emailText: EditText?=null
    private var passwordText: EditText?=null
    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize graphics element
        emailText=findViewById(R.id.email)
        passwordText=findViewById(R.id.password)
        database = Firebase.database.reference

        // Initialize Firebase Auth
        auth = Firebase.auth

        //pass to the main page if you are already login
        if(auth.currentUser != null){
            logIn()
        }
    }

    fun goFun(view:View) {
        //check if we can login
        openLoadingDialog()
        auth.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    //sing up the users
                    auth.createUserWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString()).addOnCompleteListener(this ){
                            task ->
                        if(task.isSuccessful){
                            //add to dataBase
                            task.result.user?.let {
                                database.child("users").child(
                                    it.uid).child("email").setValue(emailText?.text.toString())
                            }
                            logIn()
                        }else{
                            Toast.makeText(this,"login failed try again", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }


    }

    private fun logIn(){
        //move to the next activity
        val intent= Intent(this,SnapsActivity::class.java)
        intent.putExtra("currentusers", emailText?.text)
        startActivity(intent)
    }

    fun openLoadingDialog() {
        val loadingDialog = loadingDialog(this@MainActivity)
        loadingDialog.startLoadingDialog()
        val handler = Handler()
        handler.postDelayed(
             { loadingDialog.dismisDialog() },
            5000
        ) //You can change this time as you wish
    }
}