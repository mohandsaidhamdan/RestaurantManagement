package com.example.restaurantmanagement

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantmanagement.databinding.ActivityOrderListBinding
import com.example.restaurantmanagement.model.Meal
import com.example.restaurantmanagement.model.Order
import com.example.restaurantmanagement.ui.menu.MenuFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class OrderList : AppCompatActivity() {
    lateinit var binding : ActivityOrderListBinding
    var storage = FirebaseStorage.getInstance()
    lateinit var db : FirebaseFirestore
    var price = 0.0
    var names = ""
    lateinit var recyclerView : RecyclerView
    var Adapter: FirestoreRecyclerAdapter<Order, item_order>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var total = intent.getStringExtra("alltotal")
        var name = intent.getStringExtra("name")
        val email = getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", "").toString()

         recyclerView =binding.rvOrderListItem
        db = Firebase.firestore

        val query = db.collection("orders")

//        Build Recycler View
        val option =
            FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java)
                .build()

        ListAdapter(option)


        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                searchList(p0.toString())
                return true
            }

        })

        binding.txtTotal.text = total.toString()

    }


    class item_order(view: View) : RecyclerView.ViewHolder(view) {
        val image = itemView.findViewById<ImageView>(R.id.imageOrder)
        val name = itemView.findViewById<TextView>(R.id.txtNameOrder)
        val price = itemView.findViewById<TextView>(R.id.txtPriceOrder)
        val amount = itemView.findViewById<TextView>(R.id.txtAmountOrder)

    }
fun ListAdapter(option :  FirestoreRecyclerOptions<Order>){

//        Adapter Take option
    Adapter = object : FirestoreRecyclerAdapter<Order, item_order>(option) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): item_order {
            var view = LayoutInflater.from(this@OrderList)
                .inflate(R.layout.orderlist_item, parent, false)
            return item_order(view)
        }

        override fun onBindViewHolder(holder:item_order, position: Int, model: Order) {
            val name = model.name
            val count = model.count
            val totalPrice = model.totalPrice
            val emails = model.email
            val image2 = model.imageView_url
            val price = model.price

            holder.name.text = name
            holder.amount.text = count
            holder.price.text = price.toString()
            holder.name.text = name


            //                Delete
            holder.image.setOnLongClickListener { _ ->



                true
            }



            DwnloadImage(image2, holder.image)

        }


    }

    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = Adapter





}
    fun searchList(text: String) {
        val query = db.collection("orders").orderBy("name").startAt(text).endAt(text + "\ufaff")
        val option =
            FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        ListAdapter(option)
        Adapter!!.startListening()
        recyclerView.adapter = Adapter
        Adapter!!.notifyDataSetChanged()
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


    override fun onBackPressed() {
        super.onBackPressed()
        val name = intent.getStringExtra("name")
        val i= Intent(this , OrderDetailes::class.java)
        i.putExtra("name",name)
        startActivity(i)
    }

}