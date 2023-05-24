package com.example.restaurantmanagement

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.restaurantmanagement.databinding.ActivityAddResturant2Binding
import com.example.restaurantmanagement.model.Resturant
import com.example.restaurantmanagement.ui.home.HomeFragment
import com.example.restaurantmanagement.ui.resturant.ResturantFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class add_resturant2 : AppCompatActivity() {
    lateinit var binding: ActivityAddResturant2Binding
    private var db = Firebase.firestore
    var storage = Firebase.storage
    lateinit var url: String
    val IMAGE_PICK_REQUEST = 100
    lateinit var image: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddResturant2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Inflate the layout for this fragment

        binding.apply {

            btnAdd.setOnClickListener {
                val name = txtName.text.toString()
                val txtDescription = txtDescription.text.toString()
                val location = location.text.toString()
                val ratingBar = ratingBar.rating.toString().toFloat()
                try {
                    if (name.isNotEmpty() && txtDescription.isNotEmpty() && location.isNotEmpty()) {
                        createResturant(Resturant(name, txtDescription, location, ratingBar, url))

                    } else {
                        Toast.makeText(
                            this@add_resturant2,
                            "all flid is requard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: java.lang.Exception) {

                }


            }

            image.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_PICK_REQUEST)
            }

        }


    }

    //    Add Resturants in fier
    fun createResturant(resturant: Resturant) {
        // Create a new user with a first and last name
        val resturant = hashMapOf(
            "name" to resturant.name,
            "description" to resturant.description,
            "loation" to resturant.loation,
            "rate" to resturant.rate,
            "image" to resturant.image
        )

// Add a new document with a generated ID
        db.collection("resturants")
            .add(resturant)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    private fun uploadImage(imageUri: Uri) {
        val storageRef = storage.reference
        url = "images/${imageUri.lastPathSegment}"
        val imageRef = storageRef.child(url)
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUrl = task.result?.storage?.downloadUrl
            } else {
                // Image upload failed
                // Handle the failure
                Toast.makeText(this, "failed upload image", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
//            url_image = data.data!!
            binding.image.setImageURI(selectedImageUri)
            // Perform the image upload
            selectedImageUri?.let { uploadImage(it) }
        }
    }

}