package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.GroupDetails
import com.app.littlechat.pojo.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_groups.view.*


class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<GroupDetails>
    lateinit var context: Context
    lateinit var appInterface: AppInterface

    fun setData(context: Context, list: ArrayList<GroupDetails>, appInterface: AppInterface) {
        this.list = list
        this.context = context
        this.appInterface = appInterface
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_groups, p0, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = list.get(position).name
        if (!list.get(position).image.isEmpty())
            Picasso.get().load(list.get(position).image).placeholder(R.mipmap.ic_launcher).into(holder.ivImage)

        holder.itemView.setOnClickListener {
            appInterface.handleEvent(position, 0, null)
        }

    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tvName
        val ivImage = view.ivImage
    }


}
