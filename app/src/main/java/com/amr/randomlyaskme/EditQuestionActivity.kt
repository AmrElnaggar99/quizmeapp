package com.amr.randomlyaskme

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.snackbar.Snackbar

class EditQuestionActivity : AppCompatActivity() {
    var actCat : Category = Category("0","0",false)
    val db = dbHandler(this)
    private lateinit var selectedCategory : Category
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

        val queId = intent.getIntExtra("queId", 0)
        val actQue = db.readSingleQuestion(queId)

        val questionField = findViewById<EditText>(R.id.editquestion)
        val answerField = findViewById<EditText>(R.id.editanswer)
        val twowaysField = findViewById<SwitchCompat>(R.id.editswitch1)
        val delQue = findViewById<ImageButton>(R.id.delQue)

        delQue.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure you want to delete this question?")
            builder.setMessage("This action cannot be undone.")
            builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int -> executeDeleteQue(this,actQue) }
            builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
            builder.show()
        }
        questionField.setText(actQue.question)
        answerField.setText(actQue.answer)
        twowaysField.isChecked = actQue.twoways

        setSupportActionBar(findViewById(R.id.toolbar))
        val toolbar = supportActionBar
        toolbar!!.title = "Edit a question"
        toolbar.setDisplayHomeAsUpEnabled(true)

        val categories : MutableList<Category> = db.readCategories()

        val catList = mutableListOf(Category("Select Category", "0",false))
        categories.forEach{
            if (it.id == actQue.categoryID){
                actCat = it
            }
            catList.add(it)
        }
        val category_spinner = findViewById<Spinner>(R.id.editcategoryspinner)
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
        if(actCat.id != 0){
            category_spinner.setSelection(catAdapter.getPosition(actCat))
        } else {
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
//                Toast.makeText(this@EditQuestionActivity, "You selected $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val editbtn : Button = findViewById(R.id.editbutton)

        editbtn.setOnClickListener{
            val question : String = questionField.text.toString()
            val answer : String = answerField.text.toString()
            val twoways : Boolean = twowaysField.isChecked
            val categoryId : Int = selectedCategory.id
            val thisQuestion = Question(question,answer,twoways,categoryId,false)
            thisQuestion.id = queId
            saveAndLeave(thisQuestion)
        }
    }

    private fun executeDeleteQue(itemView: Any, actQue: Question) {
        val res = db.deleteQue(actQue.id)
        if (res != -1){
            val intent = Intent(this@EditQuestionActivity, ListCategory::class.java)
            intent.putExtra("catId", actQue.categoryID)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)

        } else{
            Toast.makeText(this, "There was a problem deleting the question.", Toast.LENGTH_SHORT).show()
        }

    }

    fun saveAndLeave(question : Question) {
        db.updateQuestion(question)
        val intent = Intent(this@EditQuestionActivity, ListCategory::class.java)
        intent.putExtra("catId", selectedCategory.id)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true

    }
}