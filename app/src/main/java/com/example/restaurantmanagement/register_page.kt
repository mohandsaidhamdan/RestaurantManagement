package com.example.restaurantmanagement

import android.content.ContentValues

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.restaurantmanagement.databinding.FragmentRegisterPageBinding
import com.example.restaurantmanagement.firebase_fun.Add
import com.google.firebase.auth.FirebaseAuth



class register_page : Fragment()  {
    private val mAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRegisterPageBinding.inflate(inflater, container, false)
        binding.apply {

            registerButton.setOnClickListener {
                val email = emailR.text.toString()
                val password = passwordR.text.toString()
                val username = usernameR.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    Registering(email, password, username)

                } else {
                    Toast.makeText(context, "All Field Required", Toast.LENGTH_SHORT).show()
                }

            }
        }
        return binding.root
    }

    //    startRegistering
    private fun Registering(email: String, password: String, username : String) {


        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")



                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, login_page())
                        .addToBackStack(null) //هاد عشان تخليه يرجع على الصفحة الي قبل
                        .commit()
                    Toast.makeText(activity, "createUserWithAcount:success", Toast.LENGTH_LONG)
                        .show()
                    val add = Add()
                    add.createAccount(email, password, username , "users")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("mylog", "createUserWithAcount:failure", task.exception)
                    val exception = task.exception
                    Toast.makeText(
                        activity,
                        "createUserWithEmail:failure $exception",
                        Toast.LENGTH_LONG
                    ).show()


                }


            }
    }


}