package com.example.readitonce

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*



class wQQWWWWWWWWWWWWWWWWWWCreateSnap : AppCompatActivity() {


    private val imageName= UUID.randomUUID().toString() +".jpg"
    private var createImg: ImageView?=null
    private var message: EditText?=null
    private var intentsnap: Intent? = null
    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? =null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createImg=findViewById(R.id.createSnapImg)
        message=findViewById(R.id.msg)

        //check permission
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            // create intent to go the gallery to bring img
            getPhoto()
        }
        //bring the image and set it into imageView
        openSomeActivityForResult()
    }

    private fun getPhoto() {
        intentsnap = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    //get the img and put it in the ImageView
    private fun openSomeActivityForResult() {
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                try {
                    val intent1 = result.data
                    val selectedImage = intent1!!.data
                    //put the img choosen in imageView
                    createImg?.setImageURI(selectedImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // create intent to go the gallery to bring img
            getPhoto()
        }
    }

    fun select(view:View) {
        //Launch activity to get result
        someActivityResultLauncher?.launch(intentsnap)
    }


    fun next(view: View) {
        openLoadingDialog()
        // config the intent to go to ChooseUserActivity
        val intent1 = Intent(this, ChooseUserActivity::class.java)
        intent1.putExtra("message", message?.text.toString())
        if(createImg?.drawable!=null) {
            // Get the data from an ImageView as bytes
            createImg?.isDrawingCacheEnabled = true
            createImg?.buildDrawingCache()
            val bitmap = (createImg?.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            intent1.putExtra("imageName", imageName)
            //upload images to firebase
            val uploadTask =
                FirebaseStorage.getInstance().getReference().child("images").child(imageName)
                    .putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "upload failed", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                val downlUri =FirebaseStorage.getInstance().getReference().child("images").child(imageName).downloadUrl
                downlUri.addOnSuccessListener {
                    Log.i("imageUri !!!!!!","$it")
                    if(it!=null){
                        intent1.putExtra("imageUrl",it.toString())
                        intent1.putExtra("from", FirebaseAuth.getInstance().currentUser?.email)
                        Log.i("info !!!!!!!!!",it.toString())
                        startActivity(intent1)
                    }
                }
            }
        }
        else{
            //send only message text without msg
            startActivity(intent1)
        }
    }

    private fun openLoadingDialog() {
        val loadingDialog = loadingDialog(this)
        loadingDialog.startLoadingDialog()
        val handler = Handler()
        handler.postDelayed(
             { loadingDialog.dismisDialog() },
            5000
        ) //You can change this time as you wish
    }


}