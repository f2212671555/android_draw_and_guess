package com.ntouandroid.drawandguess.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ntouandroid.drawandguess.R
import com.ntouandroid.drawandguess.model1.bean.MessageBean
import com.ntouandroid.drawandguess.model1.bean.UserBean

class UserListAdapter(val context: Context, private var userList: MutableList<UserBean>) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.users_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.userList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTv: TextView = itemView.findViewById(R.id.tv_user)

        fun bind(userBean: UserBean) {
            userNameTv.text = userBean.userName
        }
    }

    fun updateAll(userList: MutableList<UserBean>) {
        this.userList = userList
        this.notifyDataSetChanged()
    }

    fun add(messageBean: MessageBean) {
        var flag = true
        var index = 0
        userList.forEach { user ->
            if (user.userId == messageBean.userId) { // user existed!!
                flag = false
            }
            index++
        }
        if (flag) {
            this.userList.add(
                UserBean(
                    messageBean.roomId,
                    messageBean.userId,
                    messageBean.userName
                )
            )
            this.notifyItemInserted(index)
//            this.notifyDataSetChanged()
        }
    }


    fun remove(userId: String) {
        val iterator = userList.iterator()
        var position = 0
        while (iterator.hasNext()) {
            val user = iterator.next()
            if (user.userId == userId) {
                iterator.remove()
                this.notifyItemRemoved(position)
            }
            position++
        }
    }
}