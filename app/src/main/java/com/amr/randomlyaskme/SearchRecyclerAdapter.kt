package com.amr.randomlyaskme

import android.content.Intent
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SearchRecyclerAdapter: RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>(), Filterable{

    private var mData : MutableList<Question> = mutableListOf()
    private var mDataFilter : MutableList<Question> = mutableListOf()

    fun setDataSet(data: MutableList<Question>) {
        mData = data
        mDataFilter = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecyclerAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lq_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchRecyclerAdapter.ViewHolder, position: Int){

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

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filterResults = FilterResults()
                if(constraint == null || constraint.isEmpty()){
                    filterResults.count = mDataFilter.size
                    filterResults.values = mDataFilter
                } else{
                    var searchChar : String = constraint.toString().toLowerCase(Locale.ROOT)
                    var itemModal = mutableListOf<Question>()
                    for(question in mDataFilter){
                        if(question.question.toLowerCase(Locale.ROOT).contains(searchChar) || question.answer.toLowerCase(
                                Locale.ROOT
                            ).contains(searchChar)){
                            itemModal.add(question)
                        }
                    }
                    filterResults.count = itemModal.size
                    filterResults.values = itemModal
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mData = results?.values as MutableList<Question>
                notifyDataSetChanged()

            }

        }
    }

}