package com.example.restaurantmanagement.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantmanagement.R
import com.example.restaurantmanagement.databinding.FragmentHomeBinding
import com.example.restaurantmanagement.model.Meal
import com.example.restaurantmanagement.model.Resturant
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    var Adapter: FirestoreRecyclerAdapter<Resturant, home>? = null
    var AdapterMeal: FirestoreRecyclerAdapter<Meal, homeMeal>? = null

    val storage = FirebaseStorage.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recycler = binding.recyclerView
        val recyclermeal = binding.rvMeals
        db = Firebase.firestore

        val query = db.collection("resturants")
        val queryMeals = db.collection("meals")

//        Build Recycler View
        val option =
            FirestoreRecyclerOptions.Builder<Resturant>().setQuery(query, Resturant::class.java)
                .build()

//        Adapter Take option
        Adapter = object : FirestoreRecyclerAdapter<Resturant, home>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): home {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.home_resturant_item, parent, false)
                return home(view)
            }


            override fun onBindViewHolder(holder: home, position: Int, model: Resturant) {
                val name = model.name
                val image = model.image

                holder.name.text = name




                DwnloadImage(image, holder.image)

            }

        }

        //        Build Recycler View
        val optionMeal =
            FirestoreRecyclerOptions.Builder<Meal>().setQuery(queryMeals, Meal::class.java)
                .build()

//        Adapter Take option
        AdapterMeal = object : FirestoreRecyclerAdapter<Meal, homeMeal>(optionMeal) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): homeMeal {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.home_resturant_item, parent, false)
                return homeMeal(view)
            }


            override fun onBindViewHolder(holder: homeMeal, position: Int, model: Meal) {
                val name = model.name
                val image = model.image

                holder.name.text = name




                DwnloadImage(image, holder.image)

            }

        }

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        recycler.adapter = Adapter


        recyclermeal.layoutManager = LinearLayoutManager(context)
        recyclermeal.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        recyclermeal.adapter = AdapterMeal
        //        End Adapter


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Adapter!!.startListening()
        AdapterMeal!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        Adapter!!.stopListening()
        AdapterMeal!!.stopListening()
    }

    class home(view: View) : RecyclerView.ViewHolder(view) {
        val image = itemView.findViewById<ImageView>(R.id.resturant_img2)
        val name = itemView.findViewById<TextView>(R.id.txtImage)


    }


    class homeMeal(view: View) : RecyclerView.ViewHolder(view) {
        val image = itemView.findViewById<ImageView>(R.id.resturant_img2)
        val name = itemView.findViewById<TextView>(R.id.txtImage)


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
}