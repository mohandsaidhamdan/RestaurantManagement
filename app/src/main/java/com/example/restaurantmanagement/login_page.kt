package com.example.restaurantmanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.restaurantmanagement.databinding.FragmentLoginPageBinding
import com.example.restaurantmanagement.databinding.FragmentRegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class login_page : Fragment() {

    private val mAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        binding.apply {


            btnLogin.setOnClickListener {
                val email = txtUserName.text.toString()
                val password = txtPassword.text.toString()
                if (isValidEmail(email)) {
                    login(email, password)
                } else {
                    Toast.makeText(context, "Please Enter Valid Email", Toast.LENGTH_SHORT).show()
                }


                val check = checkBox.isChecked
                if (check) {
                    requireContext().getSharedPreferences("MyShared", Context.MODE_PRIVATE).edit()
                        .putBoolean("check", true).apply()

                }
            }
            txtRgester.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, register_page()).addToBackStack(null).commit()
            }
        }
        return binding.root
    }

    private fun login(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {

                    val intent = Intent(this.context, MainActivity::class.java)

                    requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                        .putString("email", email).apply()
                    requireContext().getSharedPreferences("MyShared", Context.MODE_PRIVATE).edit()
                        .putBoolean("check", true).apply()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    val exception = task.exception
                    Log.e("abd", exception.toString())
                    val myToast =
                        Toast.makeText(activity, "Error Username Or Password", Toast.LENGTH_LONG)
                    myToast.show()
                }


            }

    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return email.matches(emailPattern.toRegex())
    }
}