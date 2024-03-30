package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.databinding.LayoutRequestsBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.model.User
import com.app.littlechat.utility.Constants
import com.squareup.picasso.Picasso


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
        return MyViewHolder(
            LayoutRequestsBinding.inflate(
                LayoutInflater.from(p0.context),
                p0,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    private fun setVisibility(binding: LayoutRequestsBinding, isSent: Boolean) {
        binding.run {
            if (isSent) {
                btnAccept.visibility = GONE
                btnReject.visibility = GONE
                btnCancel.visibility = VISIBLE
            } else {
                btnAccept.visibility = VISIBLE
                btnReject.visibility = VISIBLE
                btnCancel.visibility = GONE
            }
        }
    }

    inner class MyViewHolder(val binding: LayoutRequestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.run {
                tvName.text = list.get(position).name
                tvEmail.text = list.get(position).email
                if (!list.get(position).image.isEmpty())
                    Picasso.get().load(list.get(position).image).placeholder(R.mipmap.ic_launcher)
                        .into(ivImage)

                if (list.get(position).status.equals(Constants.SENT))
                    setVisibility(binding, true)
                else
                    setVisibility(binding, false)


                btnAccept.setOnClickListener { appInterface.handleEvent(position, -1, null) }
                btnReject.setOnClickListener { appInterface.handleEvent(position, -3, null) }
                btnCancel.setOnClickListener { appInterface.handleEvent(position, -3, null) }
                itemView.setOnClickListener { appInterface.handleEvent(position, 0, null) }
            }
        }
    }


}
