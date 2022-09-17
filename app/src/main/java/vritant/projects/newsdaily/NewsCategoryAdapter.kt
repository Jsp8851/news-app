package vritant.projects.newsdaily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NewsCategoryAdapter(listen: NewsCategoryClicked, item: ArrayList<Category>) : RecyclerView.Adapter<NewsCategoryHolder>() {

    private val items: ArrayList<Category> = item
    private val listener : NewsCategoryClicked = listen

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsCategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)

        val viewHolder = NewsCategoryHolder(view)

        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.absoluteAdapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsCategoryHolder, position: Int) {
        val currentItem = items[position]
        if(position%2!=0)
        {
            val params : ViewGroup.MarginLayoutParams = holder.card.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0,173,0,0)
            holder.card.requestLayout()
        }
        holder.title1.text=currentItem.title1
        holder.image1.setImageResource(currentItem.imgRes1)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class NewsCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title1: TextView = itemView.findViewById(R.id.category_text1)
    val image1: ImageView = itemView.findViewById(R.id.category_image1)
    val card : CardView = itemView.findViewById(R.id.imageCardView1)
}

interface NewsCategoryClicked {
    fun onItemClicked(item: Category)
}