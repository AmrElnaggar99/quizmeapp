package com.amr.randomlyaskme

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class AddQuestionActivity : AppCompatActivity() {
    val db = dbHandler(this)
    var selectedCategory : Category = Category("0","0",false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)
        setSupportActionBar(findViewById(R.id.toolbar))
        val questionField : EditText = findViewById(R.id.question)
        questionField.requestFocus()
        val s : Int = intent.getIntExtra("selectedCat", 0)
        val twoWaysField : SwitchCompat = findViewById(R.id.switch1)

        val toolbar = supportActionBar
        toolbar!!.title = "Add a question"
        toolbar.setDisplayHomeAsUpEnabled(true)

        val catList = mutableListOf(Category("Select Category", "0",false))
        val categories : MutableList<Category> = db.readCategories()

        categories.forEach{
            if (it.id == s){
                selectedCategory = it
            }
            catList.add(it)
        }
        twoWaysField.isChecked = selectedCategory.twoways
        val category_spinner = findViewById<Spinner>(R.id.categoryspinner)
        val catAdapter : ArrayAdapter<Category> = object: ArrayAdapter<Category>(this, R.layout.spinner_default, catList){
            override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
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


        if (selectedCategory.color != "0"){
            val i = catAdapter.getPosition(selectedCategory)
            category_spinner.setSelection(i)
        } else{
            category_spinner.setSelection(0)
        }


        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = adapterView?.getItemAtPosition(position) as Category
                twoWaysField.isChecked = selectedCategory.twoways
//                Toast.makeText(this@AddQuestionActivity, "You selected $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun saveAndGoToCategory(v: View) {
        saveToDB()
        val intent = Intent(this@AddQuestionActivity, ListCategory::class.java)
        intent.putExtra("catId", selectedCategory.id)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
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
            val db = dbHandler(this@AddQuestionActivity)
            val res = db.insertQuestion(q)
            if (res != -1 ){
                Toast.makeText(this@AddQuestionActivity, "Question added successfully.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this@AddQuestionActivity, "There was a problem adding the question.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@AddQuestionActivity, "You need to fill all fields first.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}