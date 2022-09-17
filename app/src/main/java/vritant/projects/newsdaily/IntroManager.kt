package vritant.projects.newsdaily

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class IntroManager( context: Context) {

    private var sharedPreferences : SharedPreferences = context.getSharedPreferences("first",MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setFirst(isFirst : Boolean)
    {
        editor.putBoolean(INTRO_CHECK_KEY,isFirst)
        editor.commit()
        editor.apply()
    }

    fun check() : Boolean
    {
        return sharedPreferences.getBoolean(INTRO_CHECK_KEY,true)
    }
}