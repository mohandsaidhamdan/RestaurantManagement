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
import com.example.restaurantmanagement.databinding.ActivityEditMealBinding
import com.example.restaurantmanagement.model.Meal
import com.example.restaurantmanagement.model.Resturant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class EditMeal : AppCompatActivity() {
    lateinit var binding : ActivityEditMealBinding
    lateinit var db: FirebaseFirestore
    var storage = Firebase.storage
    lateinit var url: String
    var image_old = ""
    var check = false
    val IMAGE_PICK_REQUEST = 100
    lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        val name = intent.getStringExtra("name").toString()
        getMealData(name)

        binding.apply {
            btnAdd.setOnClickListener {
                if (txtName.text.toString().isNotEmpty() && txtDescription.text.toString().isNotEmpty() && txtPrice.text.toString().isNotEmpty()) {
                    if (check) {

                        getOldIDUpdate(name, Meal(txtName.text.toString(), txtDescription.text.toString(), txtPrice.text.toString().toDouble(), ratingBar.rating, url))
                    } else {
                        getOldIDUpdate(name, Meal(txtName.text.toString(), txtDescription.text.toString(), txtPrice.text.toString().toDouble(), ratingBar.rating, image_old))
                    }
                } else Toast.makeText(this@EditMeal, "All Fields Is Required", Toast.LENGTH_SHORT).show()
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

//    return old id from meal to update
    fun getOldIDUpdate(names: String, meal: Meal) {

        db.collection("meals").whereEqualTo("name", names)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.id
                    updateMeal(id, meal)
                }


            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

//    function update
    private fun updateMeal(oldId: String, meal: Meal) {
        val meals = hashMapOf<String, Any>(
            "name" to meal.name,
            "description" to meal.description,
            "image" to meal.image,
            "location" to meal.price,
            "rate" to meal.rate

        )
        db.collection("meals").document(oldId).update(meals).addOnSuccessListener {
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

//    get Data meal old and set input
    fun getMealData(names: String) {
        db.collection("meals").whereEqualTo("name", names)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name").toString()
                    val descrition = document.get("description").toString()
                    val rate = document.get("rate")
                    val locations = document.get("price")
                    val image_url = document.get("image")
                    image_old = image_url.toString()
                    binding.apply {
                        txtName.setText(name)
                        txtDescription.setText(descrition)
                        ratingBar.rating = rate.toString().toFloat()
                        txtPrice.setText(locations.toString())
                        DwnloadImage(image_url.toString(), image)
                    }


                }


            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }
}