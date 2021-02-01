package com.amr.randomlyaskme

import android.content.Context

class UserSettings(context : Context) {

    val RUNALL = "runAll"
    val ACTIVE_CAT = "activeCategory"
    val NOTI_WINDOW = "notiwindow"
    val PAUSED = "paused"

    val settings_runall = context.getSharedPreferences(RUNALL, Context.MODE_PRIVATE)

    fun getRunAll() : Boolean {
        return settings_runall.getBoolean(RUNALL, false)
    }
    fun setRunAll(state: Boolean){
        val editor = settings_runall.edit()
        editor.putBoolean(RUNALL, state)
        editor.apply()
    }

    val settings_actcat = context.getSharedPreferences(ACTIVE_CAT, Context.MODE_PRIVATE)

    fun getActCat() : Int {
        return settings_actcat.getInt(ACTIVE_CAT, 0)
    }
    fun setActCat(state: Int){
        val editor = settings_actcat.edit()
        editor.putInt(ACTIVE_CAT, state)
        editor.apply()
    }

    val settings_nitwindow = context.getSharedPreferences(NOTI_WINDOW, Context.MODE_PRIVATE)

    fun getNotiWin() : Int {
        return settings_nitwindow.getInt(NOTI_WINDOW, 60)
    }
    fun setNotiWin(state: Int){
        val editor = settings_nitwindow.edit()
        editor.putInt(NOTI_WINDOW, state)
        editor.apply()
    }

    val settings_paused = context.getSharedPreferences(PAUSED, Context.MODE_PRIVATE)

    fun getPaused() : Boolean {
        return settings_paused.getBoolean(PAUSED, true)
    }
    fun setPaused(state: Boolean){
        val editor = settings_paused.edit()
        editor.putBoolean(PAUSED, state)
        editor.apply()
    }

}