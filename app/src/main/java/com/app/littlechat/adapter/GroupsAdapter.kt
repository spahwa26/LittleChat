package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.databinding.LayoutGroupsBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.model.GroupDetails
import com.squareup.picasso.Picasso


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
        return MyViewHolder(LayoutGroupsBinding.inflate(LayoutInflater.from(p0.context), p0, false))
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class MyViewHolder(val binding: LayoutGroupsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.run {
                tvName.text = list.get(position).name
                if (!list.get(position).image.isEmpty())
                    Picasso.get().load(list.get(position).image).placeholder(R.mipmap.ic_launcher)
                        .into(ivImage)

                itemView.setOnClickListener {
                    appInterface.handleEvent(position, 0, null)
                }
            }
        }
    }


}
