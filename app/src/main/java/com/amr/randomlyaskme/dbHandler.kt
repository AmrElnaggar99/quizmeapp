package com.amr.randomlyaskme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DATABASE_NAME = "quizMeDB"

val CAT_TABLE = "Categories"
val CAT_NAME = "title"
val CAT_COLOR = "color"
val CAT_ID = "catId"
val CAT_TWOWAYS = "twoways"

val QUE_TABLE = "Questions"
val QUE_ID = "queId"
val QUE_QUESTION = "question"
val QUE_ANSWER = "answer"
val QUE_TWOWAYS = "twoways"
val QUE_CATEGORYID = "categoryId"
val QUE_REVISE = "revise"


class dbHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        val catTable = "CREATE TABLE " + CAT_TABLE + " (" +
                CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CAT_NAME + " VARCHAR(255), " +
                CAT_COLOR + " VARCHAR(255), " +
                CAT_TWOWAYS + " INTEGER); "
        db?.execSQL(catTable)
        val queTable = "CREATE TABLE " + QUE_TABLE + " ( " +
                QUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QUE_QUESTION + " VARCHAR(255), " +
                QUE_ANSWER + " VARCHAR(255), " +
                QUE_TWOWAYS + " INTEGER, " +
                QUE_REVISE + " INTEGER, " +
                QUE_CATEGORYID + " INTEGER);"
        db?.execSQL(queTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertQuestion (question : Question): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(QUE_QUESTION, question.question)
        cv.put(QUE_ANSWER, question.answer)
        cv.put(QUE_TWOWAYS, question.twoways)
        cv.put(QUE_CATEGORYID, question.categoryID)
        cv.put(QUE_REVISE, question.revise)
        return db.insert(QUE_TABLE, null, cv).toInt()
    }

    fun insertCategory (category : Category): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(CAT_NAME, category.title)
        cv.put(CAT_COLOR, category.color)
        cv.put(CAT_TWOWAYS, category.twoways)
        return db.insert(CAT_TABLE, null, cv)
    }

    fun readCategoryQuestions(id : Int) : MutableList<Question>{
        val list : MutableList<Question> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $QUE_TABLE WHERE $QUE_CATEGORYID = $id"
        val result = db.rawQuery(query, null)
        var question = ""
        var answer = ""
        var toways = 0
        var revise = 0
        var qid = 0
        if(result.moveToFirst()){
            do
            {
                question = result.getString(result.getColumnIndex(QUE_QUESTION))
                answer = result.getString(result.getColumnIndex(QUE_ANSWER))
                toways = result.getInt(result.getColumnIndex(QUE_TWOWAYS))
                revise = result.getInt(result.getColumnIndex(QUE_REVISE))
                qid = result.getInt(result.getColumnIndex(QUE_ID))
                val res : Question = Question(question, answer, toways.toBoolean(), id,revise.toBoolean())
                res.id = qid
                list.add(res)
            }
            while(result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
    fun readSingleQuestion(id : Int) : Question{
        val db = this.readableDatabase
        val query = "SELECT * FROM $QUE_TABLE WHERE $QUE_ID = $id"
        val result = db.rawQuery(query, null)
        var question = ""
        var answer = ""
        var toways = 0
        var revise = 0
        var catid = 0
        if(result.moveToFirst()){
            do
            {
                question = result.getString(result.getColumnIndex(QUE_QUESTION))
                answer = result.getString(result.getColumnIndex(QUE_ANSWER))
                toways = result.getInt(result.getColumnIndex(QUE_TWOWAYS))
                revise = result.getInt(result.getColumnIndex(QUE_REVISE))
                catid = result.getInt(result.getColumnIndex(QUE_CATEGORYID))
            }
            while(result.moveToNext())
        }
        val res : Question = Question(question, answer, toways.toBoolean(), catid,revise.toBoolean())
        res.id = id
        result.close()
        db.close()
        return res
    }

    fun readAllQuestions() : MutableList<Question>{
        val list : MutableList<Question> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $QUE_TABLE"
        val result = db.rawQuery(query, null)
        var question = ""
        var answer = ""
        var toways = 0
        var revise = 0
        var qid = 0
        var catid = 0
        if(result.moveToFirst()){
            do
            {
                question = result.getString(result.getColumnIndex(QUE_QUESTION))
                answer = result.getString(result.getColumnIndex(QUE_ANSWER))
                toways = result.getInt(result.getColumnIndex(QUE_TWOWAYS))
                revise = result.getInt(result.getColumnIndex(QUE_REVISE))
                qid = result.getInt(result.getColumnIndex(QUE_ID))
                catid = result.getInt(result.getColumnIndex(QUE_CATEGORYID))
                val res : Question = Question(question, answer, toways.toBoolean(), catid,revise.toBoolean())
                res.id = qid
                list.add(res)
            }
            while(result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun readReviseQuestions() : MutableList<Question>{
        val list : MutableList<Question> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $QUE_TABLE WHERE $QUE_REVISE = 1"
        val result = db.rawQuery(query, null)
        var question = ""
        var answer = ""
        var toways = 0
        var revise = 0
        var qid = 0
        var catid = 0
        if(result.moveToFirst()){
            do
            {
                question = result.getString(result.getColumnIndex(QUE_QUESTION))
                answer = result.getString(result.getColumnIndex(QUE_ANSWER))
                toways = result.getInt(result.getColumnIndex(QUE_TWOWAYS))
                revise = result.getInt(result.getColumnIndex(QUE_REVISE))
                qid = result.getInt(result.getColumnIndex(QUE_ID))
                catid = result.getInt(result.getColumnIndex(QUE_CATEGORYID))
                val res : Question = Question(question, answer, toways.toBoolean(), catid,revise.toBoolean())
                res.id = qid
                list.add(res)
            }
            while(result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun readSingleCategory(id : Int) : Category{
        val db = this.readableDatabase
        val query = "SELECT * FROM $CAT_TABLE WHERE $CAT_ID = $id"
        val result = db.rawQuery(query, null)
        var title = ""
        var color = ""
        var toways = 0
        if(result.moveToFirst()){
            do
            {
                title = result.getString(result.getColumnIndex(CAT_NAME))
                color = result.getString(result.getColumnIndex(CAT_COLOR))
                toways = result.getInt(result.getColumnIndex(CAT_TWOWAYS))
            }
                while(result.moveToNext())
        }
        val res : Category = Category(title, color, toways.toBoolean())
        res.id = id
        result.close()
        db.close()
        return res
    }
    fun readCategories() : MutableList<Category>{
        val list : MutableList<Category> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $CAT_TABLE"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val title = result.getString(result.getColumnIndex(CAT_NAME))
                val color = result.getString(result.getColumnIndex(CAT_COLOR))
                val twoways = result.getInt(result.getColumnIndex(CAT_TWOWAYS))

                val category = Category(title, color, twoways.toBoolean())
                category.id = result.getString(result.getColumnIndex(CAT_ID)).toInt()
                list.add(category)
            }
                while(result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun deleteCat(id : Int) : Int {
        val db = this.writableDatabase
        db.delete(QUE_TABLE,"$QUE_CATEGORYID = ?", arrayOf(id.toString()))
        return db.delete(CAT_TABLE,"$CAT_ID = ?", arrayOf(id.toString()))
    }

    fun deleteQue(id : Int) : Int {
        val db = this.writableDatabase
        return db.delete(QUE_TABLE,"$QUE_ID = ?", arrayOf(id.toString()))
    }

    fun updateQuestion(question : Question) {
        val db = this.writableDatabase
        val query = "SELECT * FROM $QUE_TABLE"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do{
                var cv = ContentValues()
                cv.put(QUE_QUESTION, question.question)
                cv.put(QUE_ANSWER, question.answer)
                cv.put(QUE_CATEGORYID, question.categoryID)
                cv.put(QUE_TWOWAYS, question.twoways)
                cv.put(QUE_REVISE, question.revise)
                val stringid = question.id.toString()
                db.update(QUE_TABLE, cv, "$QUE_ID = ?", arrayOf(stringid))
            } while (result.moveToNext())
        }
    }
    private fun Int.toBoolean() = this == 1
}