package com.amr.randomlyaskme

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat


class AddCategoryActivity : AppCompatActivity() {
    private var colors = arrayOf("#3498db", "#9b59b6", "#2ecc71", "#e67e22", "#c0392b", "#16a085", "#8e44ad", "#f39c12", "#16a085", "#d35400")
    private var thisColor : String = colors.random()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
        val layout : View = findViewById(R.id.addcatlayout)
        val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor(thisColor), Color.parseColor("#000000")))
        gd.cornerRadius = 0f

        layout.background = gd

    }

    fun saveAndGoHome(v: View){
        saveToDB()
        val intent = Intent(this@AddCategoryActivity, MainActivity::class.java)
        startActivity(intent)
    }
    fun saveAndGoToAdd(v: View){
        val catId = saveToDB()
        val intent = Intent(this@AddCategoryActivity, AddQuestionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        if(catId != -1){
            intent.putExtra("selectedCat", catId)
            startActivity(intent)
        }

    }

    fun saveToDB() : Int{
        val catTitleField : EditText = findViewById(R.id.crcategorytitle)
        val catTitle : String = catTitleField.text.toString()
        if(catTitle.isNotEmpty()){
            // check if it exists in the db already and show alert
            val twoWaysField : SwitchCompat = findViewById(R.id.switch2)
            val category = Category(catTitle, thisColor, twoWaysField.isChecked)
            val db = dbHandler(this@AddCategoryActivity)
            val res = db.insertCategory(category)
            if (res != (-1).toLong()){
                Toast.makeText(this@AddCategoryActivity, "Category created successfully.", Toast.LENGTH_SHORT).show()
                return (res).toInt()

            } else{
                Toast.makeText(this@AddCategoryActivity, "There was a problem creating the category.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@AddCategoryActivity, "You need to give the category a title first.", Toast.LENGTH_SHORT).show()
        }
        return -1
    }

    fun close(view: View) {
        finish();
    }


}