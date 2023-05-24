package com.example.restaurantmanagement

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.parser.IntegerParser
import com.example.restaurantmanagement.databinding.ActivityOrderDetailesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class OrderDetailes : AppCompatActivity() {
    lateinit var binding : ActivityOrderDetailesBinding
    lateinit var db : FirebaseFirestore
  var alltotal  = 0.0
    var price = 0.0
    var names = ""
    var imageView_url = ""
     var storage = FirebaseStorage.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")

        getDataFromFirebase(name.toString())
        binding.apply {
//            =======Strart order====
            var countNumber =  txtNumber.text.toString().toInt()
            btnBluse.setOnClickListener {

                    var num = ++countNumber
                txtNumber.text = num.toString()
                alltotal =price * countNumber.toDouble()
                total.text = alltotal.toString()
            }

            btnLess.setOnClickListener {
                if (countNumber == 0) {
                    Toast.makeText(this@OrderDetailes, "بنفعش اقل من صفر", Toast.LENGTH_SHORT).show()

                } else {

                    val num = --countNumber
                    txtNumber.text = num.toString()
                    alltotal -= price
                    total.text = alltotal.toString()
                }
            }
//            =======End order====


//            تحويل الطلب

            btnOrder.setOnClickListener {
                val MEAL_ID = intent.getStringExtra("id")
                val email = getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", "").toString()
                createOrderTable(email,names, txtNumber.text.toString(), alltotal , price)
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

    private fun getDataFromFirebase(name : String) {
        var db = Firebase.firestore

        db.collection("meals").whereEqualTo("name", name).get().addOnSuccessListener { user ->
            for (user in user) {

                 names = user.get("name").toString()
                  price = user.get("price").toString().toDouble()

                val resturantName2 = user.get("resturantName")
                imageView_url = user.get("image").toString()
                binding.apply {
                    DwnloadImage(imageView_url.toString(), imgMeal)
                    mealName.text = names.toString()
                    total.text = price.toString()
                    resturantName.text = resturantName2.toString()
                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this , MainActivity::class.java))
    }


    //createOrderTable
    private fun createOrderTable(email : String, name : String, count : String, totalPrice : Double, price : Double) {
        val data = hashMapOf<String, Any>(
            "email" to email,
            "name" to name,
            "count" to count,
            "price" to price,
            "imageView_url" to imageView_url,
            "totalPrice" to totalPrice
        )
        db = Firebase.firestore
        db.collection("orders").add(data).addOnSuccessListener {
            val i= Intent(this@OrderDetailes , OrderList::class.java)
            startActivity(i)
        }
    }
}