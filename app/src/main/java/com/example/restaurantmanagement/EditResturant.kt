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
import com.example.restaurantmanagement.databinding.ActivityEditResturantBinding
import com.example.restaurantmanagement.model.Resturant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class EditResturant : AppCompatActivity() {
    lateinit var binding: ActivityEditResturantBinding
    lateinit var db: FirebaseFirestore
    var storage = Firebase.storage
    lateinit var url: String
    var image_old = ""
    var check = false
    val IMAGE_PICK_REQUEST = 100
    lateinit var image: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditResturantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        val name = intent.getStringExtra("name").toString()
        getResturantData(name)

        binding.apply {


            btnAdd.setOnClickListener {
                if (txtName.text.toString().isNotEmpty() && txtDescription.text.toString().isNotEmpty() && location.text.toString().isNotEmpty()) {
                    if (check) {
                        Toast.makeText(this@EditResturant, check.toString(), Toast.LENGTH_SHORT).show()
                        getOldIDUpdate(name, Resturant(txtName.text.toString(), txtDescription.text.toString(), location.text.toString(), ratingBar.rating, url))
                    } else {
                        getOldIDUpdate(name, Resturant(txtName.text.toString(), txtDescription.text.toString(), location.text.toString(), ratingBar.rating, image_old))
                    }
                } else Toast.makeText(this@EditResturant, "All Fields Is Required", Toast.LENGTH_SHORT).show()
            }
            image.setOnClickListener {
                check = true
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_PICK_REQUEST)
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getOldIDUpdate(names: String, resturant: Resturant) {

        db.collection("resturants").whereEqualTo("name", names)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {


                    val id = document.id

                    updateResturant(id, resturant)
                }


            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun updateResturant(oldId: String, resturant: Resturant) {
        val resturants = hashMapOf<String, Any>(
            "name" to resturant.name,
            "description" to resturant.description,
            "image" to resturant.image,
            "location" to resturant.loation,
            "rate" to resturant.rate

        )
        db.collection("resturants").document(oldId).update(resturants).addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Update Is Failed", Toast.LENGTH_SHORT).show()
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

    //    Get Image From Firebase Storage
    private fun DwnloadImage(uri: String, imageView2: ImageView) {
        val storageRef =
            storage.reference.child(uri) // Replace "images/image.jpg" with your actual image path in Firebase Storage

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            Picasso.get().load(imageUrl).into(imageView2)
            // Use the imageUrl as needed (e.g., display the image, store it in a database, etc.)
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred while retrieving the download URL
            Log.e("abd", exception.message.toString())
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

    fun getResturantData(names: String) {

        db.collection("resturants").whereEqualTo("name", names)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name").toString()
                    val descrition = document.get("description").toString()
                    val rate = document.get("rate")
                    val locations = document.get("loation")
                    val image_url = document.get("image")
                    image_old = image_url.toString()
                    binding.apply {
                        txtName.setText(name)
                        txtDescription.setText(descrition)
                        ratingBar.rating = rate.toString().toFloat()
                        location.setText(locations.toString())
                        DwnloadImage(image_url.toString(), image)
                    }


                }


            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

}