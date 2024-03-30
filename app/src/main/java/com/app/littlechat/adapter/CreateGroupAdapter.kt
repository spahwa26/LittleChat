package com.app.littlechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.littlechat.R
import com.app.littlechat.databinding.LayoutCreateGroupUsersBinding
import com.app.littlechat.interfaces.AppInterface
import com.app.littlechat.model.User
import com.squareup.picasso.Picasso


class CreateGroupAdapter : RecyclerView.Adapter<CreateGroupAdapter.MyViewHolder>() {
    lateinit var list: ArrayList<User>
    lateinit var context: Context
    lateinit var appInterface: AppInterface

    internal var participantsList = java.util.ArrayList<User>()

    fun setData(context: Context, list: ArrayList<User>, appInterface: AppInterface) {
        this.list = list
        this.context = context
        this.appInterface = appInterface
    }

    fun setParticipantList(participantsList: ArrayList<User>) {
        this.participantsList = participantsList
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val binding =
            LayoutCreateGroupUsersBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        val holder = MyViewHolder(binding.root)
        holder.setBinding(binding)
        return holder
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    private fun isAlreadyParticipant(id: String): Boolean {
        var value = false
        for (user in participantsList) {
            if (user.id.equals(id)) {
                value = true
                break
            }
        }
        return value
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        private var binding: LayoutCreateGroupUsersBinding? = null

        fun setBinding(_binding: LayoutCreateGroupUsersBinding) {
            binding = _binding
        }

        fun onBind(position: Int) {
            binding?.apply {
                tvName.text = list.get(position).name
                tvEmail.text = list.get(position).email
                if (!list.get(position).image.isEmpty())
                    Picasso.get().load(list.get(position).image).placeholder(R.mipmap.ic_launcher)
                        .into(ivImage)

                cvAdd.visibility = VISIBLE


                if (!participantsList.isEmpty() && isAlreadyParticipant(list[position].id)) {
                    tvName.isEnabled = false
                    tvEmail.isEnabled = false
                    tvEmail.text = "Already added to group."
                    cvAdd.isChecked = true
                } else {
                    tvName.isEnabled = true
                    tvEmail.isEnabled = true
                    cvAdd.isChecked = list[position].isAdded
                    itemView.setOnClickListener {
                        if (list[position].isAdded) {
//                cvAdd.isChecked = false
//                list[position].isAdded = false
//                appInterface.handleEvent(position, -2, null)
                        } else {
                            cvAdd.isChecked = true
                            list[position].isAdded = true
                            appInterface.handleEvent(position, -1, null)
                        }
                    }
                }
            }
        }
    }


}
