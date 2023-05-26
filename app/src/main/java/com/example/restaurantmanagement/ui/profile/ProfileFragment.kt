package com.example.restaurantmanagement.ui.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanagement.LoginActivity
import com.example.restaurantmanagement.R
import com.example.restaurantmanagement.databinding.FragmentProfileBinding
import com.example.restaurantmanagement.login_page
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.internal.operators.flowable.FlowableOnErrorReturn

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        db = Firebase.firestore

 //        =============start logout================
        binding.logout.setOnClickListener {
            startActivity(Intent( context  , LoginActivity::class.java))
            requireActivity().finish()
            requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                .putString("email", "").apply()
            requireContext().getSharedPreferences("MyShared", Context.MODE_PRIVATE).edit()
                .putBoolean("check", false).apply()
        }
//        =============End logout================
        binding.btnEdit.setOnClickListener {
            showbottomDilaog()
        }
        binding.btnEdit.setOnClickListener {
            showbottomDilaog()
        }

        val email = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            .getString("email", "").toString()
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener {
            for (data in it) {
                val oldName = data.getString("name")
                val oldbirthday = data.getString("birthday")
                val oldaddress = data.getString("address")
                val oldphone = data.getString("phone")
                val oldemail = data.getString("email")

                binding.profileUsername.text = oldName.toString()
                binding.profileName.text = oldName.toString()
                binding.profileBirthday.text = oldbirthday.toString()
                binding.profileAddress.text = oldaddress.toString()
                binding.profilePhone.text = oldphone.toString()
                binding.profileEmail.text = oldemail.toString()
            }
        }


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("CommitTransaction")
    private fun showbottomDilaog() {
        val dialog = activity?.let { Dialog(it) }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_edit)

        val btnUpdate: Button = dialog.findViewById(R.id.edit_button)

        val name: EditText = dialog.findViewById(R.id.edit_name)

        val phone: EditText = dialog.findViewById<EditText?>(R.id.edit_phone)
        val birthday: EditText = dialog.findViewById(R.id.edit_birthday)
        val address: EditText = dialog.findViewById(R.id.edit_address)
        val email = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            .getString("email", "").toString()
        btnUpdate.setOnClickListener {
            if (name.text.isNotEmpty() && birthday.text.isNotEmpty() && address.text.isNotEmpty() && phone.text.isNotEmpty()) {
                getID(
                    email,
                    name.text.toString(),
                    birthday.text.toString(),
                    address.text.toString(),
                    phone.text.toString()
                )
                dialog.dismiss()
                activity?.let { it1 -> refreshFragment(it1) }

            } else {
                Toast.makeText(context, "All Fields Is Required", Toast.LENGTH_SHORT).show()
            }
        }



        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations =
            R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun updateProfile(
        oldId: String,
        name: String,
        birthday: String,
        address: String,
        phone: String
    ) {
        val informationProfile = hashMapOf<String, Any>(
            "name" to name,
            "birthday" to birthday,
            "address" to address,
            "phone" to phone
        )

        db.collection("users").document(oldId).update(informationProfile).addOnSuccessListener {
            Toast.makeText(context, "Update Profile Is Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Update Profile Failed", Toast.LENGTH_SHORT).show()
        }

    }


    private fun getID(
        email: String,
        name: String,
        birthday: String,
        address: String,
        phone: String
    ) {
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener {
            for (data in it) {
                val id = data.id
                val oldName = data.getString("name")
                val oldbirthday = data.getString("birthday")
                val oldaddress = data.getString("address")
                val oldphone = data.getString("phone")

                binding.profileUsername.text = oldName.toString()
                binding.profileBirthday.text = oldbirthday.toString()
                binding.profileAddress.text = oldaddress.toString()
                binding.profilePhone.text = oldphone.toString()

                updateProfile(id, name, birthday, address, phone)
            }
        }.addOnFailureListener { e -> Log.e("abd", e.message.toString()) }
    }

    private fun getProfileData(email: String) {

    }

    private fun refreshFragment(context: Context) {
        context?.let {
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.container)
                currentFragment?.let {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.detach(it)
                    fragmentTransaction.attach(it)
                    fragmentTransaction.commit()
                }
            }


        }

    }
}