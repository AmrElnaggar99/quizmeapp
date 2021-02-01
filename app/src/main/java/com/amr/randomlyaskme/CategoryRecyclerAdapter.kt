package com.amr.randomlyaskme

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CategoryRecyclerAdapter: RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>(){

    private var mCategories : MutableList<Category> = (mutableListOf(Category("Add\nNew Category", "#e74c3c", false)))

    fun setDataSet(data: MutableList<Category>) {
        mCategories = (mCategories + data) as MutableList<Category>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRecyclerAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.category_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategoryRecyclerAdapter.ViewHolder, position: Int){
        if (position == 0){
            holder.card.setContentPadding(60,60,60,60)
            holder.itemTitle.textSize = 22.0F
            holder.delbtn.visibility = View.INVISIBLE
        }
        holder.itemTitle.text = mCategories[position].title
        holder.card.setCardBackgroundColor(Color.parseColor(mCategories[position].color))
        holder.id = mCategories[position].id.toString()

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val db = dbHandler(itemView.context)
        var itemTitle : TextView
        val card : CardView
        var id : String
        val delbtn : ImageView


        init {
            itemTitle = itemView.findViewById(R.id.category_title)
            card = itemView.findViewById(R.id.card_view)
            id = "-1"
            delbtn = itemView.findViewById(R.id.delete_cat)

            delbtn.setOnClickListener{
                val position: Int = adapterPosition
                val catId = mCategories[position].id
                val builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Are you sure you want to delete this category?")
                builder.setMessage("All the questions in this category will be deleted too.")
                builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int -> executeDeleteCat(itemView,catId) }
                builder.setNegativeButton("Cancel") {dialogInterface: DialogInterface, i: Int -> }
                builder.show()
            }

            itemView.setOnClickListener{
                val position: Int = adapterPosition
                if(position == 0)
                    goToAddCategory(itemView)
                else
                    goToListCategory(itemView, mCategories[position].id)
//                Toast.makeText(itemView.context,"you clicked on ${mCategories[position].title}", Toast.LENGTH_LONG).show()

            }
        }

        private fun executeDeleteCat(v : View, id : Int) {
            val position: Int = adapterPosition
            db.deleteCat(id)
            mCategories.removeAt(position)
            notifyItemRemoved(position)
            val intent = Intent(v.context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            v.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mCategories.size
    }
    fun goToListCategory(v: View, catId : Int){
        val intent = Intent(v.context, ListCategory::class.java)
        intent.putExtra("catId", catId)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        v.context.startActivity(intent)
    }

    fun goToAddCategory(v: View){
        val intent = Intent(v.context, AddCategoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        v.context.startActivity(intent)
    }

}