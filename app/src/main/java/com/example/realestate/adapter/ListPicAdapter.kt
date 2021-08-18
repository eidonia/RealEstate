package com.example.realestate.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestate.R

class
ListPicAdapter constructor(private val context: Context, private val updatePic: UpdatePic) :
    RecyclerView.Adapter<ListPicAdapter.ViewHolder>() {

    private var listUri = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pic_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = Uri.parse(listUri[position])
        Glide.with(context)
            .load(uri)
            .into(holder.image)

        holder.itemView.setOnLongClickListener {
            updatePic.deletePic(listUri[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return listUri.size
    }

    fun addToList(picUri: String) {
        this.listUri.add(picUri)
        notifyDataSetChanged()
    }

    fun addList(listUri: MutableList<String>) {
        this.listUri.clear()
        this.listUri.addAll(listUri)
        notifyDataSetChanged()
    }

    interface UpdatePic {
        fun deletePic(picUri: String)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.picEsate)

    }
}