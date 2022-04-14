package com.example.readitonce

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class SnapView : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var msgText: TextView? = null
    private var msgImage: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap_view)

        //initialize the graphics elements
        msgText = findViewById(R.id.msgText)
        msgImage = findViewById(R.id.msgImage)


        //show the message text
        msgText?.text = intent.getStringExtra("message")
        val task = ImageDownload()
        val myImg: Bitmap
        try {
            myImg =
                //download the image
                task.execute(intent.getStringExtra("imageUrl"))
                    .get()!!
            msgImage?.setImageBitmap(myImg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    // create class to download the image from url
    class ImageDownload : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg p0: String?): Bitmap? {
            return try {
                val url = URL(p0[0])
                val urlC =
                    url.openConnection() as HttpURLConnection
                urlC.connect()
                val `in` = urlC.inputStream
                BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //delete message when press the backButton
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
            .child("snaps").child(intent.getStringExtra("code").toString()).removeValue()
        FirebaseStorage.getInstance().reference.child("images")
            .child(intent.getStringExtra("imageName").toString()).delete()
        Toast.makeText(this,"message deleted successfully",Toast.LENGTH_SHORT).show()
    }
}