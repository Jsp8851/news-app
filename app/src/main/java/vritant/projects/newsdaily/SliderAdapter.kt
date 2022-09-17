package vritant.projects.newsdaily

import android.app.Service
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter

class SliderAdapter(var context: Context, private var layout: ArrayList<Int>) : PagerAdapter() {

    private lateinit var inflater : LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = inflater.inflate(layout[position],container,false)

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
       return layout.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj as ConstraintLayout
        }

}