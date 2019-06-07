package com.app.littlechat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.littlechat.R
import com.app.littlechat.pojo.User
import kotlinx.android.synthetic.main.layout_users.view.*


class UsersAdapter : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<User>
    lateinit var context: Context
    var total = 0.0

    fun setData(context: Context, list: ArrayList<User>) {
        this.list = list
        this.context = context
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_users, p0, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text=list.get(position).name
        holder.tvEmail.text=list.get(position).email
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tvName
        val tvEmail = view.tvEmail
    }


}
