package com.example.restaurantmanagement

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.example.restaurantmanagement.databinding.ActivityAddMealBinding
import com.example.restaurantmanagement.model.Meal
import com.example.restaurantmanagement.model.Resturant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class add_meal : AppCompatActivity() {
    lateinit var binding: ActivityAddMealBinding
    private var db = Firebase.firestore
    var storage = Firebase.storage
    lateinit var url: String
    val IMAGE_PICK_REQUEST = 100
    lateinit var image: ImageView

    lateinit var adpter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getResturantNames()


        binding.apply {

            btnAdd.setOnClickListener {
                val name = txtName.text.toString()
                val txtDescription = txtDescription.text.toString()
                val price = txtPrice.text.toString()
                val ratingBar = ratingBar.rating.toString().toFloat()
                try {
                    if (name.isNotEmpty() && txtDescription.isNotEmpty() && price.isNotEmpty()) {
                        createMeal(Meal(name, txtDescription, price.toDouble(), ratingBar, url, binding.resturantNames.text.toString()))

                    } else {
                        Toast.makeText(
                            this@add_meal,
                            "All Fields Is Required",
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


    //    Add Resturants in firebase
    fun createMeal(meal: Meal) {
        // Create a new user with a first and last name
        val meal = hashMapOf(
            "name" to meal.name,
            "description" to meal.description,
            "price" to meal.price,
            "rate" to meal.rate,
            "image" to meal.image,
            "resturantName" to meal.resturantName
        )

// Add a new document with a generated ID
        db.collection("meals")
            .add(meal)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Document Snapshot added with ID: ${documentReference.id}")
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

    //    return Name Resturant
    private fun getResturantNames() {
        var resturantsName = ArrayList<String>()
        db.collection("resturants")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nameList = document.getString("name")

                    resturantsName.add(nameList.toString())


                }

//                adapter spiner
                adpter = ArrayAdapter(
                    this,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, resturantsName
                )
                binding.resturantNames.setAdapter(adpter)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

    }

}