package com.example.furrever

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DogsAdapter(private var dogs: List<Dog>?, private val context: Context) :
    RecyclerView.Adapter<DogsAdapter.DogsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogsViewHolder {
        return DogsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DogsViewHolder, position: Int) {
        val dog = dogs!![position]
        Glide.with(context)
            .load(dog.imgUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(250, 250)
            .dontAnimate()
            .dontTransform()
            .centerCrop()
            .into(holder.dogImage)
    }

    override fun getItemCount(): Int {
        return if (dogs == null) 0 else dogs!!.size
    }

    fun setDogs(dogsList: List<Dog>) {
        dogs = dogsList
        notifyDataSetChanged()
        Log.d("DOG", "Notified adapter about the update")
    }

    class DogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val dogImage: ImageView = itemView.findViewById(R.id.dogImage)

    }
}
