package com.amr.randomlyaskme

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import java.util.*


class ListCategory : AppCompatActivity() {
    private var catId : Int = 0
    private val db = dbHandler(this)
    private lateinit var data : MutableList<Question>
    private lateinit var thisCat : Category
    private lateinit var lqView: RecyclerView
    private var lqLayoutManager: RecyclerView.LayoutManager? = null
    private var lqAdapter: RecyclerView.Adapter<CategoryListRecyclerAdapter.ViewHolder>? = null
    private lateinit var deletedItem : Question
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_category2)
        setSupportActionBar(findViewById(R.id.toolbar))
        catId = intent.getIntExtra("catId", 0)
        data = db.readCategoryQuestions(catId)
        thisCat = db.readSingleCategory(catId)

        val app_bar : View = findViewById(R.id.app_bar)
        val toolbarforbg : View = findViewById(R.id.toolbar)
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor(thisCat.color), Color.parseColor("#202125")))
        gd.cornerRadius = 0f

        app_bar.background = gd

        val title = thisCat.title
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = thisCat.title

        val addbtn = findViewById<Button>(R.id.addq)
        val toolbar = supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)

        lqView = findViewById(R.id.questionslistRv)
        lqLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lqView.layoutManager = lqLayoutManager
        lqAdapter = CategoryListRecyclerAdapter()
        (lqAdapter as CategoryListRecyclerAdapter).setDataSet(data)
        lqView.adapter = lqAdapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(lqView)
    }


    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            var position = viewHolder.adapterPosition
            deletedItem = data[position]
            val res = db.deleteQue(data[position].id)
            if (res != -1){
                data.removeAt(position)
                lqAdapter?.notifyItemRemoved(position)
                val deletedq : String? = deletedItem.question
                Snackbar.make(lqView, "$deletedq is deleted.", Snackbar.LENGTH_LONG).setAction(
                        "Undo",
                        View.OnClickListener {
                            val pos = db.insertQuestion(deletedItem)
                            data.add(data.size, db.readSingleQuestion(pos))
                            lqAdapter?.notifyItemInserted(data.size)
                        }).show()
            }
        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_list_category, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.addq -> {
            val intent = Intent(this@ListCategory, AddQuestionActivity::class.java)
            intent.putExtra("selectedCat", thisCat.id)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



}