package vritant.projects.newsdaily

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_news.view.*

class NewsListAdapter(listen: NewsItemClicked, item: ArrayList<News>,share : NewsItemShareClicked) : RecyclerView.Adapter<NewsViewHolder>() {

    private val items: ArrayList<News> = item
    private val listener : NewsItemClicked = listen
    private val shareListener : NewsItemShareClicked = share

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)

        val viewHolder = NewsViewHolder(view)

        view.full_article_button.setOnClickListener{
            listener.onItemClicked(items[viewHolder.absoluteAdapterPosition])
        }

        view.share_button.setOnClickListener {
            shareListener.onItemShareClicked(items[viewHolder.absoluteAdapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.titleView.text = currentItem.title
        holder.author.text = currentItem.author
        holder.agency.text = currentItem.newsAgency
        holder.content.text=currentItem.content


        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(currentItem.imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    holder.image.setImageBitmap(resource)
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    holder.image.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.news_error_image))
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onLoadCleared(placeholder: Drawable?) {
                    holder.image.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.news_error_image))
                }
            })


    }
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.title_tv)
    val image: ImageView = itemView.findViewById(R.id.image)
    val author: TextView = itemView.findViewById(R.id.author)
    val agency : TextView = itemView.findViewById(R.id.news_agency)
    val content : TextView = itemView.findViewById(R.id.content)
}

interface NewsItemClicked {
    fun onItemClicked(item: News)
}

interface NewsItemShareClicked {
    fun onItemShareClicked(item: News)
}