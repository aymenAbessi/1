package com.example.readitonce

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChooseUserActivity : AppCompatActivity() {

    private var chooseListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var usersId: ArrayList<String> = ArrayList()
    var arrayAdapter: ArrayAdapter<String>? =null
    private var database: DatabaseReference? = null
    private var snapMap:Map<String, String>?=null
    var filter = ArrayList<String?>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        //Initialize the dataBAse and the list view
        database = FirebaseDatabase.getInstance().reference
        chooseListView = findViewById(R.id.users)
         arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        chooseListView?.adapter = arrayAdapter

        //get the Users gmail to choose from them
        database!!.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                emails.clear()
                usersId.clear()
                snapshot.value.toString()
                for (snap in snapshot.children) {
                    usersId.add(snap.key.toString())
                    emails.add(snap.child("email").value.toString())
                }
                arrayAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



        initSearchView()
        chooseListView?.onItemClickListener= AdapterView.OnItemClickListener { _, _, i, _ ->
            var majic=0
            if(filter.isEmpty()){
                majic=i
            }else{
                majic=emails.indexOf(filter.get(i))
            }

           FirebaseAuth.getInstance().let {
               try{
                   if(it.currentUser?.email != null){
                       //send data to firebase using map structure
                       snapMap= mapOf(
                           "from" to  it.currentUser!!.email.toString(),
                           "imageName" to intent.getStringExtra("imageName").toString(),
                           "imageUrl" to intent.getStringExtra("imageUrl").toString(),
                           "message" to intent.getStringExtra("message").toString(),
                           "notified" to "false"
                       )
                       database!!.child("users").child(usersId.get(majic)).child("snaps").push()
                           .setValue(snapMap)
                       Toast.makeText(this,"sent successfully",Toast.LENGTH_SHORT).show()
                   }
               }catch(e:Exception){
                   e.printStackTrace()
               }
            }

        }






    }


    fun initSearchView() {
         filter = ArrayList<String?>()
        val searchView = findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                filter.clear()
                for (ss in emails) {
                    if (ss.toLowerCase().contains(s.toLowerCase())) {
                        filter.add(ss)
                    }
                }
                arrayAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    filter
                )
                chooseListView?.setAdapter(arrayAdapter)
                return false
            }
        })
    }

    fun all(view: View?) {
        arrayAdapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, emails)
        chooseListView?.setAdapter(arrayAdapter)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //after sending message you press back button to go the main page (snapsActivity)
        val intent = Intent(this, SnapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}