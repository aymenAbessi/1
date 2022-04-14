package com.example.readitonce

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SnapsActivity : AppCompatActivity() {


    var mNotificationManager: NotificationManager? = null
    var id = 0

private lateinit var auth: FirebaseAuth
private var listOfSnaps: ListView?=null
var emails :ArrayList<String> = ArrayList()
    var notif :ArrayList<DataSnapshot> = ArrayList()
private var database: DatabaseReference? =null
    var arrayAdapter: MyAdapter? =null
var snaps:ArrayList<DataSnapshot> =ArrayList()
    lateinit var nomsg: TextView




override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_snaps)


    database= FirebaseDatabase.getInstance().reference
    listOfSnaps=findViewById(R.id.snaps)
    // Initialize Firebase Auth
    auth = Firebase.auth

    nomsg=findViewById(R.id.nomsg)


     arrayAdapter= MyAdapter(this,R.layout.item, emails)
    listOfSnaps?.adapter=arrayAdapter




    sendNotification()



    listOfSnaps?.onItemClickListener= AdapterView.OnItemClickListener { adapterView, view, i, l ->
        if(!(snaps.isEmpty())) {
            openLoadingDialog()
            intent = Intent(this, SnapView::class.java)
            val snapInfo=snaps.get(i).children
            val array: ArrayList<DataSnapshot> = ArrayList()
            for(sn in snapInfo) {
                array.add(sn)
            }
            //pass code/imageName/message/imageUrl to SnapView to aff the message
            intent.putExtra("code",snaps.get(i).key.toString())
            intent.putExtra("imageName", array.get(1).value.toString())
            intent.putExtra("message",array.get(3).value.toString())
            intent.putExtra("imageUrl",array.get(2).value.toString())
            startActivity(intent)
        }
    }


}


override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val infuleter= MenuInflater(this)
    infuleter.inflate(R.menu.snaps,menu)
    return super.onCreateOptionsMenu(menu)
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
     if(item.itemId ==R.id.newSnap){
         //go the creation page of snaps
    val intent= Intent(this,CreateSnap::class.java)
    startActivity(intent)
     }else if(item.itemId ==R.id.Logout) {
         //sign out and back to the main page
         auth.signOut()
         finish()
     }
return super.onOptionsItemSelected(item)
}

override fun onBackPressed() {
    super.onBackPressed()
    //sign out and back to the main page
    auth.signOut()
}
    fun openLoadingDialog() {
        val loadingDialog = loadingDialog(this)
        loadingDialog.startLoadingDialog()
        val handler = Handler()
        handler.postDelayed(
             { loadingDialog.dismisDialog() },
            5000
        ) //You can change this time as you wish
    }


    fun notify(array:ArrayList<DataSnapshot>,s:String) {
        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val ii = Intent(applicationContext, SnapView::class.java)

        //pass code/imageName/message/imageUrl to SnapView to aff the message
        ii.putExtra("code",s)
        ii.putExtra("imageName", array.get(1).value.toString())
        ii.putExtra("message",array.get(3).value.toString())
        ii.putExtra("imageUrl",array.get(2).value.toString())


        val pendingIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), ii, 0)
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        mBuilder.setContentTitle("read it once")
        mBuilder.setContentText(array.get(0).value.toString())
        mBuilder.setSmallIcon(R.drawable.notification)
        mBuilder.setAutoCancel(true)
        mBuilder.priority = Notification.PRIORITY_MAX
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager!!.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager!!.notify(id, mBuilder.build())
        id++
    }



    fun sendNotification(){
        //get messages from firebase
        database!!.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cuurentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
                emails.clear()
                snaps.clear()
                notif.clear()

                //add snaps and email to the arrays
                for(snap in snapshot.children) {
                    if (snap.child("email").value.toString() == cuurentUser) {
                        val gogo = snap.child("snaps")


                        for(child in gogo.children) run {
                            snaps.add(child)

                        }

                        for (snap2 in gogo.children) {

                            if (snap2.child("from").value != null)
                                emails.add(snap2.child("from").value.toString())
                            if (snap2.child("notified").value.toString()=="false"){

                                notif.add(snap2)
                                FirebaseAuth.getInstance().currentUser?.let {
                                    database!!.child("users").child(
                                        it.uid).child("snaps").child(snap2.key.toString()).child("notified").setValue("true")
                                }
                            }
                        }
                    }

                    arrayAdapter!!.notifyDataSetChanged()
                }


                if(snaps.isEmpty()){
                    nomsg.visibility=View.VISIBLE
                }else{

                    nomsg.visibility=View.INVISIBLE
                }
                for(i in notif){
                    val snapInfo=i.children
                    val array: ArrayList<DataSnapshot> = ArrayList()
                    for(sn in snapInfo) {
                        array.add(sn)
                    }

                    notify(array,i.key.toString())
                    array.clear()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }


}