package vritant.projects.newsdaily

import android.content.Context
import android.net.Uri
import android.util.Log.d
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class NewsWorker(context:Context, workerParameters:WorkerParameters)  : Worker(context,workerParameters) {

    private lateinit var notificationArticle : News
    private var key = "a7ca283637ad4f0fb7c6218fa00fb292"
    private lateinit var notificationNewsManager: NotificationNewsManager

    override fun doWork(): Result {

        fetchNews()

        if (notificationArticle.title.isEmpty())
            return Result.failure()

        d("periodicWorkRequest"," news article is ${notificationArticle.title}")

        notificationNewsManager = NotificationNewsManager(applicationContext)
        notificationNewsManager.setNews(notificationArticle)

        Notifications(applicationContext).remind()

        return Result.success()
    }

    private fun fetchNews()
    {

        lateinit var title : String
        lateinit var author : String
        lateinit var articleUrl : String
        lateinit var imageUrl : String
        lateinit var content : String
        lateinit var newsAgency : String

        val future = RequestFuture.newFuture<JSONObject>()

        val mQueue = MySingleton.getInstance(applicationContext).getRequestQueue()

        val baseUri: Uri = Uri.parse(headlineUrl)
        val builder : Uri.Builder? = baseUri.buildUpon()

        if (builder != null) {
            builder.appendQueryParameter(CATEGORY_KEY, CATEGORY_GENERAL)
            builder.appendQueryParameter(apiKeyTag,key)
            builder.appendQueryParameter(pageTag,pageSize)
        }

        val url = builder.toString()

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            future,
            {
                Toast.makeText(applicationContext,"Unable to fetch News!",Toast.LENGTH_LONG).show()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

        mQueue.add(jsonObjectRequest)

        try {
            val response = future.get(10,TimeUnit.MINUTES)

            future.onResponse(response)

            val status = response.getString("status")
            if(status=="error")
            {
                key="752262aaa61b43bc80a60a8a6e720879"
                fetchNews()
            }

            d("periodicWorkRequest","fetching news ")

            val newsJsonArray = response.getJSONArray("articles")


                val newsJsonObject = newsJsonArray.getJSONObject((0 until newsJsonArray.length()).random())
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

            notificationArticle = News(title, author,newsAgency, articleUrl, imageUrl,content)


        }catch (e : InterruptedException) {

            e.printStackTrace()
            // exception handling
        } catch (e : ExecutionException) {
            e.printStackTrace()
            // exception handling
        } catch (e : TimeoutException) {
            e.printStackTrace()
        }

    }

}