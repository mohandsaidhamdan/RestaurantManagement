package com.example.restaurantmanagement.ui.menu

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantmanagement.*
import com.example.restaurantmanagement.databinding.FragmentMenuBinding
import com.example.restaurantmanagement.model.Meal
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MenuFragment : Fragment() {
    lateinit var db: FirebaseFirestore
    var Adapter: FirestoreRecyclerAdapter<Meal, meal_item>? = null
    val storage = FirebaseStorage.getInstance()
    var typeAccount : String = ""
    var MEAL_ID = ""

private var _binding: FragmentMenuBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {


    _binding = FragmentMenuBinding.inflate(inflater, container, false)
    val root: View = binding.root

//        get email form SharedPreferences to checed type acount
      val emailUser = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", "").toString()
      getUser(emailUser)

      //        Start Adapter
      db = Firebase.firestore
      val recycler = binding.rvMeals

      val query = db.collection("meals").orderBy("rate", Query.Direction.DESCENDING)

      val  email = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE).getString("email" , "").toString()


//      ======== Start cheked acount type to permations ===
      if (email == "admin@gmail.com") {
          binding.btnAdd.visibility = View.VISIBLE
      }
      else {
         binding.btnAdd.visibility = View.GONE
      }
//      ======== End cheked acount type to permations ===



//        Build Recycler View
      val option =
          FirestoreRecyclerOptions.Builder<Meal>().setQuery(query, Meal::class.java)
              .build()

//        Adapter Take option
      Adapter = object : FirestoreRecyclerAdapter<Meal, meal_item>(option) {
          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): meal_item {
              var view = LayoutInflater.from(context)
                  .inflate(R.layout.show_meals, parent, false)
              return meal_item(view)
          }

          override fun onBindViewHolder(holder: meal_item, position: Int, model: Meal) {
              val name = model.name
              val description = model.description
          val resturantName = model.resturantName
              val rate = model.rate
              val image = model.image
              val price = model.price

              holder.name.text = name
              holder.price.text = price.toString()
              holder.description.text = description
             holder.resturantName.text = resturantName
              holder.rate.rating = rate

              //                Delete
              holder.image.setOnLongClickListener { _ ->
                  Toast.makeText(context, typeAccount, Toast.LENGTH_SHORT).show()
                  if (typeAccount == "admin") {
                      val dilalog = AlertDialog.Builder(context)
                      dilalog.setTitle("Delete Account")
                      dilalog.setMessage("You Are Sure Delete Account ??")
                      dilalog.setPositiveButton("Delete") { dialog, which ->
                          // Do something when the positive button is clicked
                          getidDelete(model.name)
                          Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
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

              holder.edit.setOnClickListener {
                  val i = Intent(context, EditMeal::class.java)
                  i.putExtra("name", model.name)
                  startActivity(i)

              }

              holder.order.setOnClickListener{
                  val i = Intent(context, OrderDetailes::class.java)
                  i.putExtra("name", model.name)
                  getIDMeal(model.name)
                  i.putExtra("id", MEAL_ID)
                  startActivity(i)
              }
              //      ======== Start cheked acount type to permations ===
              val  email = requireActivity().getSharedPreferences("user" , Context.MODE_PRIVATE).getString("email" , "").toString()
              if (email == "admin@gmail.com") {
                 holder.edit.visibility = View.VISIBLE
              }
              else {
                 holder.edit.visibility = View.GONE
              }
              //      ======== End cheked acount type to permations ===


              DwnloadImage(image, holder.image)

          }


      }

      recycler.layoutManager = LinearLayoutManager(context)
      recycler.adapter = Adapter

      //        End Adapter


      binding.btnAdd.setOnClickListener {
          startActivity(Intent(context, add_meal::class.java))
//            requireActivity().finish()
      }


      return root

  }


    class meal_item(view: View) : RecyclerView.ViewHolder(view) {
        val image = itemView.findViewById<ImageView>(R.id.image_meal)
        val name = itemView.findViewById<TextView>(R.id.txtNameMeal)
        val price = itemView.findViewById<TextView>(R.id.txtPrice)

        val description = itemView.findViewById<TextView>(R.id.txtDescription)
        val rate = itemView.findViewById<RatingBar>(R.id.txtRateMeal)
        val oreder = itemView.findViewById<Button>(R.id.btnOrder)
        val resturantName = itemView.findViewById<TextView>(R.id.txtResturant)
        val edit = itemView.findViewById<ImageView>(R.id.editMeal)
        val order = itemView.findViewById<Button>(R.id.btnOrder)


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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    ====get type acount=============
    private fun getUser(emails : String) {
        var db = Firebase.firestore

        db.collection("users").get().addOnSuccessListener { user ->
            for (user in user) {

                val email = user.get("email")

                if (email!!.equals(emails))
                {
                    typeAccount = user.get("type_account") .toString()
                }

            }
        }

    }


    // ==============   Return ID Member To Delete ===========
    fun getidDelete(names : String ){
        db.collection("meals")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val name = document.get("name")

                    if (name== names ){
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
    fun delete(id : String){
        db.collection("meals").document(id).delete()

    }



//    Get ID Meal
fun getIDMeal(names : String ){
    db.collection("meals")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {

                val name = document.get("name")

                if (name== names ){
                    MEAL_ID = document.id

                }

            }
        }
        .addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
}
}