package com.amr.randomlyaskme

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amr.randomlyaskme.databinding.ActivityMainBinding
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>? = null
    private var reviseLayoutManager: RecyclerView.LayoutManager? = null
    private var reviseAdapter: RecyclerView.Adapter<ReviseRecyclerAdapter.ViewHolder>? = null
    private lateinit var binding: ActivityMainBinding
    lateinit var category_spinner : Spinner
    lateinit var twoWaysField : SwitchCompat
    lateinit var catAdapter : ArrayAdapter<Category>
    val db = dbHandler(this)
    var selectedCategory : Category = Category("0", "0", false)
    lateinit var questions: MutableList<Question>
    lateinit var categories : MutableList<Category>
    var catList = mutableListOf(Category("Select Category", "0", false))
    override fun onResume() {
        super.onResume()
        initRevise()
        initCats()
        initSpinner()
//        questions = db.readAllQuestions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        questions = db.readAllQuestions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        twoWaysField = findViewById(R.id.switch1)
        category_spinner = findViewById<Spinner>(R.id.categoryspinner)
        val myScrollView : View = findViewById(R.id.scrollView)
        val endspace : View = findViewById(R.id.endspace)
        val quinput : View = findViewById(R.id.question)
        val editTextSearch : SearchView = findViewById(R.id.editTextSearch)
        val textView10 : TextView = findViewById(R.id.textView10)
        val addbutton : Button = findViewById(R.id.addbutton)


        textView10.setOnClickListener{
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
        editTextSearch.setOnClickListener{
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        quinput.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) myScrollView.scrollTo(endspace.left, endspace.top)
        }
        val ansinput : View = findViewById(R.id.answer)
        ansinput.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) myScrollView.scrollTo(endspace.left, endspace.top)
        }


        val rv: RecyclerView= findViewById(R.id.recyclerView)
        initCats()
        initRevise()
        initSpinner()
    }

    fun goToSettings(v: View){
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun saveAndGoToCategory(v: View) {
        saveToDB()
        val intent = Intent(this@MainActivity, ListCategory::class.java)
        if(selectedCategory.id != 0) {
            intent.putExtra("catId", selectedCategory.id)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }

    fun saveToDB(){
        val questionField : EditText = findViewById(R.id.question)
        val question : String = questionField.text.toString()
        val answerField : EditText = findViewById(R.id.answer)
        val answer : String = answerField.text.toString()
        val catId : Int = selectedCategory.id
        if(question.isNotEmpty() && catId != 0){
            val twoWaysField : SwitchCompat = findViewById(R.id.switch1)
            val q = Question(question, answer, twoWaysField.isChecked, catId, false)
            val db = dbHandler(this@MainActivity)
            val res = db.insertQuestion(q)
            if (res != -1){
                Toast.makeText(
                        this@MainActivity,
                        "Question added successfully.",
                        Toast.LENGTH_SHORT
                ).show()
            } else{
                Toast.makeText(
                        this@MainActivity,
                        "There was a problem adding the question.",
                        Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                    this@MainActivity,
                    "You need to fill all fields first.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun initRevise(){
        val reviseView: RecyclerView= findViewById(R.id.reviseView)
        reviseLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reviseView.layoutManager = reviseLayoutManager
        reviseAdapter = ReviseRecyclerAdapter()

        (reviseAdapter as ReviseRecyclerAdapter).setDataSet(db.readReviseQuestions())
        reviseView.adapter = reviseAdapter
    }

    fun initCats(){
        val rv: RecyclerView= findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv.layoutManager = layoutManager
        adapter = CategoryRecyclerAdapter()
        categories = db.readCategories()
        (adapter as CategoryRecyclerAdapter).setDataSet(categories)
        rv.adapter = adapter
    }

    fun initSpinner(){
        catList.clear()
        catList.add(Category("Select Category", "0", false))
        categories = db.readCategories()
        categories.forEach{
            catList.add(it)
        }


        catAdapter = object: ArrayAdapter<Category>(
            this,
            R.layout.spinner_default,
            catList
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view:TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                // set item text bold
                view.setTypeface(view.typeface, Typeface.BOLD)

                // set selected item style
                if (position == category_spinner.selectedItemPosition && position !=0 ){
                    view.background = ColorDrawable(Color.parseColor("#1e90ff"))
                    view.setTextColor(Color.parseColor("#000000"))
                }

                // make hint item color gray
                if(position == 0){
                    view.setTextColor(Color.LTGRAY)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                // disable first item
                // first item is display as hint
                return position != 0
            }
        }

        category_spinner.adapter = catAdapter

        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = adapterView?.getItemAtPosition(position) as Category
                twoWaysField.isChecked = selectedCategory.twoways
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}