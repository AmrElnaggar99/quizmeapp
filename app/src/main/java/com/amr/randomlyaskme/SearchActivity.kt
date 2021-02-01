package com.amr.randomlyaskme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private var searchLayoutManager: RecyclerView.LayoutManager? = null
    private var searchAdapter: RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>? = null
    val db = dbHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchField : SearchView = findViewById(R.id.editTextSearch2)
        searchField.focusable = View.FOCUSABLE
        searchField.requestFocusFromTouch()
        searchField.isIconified = false

        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
        initSearch()


//        searchField.setOnQueryTextFocusChangeListener { v, hasFocus ->
//            if (!hasFocus){
//                finish()
//            }
//        }

    }
    fun initSearch(){
        val searchView: RecyclerView = findViewById(R.id.searchView)
        searchLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchView.layoutManager = searchLayoutManager
        searchAdapter = SearchRecyclerAdapter()

        (searchAdapter as SearchRecyclerAdapter).setDataSet(db.readAllQuestions())
        searchView.adapter = searchAdapter
        val searchField : SearchView = findViewById(R.id.editTextSearch2)
        searchField.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (searchAdapter as SearchRecyclerAdapter).filter.filter(newText)
                return true
            }

        })
    }
}