package com.example.realestate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R

class PoiAdapter : RecyclerView.Adapter<PoiAdapter.ViewHolder>() {

    private val listPoiOrCriteria = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_poi_criteria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "- ${listPoiOrCriteria[position]}"
    }

    override fun getItemCount(): Int {
        return listPoiOrCriteria.size
    }

    fun addList(list: MutableList<String>) {
        this.listPoiOrCriteria.addAll(list)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textPoiOrCriteria)

    }
}