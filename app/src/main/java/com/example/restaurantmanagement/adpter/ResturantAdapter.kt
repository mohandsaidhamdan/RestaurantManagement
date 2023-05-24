package com.example.restaurantmanagement.adpter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantmanagement.R
import com.example.restaurantmanagement.databinding.ShowResturantsBinding
import com.example.restaurantmanagement.model.Resturant

class ResturantAdapter(var activity: Activity, var data: ArrayList<Resturant>) :

RecyclerView.Adapter<ResturantAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: ShowResturantsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ShowResturantsBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val d = data[position]

        holder.binding.txtNameResturant.text = d.name
        holder.binding.txtDescription.text = d.description
        holder.binding.txtRateMeal.rating = d.rate.toString().toFloat()


        holder.binding.root.setOnLongClickListener { _ ->
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setIcon(R.drawable.ic_delete)
            alertDialog.setTitle("Delete Meal")
            alertDialog.setMessage("Are You Sure To Delete Information Meal")
            alertDialog.setPositiveButton("Yes") { _, _ ->


            }
            alertDialog.setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
            alertDialog.create().show()
            true

        }


        holder.binding.edit.setOnClickListener {

        }



    }





    override fun getItemCount(): Int {
        return data.size
    }




}