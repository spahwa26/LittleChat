package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_create_group_users.view.*


class CreateGroupAdapter : RecyclerView.Adapter<CreateGroupAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<User>
    lateinit var context: Context
    lateinit var appInterface: AppInterface

    fun setData(context: Context, list: ArrayList<User>, appInterface: AppInterface) {
        this.list = list
        this.context = context
        this.appInterface = appInterface
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_create_group_users, p0, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = list.get(position).name
        holder.tvEmail.text = list.get(position).email
        if (!list.get(position).image.isEmpty())
            Picasso.get().load(list.get(position).image).placeholder(R.mipmap.ic_launcher).into(holder.ivImage)

        holder.cvAdd.visibility = VISIBLE

        holder.cvAdd.isChecked = list[position].isAdded


        holder.itemView.setOnClickListener {
            if (list[position].isAdded) {
//                holder.cvAdd.isChecked = false
//                list[position].isAdded = false
//                appInterface.handleEvent(position, -2, null)
            } else {
                holder.cvAdd.isChecked = true
                list[position].isAdded = true
                appInterface.handleEvent(position, -1, null)
            }
        }


    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tvName
        val tvEmail = view.tvEmail
        val ivImage = view.ivImage
        val cvAdd = view.cvAdd
    }


}
