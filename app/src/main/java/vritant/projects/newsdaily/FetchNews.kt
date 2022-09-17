package vritant.projects.newsdaily

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest


class FetchNews(private val newsCategory : String,private val context : Context) {

    private var mQueue: RequestQueue? = null
    private val articleList = ArrayList<News>()
    private var key = "a7ca283637ad4f0fb7c6218fa00fb292"
    private val newsList = MutableLiveData<List<News>>()

    private lateinit var title : String
    private lateinit var author : String
    private lateinit var articleUrl : String
    private lateinit var imageUrl : String
    private lateinit var content : String
    private lateinit var newsAgency : String

    fun getData() : LiveData<List<News>>
    {
        fetch()
        return newsList
    }

     private fun fetch()
    {

        mQueue = MySingleton.getInstance(context).getRequestQueue()

        val baseUri: Uri = Uri.parse(headlineUrl)
        val builder : Uri.Builder? = baseUri.buildUpon()


        if (builder != null) {
            builder.appendQueryParameter(CATEGORY_KEY,newsCategory)
            builder.appendQueryParameter(apiKeyTag,key)
            builder.appendQueryParameter(pageTag,pageSize)
        }

        val url = builder.toString()

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            {
                val status = it.getString("status")
                if(status=="error")
                {
                    key="752262aaa61b43bc80a60a8a6e720879"
                    fetch()
                }

                val newsJsonArray = it.getJSONArray("articles")

                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    title = newsJsonObject.getString("title")
                    val sourceJSONObject = newsJsonObject.getJSONObject("source")
                    newsAgency = sourceJSONObject.getString("name")
                    author = newsJsonObject.getString("author")
                    articleUrl = newsJsonObject.getString("url")
                    imageUrl = newsJsonObject.getString("urlToImage")
                    val contentString = newsJsonObject.getString("content")
                    if(contentString!="null")
                    {
                        content = contentString.dropLast(15)
                        content = "$content...................."
                    }
                    else
                    {
                        content = "No details found. Please head over to read the full article."
                    }
                    if(author=="null")
                    {
                        author="Unknown"
                    }

                    val news = News(title, author,newsAgency, articleUrl, imageUrl,content)

                    articleList.add(news)
                }
                newsList.value = articleList

            },
            {

            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }

        }

        mQueue!!.add(jsonObjectRequest)
    }
}