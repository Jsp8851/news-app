package vritant.projects.newsdaily

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class NotificationNewsManager( context: Context) {

    private var sharedPreferences : SharedPreferences = context.getSharedPreferences("news",MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()


    fun setNews(article : News)
    {
        editor.apply {
            putString("title",article.title)
            putString("author",article.author)
            putString("newsAgency",article.newsAgency)
            putString("url",article.url)
            putString("imageUrl",article.imageUrl)
            putString("content",article.content)
            commit()
            apply()
        }
    }

    fun getNews() : News
    {
        return News(sharedPreferences.getString("title","").toString(),
            sharedPreferences.getString("author","").toString(),
        sharedPreferences.getString("newsAgency","").toString(),
            sharedPreferences.getString("url","").toString(),
            sharedPreferences.getString("imageUrl","").toString(),
            sharedPreferences.getString("content","").toString(),
            )
    }
}