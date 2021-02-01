package com.amr.randomlyaskme

import android.content.Intent
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView

class ReviseRecyclerAdapter: RecyclerView.Adapter<ReviseRecyclerAdapter.ViewHolder>(){

    private var mData : MutableList<Question> = mutableListOf()

    fun setDataSet(data: MutableList<Question>) {
        mData = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviseRecyclerAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lq_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReviseRecyclerAdapter.ViewHolder, position: Int){

        holder.question.text = mData[position].question
        holder.answer.text = mData[position].answer
        holder.id = mData[position].id.toString()

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var question : TextView
        val answer : TextView
        var id : String
        init {
            question = itemView.findViewById(R.id.questionr)
            answer = itemView.findViewById(R.id.answerr)
            id = "-1"
            itemView.setOnClickListener{
                val position: Int = adapterPosition
                goToEdit(itemView, mData[position].id)

            }
        }

    }
    fun goToEdit(v: View, id : Int) {
        val intent = Intent(v.context, EditQuestionActivity::class.java)
        intent.putExtra("queId", id)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        v.context.startActivity(intent)
    }
    override fun getItemCount(): Int {
        return mData.size
    }

}