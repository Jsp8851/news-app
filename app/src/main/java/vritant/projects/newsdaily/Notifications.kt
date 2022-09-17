package vritant.projects.newsdaily

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat

class Notifications(private val context: Context) {

    private val newsNotificationId = 1138

    private val newsPendingIntentId = 3417

    private val newsNotificationChannelId = "news_notification_channel"


    fun remind()
    {

        val article : News = NotificationNewsManager(context).getNews()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(newsNotificationChannelId,
                "news_notification",
                NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context,newsNotificationChannelId)

        builder.apply {
            color = ContextCompat.getColor(context,R.color.slide_bck)
            setContentTitle("We've got a News for You")
            setContentText(article.title)
            setSmallIcon(IconCompat.createWithResource(context,R.drawable.icon_nd))
            setLargeIcon(largeIcon(context))
            setContentIntent(contentIntent(context))
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(article.title))
            setVisibility(VISIBILITY_PUBLIC)
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O)
            {
                priority = NotificationCompat.PRIORITY_HIGH
            }
        }

        Log.d("periodicWorkRequest", " notification article is ${article.title}")

        notificationManager.notify(newsNotificationId,builder.build())
    }

    private fun contentIntent(context: Context): PendingIntent? {
        val intent = Intent(context, ArticleActivity::class.java)

        return PendingIntent.getActivity(context,newsPendingIntentId,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private fun largeIcon(context: Context): Bitmap {

        return BitmapFactory.decodeResource(context.resources, R.drawable.icon_nd)
    }


}