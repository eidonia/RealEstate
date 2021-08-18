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
) : RecyclerView.Adapter<ListLocOrBuyAdapter.ViewHolder>() {

    private var listEstate = mutableListOf<RealEstate>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_estate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val estate = listEstate[position]
        Glide
            .with(context)
            .load(estate.listPic[0])
            .into(holder.imgEstate)

        holder.priceEstate.text = estate.getCurrency(context)
        holder.roomEstate.text = context.getString(R.string.nb_room, estate.nbRoom)
        holder.sizeEstate.text = context.getString(R.string.size_estate, estate.size)
        holder.dateEntry.text = estate.getDate()
        holder.employee.text = estate.employee
        holder.typeEstate.text = estate.formatType()
        holder.city.text = estate.city
        holder.available.text = when (estate.isAvailable) {
            true -> context.getString(R.string.estate_dispo)
            else -> context.getString(R.string.estate_not_dispo)
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
        this.listEstate.addAll(estateList)
        notifyDataSetChanged()
    }

    interface FragCallBacks {
        fun onClickList(id: Long)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgEstate: ImageView = itemView.findViewById(R.id.imgEstate)
        val priceEstate: TextView = itemView.findViewById(R.id.priceEstate)
        val roomEstate: TextView = itemView.findViewById(R.id.roomEstate)
        val sizeEstate: TextView = itemView.findViewById(R.id.sizeEstate)
        val dateEntry: TextView = itemView.findViewById(R.id.dateEntryEstate)
        val employee: TextView = itemView.findViewById(R.id.employeeEstate)
        val available: TextView = itemView.findViewById(R.id.availableEstate)
        val city: TextView = itemView.findViewById(R.id.cityEstate)
        val typeEstate: TextView = itemView.findViewById(R.id.typeEstate)
    }
}