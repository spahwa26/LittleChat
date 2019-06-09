package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.pojo.User
import com.app.littlechat.utility.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_requests.view.*
import kotlinx.android.synthetic.main.layout_users.view.ivImage
import kotlinx.android.synthetic.main.layout_users.view.tvEmail
import kotlinx.android.synthetic.main.layout_users.view.tvName


class RequestsAdapter : RecyclerView.Adapter<RequestsAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<User>
    lateinit var context: Context
    lateinit var appInterface: AppInterface

    fun setData(context: Context, list: ArrayList<User>, appInterface: AppInterface) {
        this.list = list
        this.context = context
        this.appInterface = appInterface
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.layout_requests, p0, false)
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

        if (list.get(position).status.equals(Constants.SENT))
            setVisibility(holder, true)
        else
            setVisibility(holder, false)


        holder.btnAccept.setOnClickListener { appInterface.handleEvent(position, -1, null) }
        holder.btnReject.setOnClickListener { appInterface.handleEvent(position, -3, null) }
        holder.btnCancel.setOnClickListener { appInterface.handleEvent(position, -3, null) }
        holder.itemView.setOnClickListener { appInterface.handleEvent(position, 0, null) }
    }

    private fun setVisibility(holder: MyViewHolder, isSent: Boolean) {
        if (isSent) {
            holder.btnAccept.visibility = GONE
            holder.btnReject.visibility = GONE
            holder.btnCancel.visibility = VISIBLE
        } else {
            holder.btnAccept.visibility = VISIBLE
            holder.btnReject.visibility = VISIBLE
            holder.btnCancel.visibility = GONE
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tvName
        val tvEmail = view.tvEmail
        val ivImage = view.ivImage
        val btnAccept = view.btnAccept
        val btnReject = view.btnReject
        val btnCancel = view.btnCancel
    }


}
