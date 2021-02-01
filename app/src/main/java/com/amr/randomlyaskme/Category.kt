package com.amr.randomlyaskme

import android.R.attr.name


class Category (var title: String, var color: String, var twoways: Boolean) {
    var id : Int = 0

    //to display object as a string in spinner
    override fun toString(): String {
        return title
    }
}