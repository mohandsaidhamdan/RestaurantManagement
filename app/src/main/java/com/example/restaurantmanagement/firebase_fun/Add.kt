package com.example.restaurantmanagement.firebase_fun
import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class Add {

    var db = Firebase.firestore

    fun createAccount(email: String, password: String, username:String , tableName: String) {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "email" to email,
            "password" to password,
            "type_account" to "user",
            "username" to username
        )

// Add a new document with a generated ID
        db.collection(tableName)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }
}