package com.example.realestate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestate.R
import com.example.realestate.models.RealEstate

class ListLocOrBuyAdapter constructor(
    private val context: Context,
    private val fragCallbacks: FragCallBacks
) : RecyclerView.Adapter<ListLocOrBuyAdapter.ViewwHolder>() {

    private var listEstate = mutableListOf<RealEstate>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewwHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_estate, parent, false)
        return ViewwHolder(view)
    }

    override fun onBindViewHolder(holder: ViewwHolder, position: Int) {
        val estate = listEstate[position]
        Log.d("test", "blop")
        Glide
            .with(context)
            .load(estate.listPic[0])
            .into(holder.imgEstate)

        holder.priceEstate.text = estate.getCurrency(context)
        holder.roomEstate.text = "${estate.nbRoom} pièces"
        holder.sizeEstate.text = "${estate.size} m²"
        holder.dateEntry.text = estate.getDate()
        holder.employee.text = estate.employee
        holder.available.text = when (estate.isAvailable) {
            true -> "Disponible"
            else -> "Non Disponible"
        }

        holder.itemView.setOnClickListener {
            fragCallbacks.onClickList(estate.dateEntry)
        }
    }

    override fun getItemCount(): Int {
        return listEstate.size
    }

    fun addList(estateList: MutableList<RealEstate>) {
        this.listEstate.clear()
        Log.d("test", "adapter : ${estateList.size}")
        this.listEstate.addAll(estateList)
        Log.d("test", "adapter listEstate : ${listEstate.size}")
        notifyDataSetChanged()
    }

    interface FragCallBacks {
        fun onClickList(id: Long)
    }


    class ViewwHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgEstate: ImageView = itemView.findViewById(R.id.imgEstate)
        val priceEstate: TextView = itemView.findViewById(R.id.priceEstate)
        val roomEstate: TextView = itemView.findViewById(R.id.roomEstate)
        val sizeEstate: TextView = itemView.findViewById(R.id.sizeEstate)
        val dateEntry: TextView = itemView.findViewById(R.id.dateEntryEstate)
        val employee: TextView = itemView.findViewById(R.id.employeeEstate)
        val available: TextView = itemView.findViewById(R.id.availableEstate)
    }
}