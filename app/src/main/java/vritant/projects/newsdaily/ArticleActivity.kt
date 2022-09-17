package vritant.projects.newsdaily

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_article.*

class ArticleActivity : AppCompatActivity(){

    private lateinit var article : News
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        supportActionBar?.displayOptions= androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.heading)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)

        article = NotificationNewsManager(this).getNews()

        author.text =  article.author
        title_tv.text = article.title
        news_agency.text = article.newsAgency

        Glide.with(image)
            .asBitmap()
            .load(article.imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    image.setImageBitmap(resource)
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    image.setImageDrawable(getDrawable(R.drawable.news_error_image))
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onLoadCleared(placeholder: Drawable?) {
                    image.setImageDrawable(getDrawable(R.drawable.news_error_image))
                }
            })

        content.text = article.content

        share_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,"Hey, Checkout this news I read from News Daily App ${article.url} ")
            val chooser= Intent.createChooser(intent,"Share this news using...")
            startActivity(chooser)
        }

        full_article_button.setOnClickListener {
            val builder =  CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(article.url))
        }

        MobileAds.initialize(this){}

        loadAd()

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            showAd()
        },3057)

    }

    private fun loadAd()
    {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, AD_UNIT_ID,adRequest,
            object : InterstitialAdLoadCallback()
            {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("showAd", adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("showAd", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun showAd()
    {
        if(mInterstitialAd!=null)
        {
            mInterstitialAd?.fullScreenContentCallback = object :  FullScreenContentCallback()
            {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("showAd", "Ad was dismissed.")

                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("showAd", "Ad failed to show.")

                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("showAd", "Ad showed fullscreen content.")
                    mInterstitialAd = null
                }
            }

            mInterstitialAd?.show(this)
        }
        else
        {
            Log.d("showAd", "Ad wasn't loaded")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MainActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            val intent = Intent(this,MainActivity::class.java)
            this.finish()
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        showAd()
    }
}