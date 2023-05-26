package com.example.restaurantmanagement.ui.resturant

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantmanagement.EditResturant
import com.example.restaurantmanagement.R
import com.example.restaurantmanagement.add_resturant2
import com.example.restaurantmanagement.databinding.FragmentResturantBinding
import com.example.restaurantmanagement.model.Order
import com.example.restaurantmanagement.model.Resturant
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale


class ResturantFragment : Fragment() {
    lateinit var db: FirebaseFirestore
    var Adapter: FirestoreRecyclerAdapter<Resturant, resturant_item>? = null
    lateinit var binding: FragmentResturantBinding
    val storage = FirebaseStorage.getInstance()
    var typeAccount: String = ""
    private lateinit var dataList: ArrayList<Resturant>
lateinit var recycler : RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResturantBinding.inflate(inflater, container, false)


//        get email form SharedPreferences to checed type acount
        val emailUser = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
            .getString("email", "").toString()
        getUser(emailUser)


        val  email = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE).getString("email" , "").toString()

        if (email == "admin@gmail.com") {

           binding.btnAdd.visibility = View.VISIBLE
        }
        else {
            binding.btnAdd.visibility = View.GONE
        }
//        ==================================Start Adapter==================================
        db = Firebase.firestore
         recycler = binding.recyclerView2

        val query = db.collection("resturants").orderBy("rate", Query.Direction.DESCENDING)

//        Build Recycler View
        val option =
            FirestoreRecyclerOptions.Builder<Resturant>().setQuery(query, Resturant::class.java)
                .build()
        ListAdapter(option)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = Adapter


        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                searchList(p0.toString())
                return true
            }

        })

            //     ==================================End Adapter==================================


//
//        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//            override fun onQueryTextChange(newText: String): Boolean {
//                Adapter.searchDataList(searchList(newText))
//                return true
//            }
//        })


        binding.btnAdd.setOnClickListener {
            startActivity(Intent(context, add_resturant2::class.java))
//            requireActivity().finish()
        }



        return binding.root

    }


//
//fun searchList(text: String): List<Resturant> {
//    val searchList = ArrayList<Resturant>()
//    for (dataClass in dataList) {
//        if (dataClass.name?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
//            searchList.add(dataClass)
//        }
//    }
//    return searchList
//}

    class resturant_item(view: View) : RecyclerView.ViewHolder(view) {
        val image = itemView.findViewById<ImageView>(R.id.resturant_img)
        val name = itemView.findViewById<TextView>(R.id.txtNameResturant)
        val location = itemView.findViewById<TextView>(R.id.location)
        val description = itemView.findViewById<TextView>(R.id.txtDescription)
        val rate = itemView.findViewById<RatingBar>(R.id.txtRateMeal)
        val edit = itemView.findViewById<Button>(R.id.edit)
        val root = itemView.rootView


    }



    override fun onStart() {
        super.onStart()
        Adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        Adapter!!.stopListening()
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

    fun searchList(text: String) {
        val query = db.collection("resturants").orderBy("name").startAt(text).endAt(text + "\ufaff")
        val option =
            FirestoreRecyclerOptions.Builder<Resturant>().setQuery(query, Resturant::class.java).build()
        ListAdapter(option)
        Adapter!!.startListening()
        recycler.adapter = Adapter
        Adapter!!.notifyDataSetChanged()
    }
    //    ====get type acount=============
    private fun getUser(emails: String) {
        var db = Firebase.firestore

        db.collection("users").get().addOnSuccessListener { user ->
            for (user in user) {

                val email = user.get("email")

                if (email!!.equals(emails)) {
                    typeAccount = user.get("type_account").toString()
                }

            }
        }

    }

    // ==============   Return ID Member To Delete ===========
    fun getidDelete(names: String) {
        db.collection("resturants")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val name = document.get("name")

                    if (name == names) {
                        val id = document.id
                        delete(id)
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    //    Delete Function
    fun delete(id: String) {
        db.collection("resturants").document(id).delete()

    }

    fun ListAdapter(option: FirestoreRecyclerOptions<Resturant>){


//        Adapter Take option

        Adapter = object : FirestoreRecyclerAdapter<Resturant, resturant_item>(option)  {
            var dataList: List<Resturant> = listOf() // قائمة البحث الجديدة



            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): resturant_item {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.show_resturants, parent, false)
                return resturant_item(view)
            }

            override fun onBindViewHolder(holder: resturant_item, position: Int, model: Resturant) {
                val name = model.name
                val description = model.description
                val location = model.loation
                val rate = model.rate
                val image = model.image

                holder.name.text = name
                holder.description.text = description
                holder.location.text = location
                holder.rate.rating = rate

//                Delete
                holder.root.setOnLongClickListener { _ ->
                    if (typeAccount == "admin") {
                        val dilalog = AlertDialog.Builder(context)
                        dilalog.setTitle("Delete Account")
                        dilalog.setMessage("You Are Sure Delete Account ??")
                        dilalog.setPositiveButton("Delete") { dialog, which ->
                            // Do something when the positive button is clicked
                            getidDelete(model.name)
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()


                        }
                        dilalog.setNegativeButton("Cancel") { dialog, which ->
                            // Do something when the negative button is clicked
                            dialog.dismiss()

                        }
                        dilalog.show()
                    }

                    true
                }

                val  email = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE).getString("email" , "").toString()

                if (email == "admin@gmail.com") {

                    holder.edit.visibility = View.VISIBLE
                }
                else {
                    holder.edit.visibility = View.GONE
                }
                holder.edit.setOnClickListener {


                    val i = Intent(context, EditResturant::class.java)
                    i.putExtra("name", model.name)
                    startActivity(i)

                }

                DwnloadImage(image, holder.image)

            }


        }

    }
}