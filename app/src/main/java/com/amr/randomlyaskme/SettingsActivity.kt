package com.amr.randomlyaskme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    lateinit var usersettings : UserSettings
    var actCat : Category = Category("0","0",false)
    val db = dbHandler(this)
    lateinit var questions: MutableList<Question>
    var selectedCategory : Category? = null

    private fun setAlarm(mins : Long, cat : String) {

        val am : AlarmManager = this@SettingsActivity.getSystemService(ALARM_SERVICE) as AlarmManager
        val amintent : Intent = Intent(this@SettingsActivity, AlarmReceiver::class.java)
        amintent.putExtra("cat", cat)
        val ampendingIntent : PendingIntent = PendingIntent.getBroadcast(
                this@SettingsActivity,
                0,
                amintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                1000 * 60 * mins,
                ampendingIntent
        )
        usersettings.setPaused(false)
        Toast.makeText(this, "The app will send you a notification every $mins minutes.", Toast.LENGTH_SHORT).show()

    }

    private fun cancelAlarm() {
        val am : AlarmManager = this@SettingsActivity.getSystemService(ALARM_SERVICE) as AlarmManager
        val amintent : Intent = Intent(this@SettingsActivity, AlarmReceiver::class.java)
        val ampendingIntent : PendingIntent = PendingIntent.getBroadcast(
                this@SettingsActivity,
                0,
                amintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        am.cancel(ampendingIntent)
        ampendingIntent.cancel()
        usersettings.setPaused(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)


        val pauseButton : Button = findViewById(R.id.pauseBtn)
        val runBtn : Button = findViewById(R.id.runBtn)
        val noqulabel : TextView = findViewById(R.id.noquestionshint)
        val notwinField = findViewById<EditText>(R.id.editTextNumber)
        val category_spinner = findViewById<Spinner>(R.id.setcategoryspinner)
        val s : SwitchCompat = findViewById(R.id.activecatswitch)
//        val setTimeBtn = findViewById<Button>(R.id.setTime)
        val activestatus : TextView = findViewById(R.id.activestatus)

        runBtn.isActivated = false
        runBtn.isEnabled = false
        questions = db.readAllQuestions()

        if(questions.isNotEmpty()){
            noqulabel.visibility = View.INVISIBLE
            runBtn.isActivated = true
            runBtn.isEnabled = true
        }

        usersettings = UserSettings(this)
        val isRunAll = usersettings.getRunAll()
        val actCatId = usersettings.getActCat()
        var notiWin = usersettings.getNotiWin()
        val isPaused = usersettings.getPaused()



        if(!isPaused){
            activestatus.text = "Active Every $notiWin mins"
            activestatus.setTextColor(Color.parseColor("#2ed573"))
            s.isActivated = false
            s.isEnabled = false
            s.alpha = 0.2F
            notwinField.isActivated = false
            notwinField.isEnabled = false
            notwinField.alpha = 0.2F
            category_spinner.isEnabled = false
            notwinField.isActivated = false
//            setTimeBtn.isActivated = false
//            setTimeBtn.isEnabled = false
//            setTimeBtn.alpha = 0.2F
            category_spinner.background.alpha = 20
            category_spinner.alpha = 0.2F
            findViewById<TextView>(R.id.textView6).alpha = 0.2F
            findViewById<TextView>(R.id.textView7).alpha = 0.2F
            findViewById<TextView>(R.id.textView4).alpha = 0.2F
            findViewById<TextView>(R.id.textView8).alpha = 0.2F
            findViewById<TextView>(R.id.textView9).alpha = 0.2F


        } else {
            activestatus.text = "Paused"
            activestatus.setTextColor(Color.parseColor("#ff4757"))

        }


        notwinField.setText(notiWin.toString())


        setSupportActionBar(findViewById(R.id.toolbar))
        val toolbar = supportActionBar
        toolbar!!.title = "Settings"
        toolbar.setDisplayHomeAsUpEnabled(true)



        if(isPaused) {
            pauseButton.visibility = View.INVISIBLE
            runBtn.visibility = View.VISIBLE
        } else{
            pauseButton.visibility = View.VISIBLE
            runBtn.visibility = View.INVISIBLE
        }

        val categories : MutableList<Category> = db.readCategories()

        val catList = mutableListOf(Category("Select Category", "0",false))
        categories.forEach{
            if (it.id == actCatId){
                actCat = it
            }
            catList.add(it)
        }

        val catAdapter : ArrayAdapter<Category> = object: ArrayAdapter<Category>(this, R.layout.spinner_default, catList){
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
                usersettings.setActCat(selectedCategory!!.id)
//                Toast.makeText(this@SettingsActivity, "You selected $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        if(isRunAll){
            category_spinner.background.alpha = 20
            category_spinner.alpha = 0.2F
            findViewById<TextView>(R.id.textView6).alpha = 0.2F
            category_spinner.isEnabled = false
        }

        s.isChecked = isRunAll
        s.setOnCheckedChangeListener { _, isChecked ->
            category_spinner.isEnabled = !isChecked
            usersettings.setRunAll(isChecked)
            if(isChecked){
                category_spinner.background.alpha = 20
                category_spinner.alpha = 0.2F
                findViewById<TextView>(R.id.textView6).alpha = 0.2F
            } else{
                category_spinner.alpha = 1F
                category_spinner.background.alpha = 255
                findViewById<TextView>(R.id.textView6).alpha = 1F
            }
        }


//        setTimeBtn.setOnClickListener{
//            val win : Int = Integer.parseInt(notwinField.text.toString())
//            if(win >= 1){
//                usersettings.setNotiWin(win)
//                notiWin = usersettings.getNotiWin()
//            } else{
//                Toast.makeText(this, "Please input a notification window that's larger than 0 minutes.", Toast.LENGTH_SHORT).show()
//            }
//        }
        pauseButton.setOnClickListener{
            pauseButton.visibility = View.INVISIBLE
            runBtn.visibility = View.VISIBLE

            cancelAlarm()
            Toast.makeText(this, "The app will not send you any notifications till enabled again.", Toast.LENGTH_SHORT).show()
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        runBtn.setOnClickListener{
            val win : Int = Integer.parseInt(notwinField.text.toString())
            if(win >= 1){
                usersettings.setNotiWin(win)
                notiWin = usersettings.getNotiWin()
            } else{
                Toast.makeText(this, "Please input a notification window that's larger than 0 minutes.", Toast.LENGTH_SHORT).show()
            }
            if(s.isChecked){
                pauseButton.visibility = View.VISIBLE
                runBtn.visibility = View.INVISIBLE
                setAlarm(notiWin.toLong(), "all")
            } else if (category_spinner.selectedItemPosition != 0){
                pauseButton.visibility = View.VISIBLE
                runBtn.visibility = View.INVISIBLE
                val cat = selectedCategory?.id
                setAlarm(notiWin.toLong(), cat.toString())
            } else{
                Toast.makeText(this, "You need to choose a category first.", Toast.LENGTH_SHORT).show()
            }
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }




    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}