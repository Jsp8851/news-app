package vritant.projects.newsdaily

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log.d
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import vritant.projects.newsdaily.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(),NewsItemClicked,NewsCategoryClicked,NewsItemShareClicked{

    private lateinit var viewModel: MainViewModel
    private lateinit var newsAdapter: NewsListAdapter

    private lateinit var categoryList : ArrayList<Category>
    private lateinit var categoryAdapter: NewsCategoryAdapter
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navView : NavigationView
    private lateinit var introManager: IntroManager
    private lateinit var binding : ActivityMainBinding

    private lateinit var workManager : WorkManager

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar?.displayOptions= androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.heading)

        binding.categoryCards.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        categoryList = ArrayList()
        categoryList.add(Category(getString(R.string.name_tech), R.drawable.technology))
        categoryList.add(Category(getString(R.string.name_business), R.drawable.business2))
        categoryList.add(Category(getString(R.string.name_health), R.drawable.health2))
        categoryList.add(Category(getString(R.string.name_entertainment), R.drawable.entertainment2))
        categoryList.add(Category(getString(R.string.name_sports), R.drawable.sports2))
        categoryList.add(Category(getString(R.string.name_science), R.drawable.science2))

        categoryAdapter = NewsCategoryAdapter(this@MainActivity, categoryList)
        binding.categoryCards.adapter = categoryAdapter
        viewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]

        drawerLayout=findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.email_contact -> {
                    val address = arrayOf(getString(R.string.developer_email))
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.putExtra(Intent.EXTRA_EMAIL,address)
                    intent.data = Uri.parse("mailto:")
                    if(intent.resolveActivity(packageManager)!=null)
                    {
                        startActivity(intent)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_open_24)

        sharedPreferences = getSharedPreferences("firstTime",Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("isFirst",true)
        editor.commit()
        editor.apply()


        if (!isNetworkAvailable())
        {
            list.visibility = View.GONE
            no_internet_iv.visibility = View.VISIBLE
        }
        else
        {
            getArticles()
        }

        observeNetworkState()

        introManager = IntroManager(this)

        if (introManager.check())
        {
            setupWorkManager()
            introManager.setFirst(false)
        }

        MobileAds.initialize(this){}

        loadAd()
    }

    override fun onRestart() {
        super.onRestart()
        showAd()
    }

    private fun loadAd()
    {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, AD_UNIT_ID,adRequest,
            object : InterstitialAdLoadCallback()
        {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                d("showAd",adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                d("showAd", "Ad was loaded.")
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
                    d("showAd", "Ad was dismissed.")

                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    d("showAd", "Ad failed to show.")

                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    d("showAd", "Ad showed fullscreen content.")
                    mInterstitialAd = null
                }
            }

            mInterstitialAd?.show(this)
        }
        else
        {
            d("showAd","Ad wasn't loaded")
        }
    }

    private fun ifFirstTime() : Boolean
    {
        return sharedPreferences.getBoolean("isFirst",true)
    }


    private fun setupWorkManager()
    {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(NewsWorker::class.java,6,TimeUnit.HOURS)
            .setInitialDelay(2,TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork("NewsFetchWork",ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest)

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observeForever {
            if (it != null) {

                d("periodicWorkRequest", "Status changed to ${it.state}")

            }
        }
    }

    private fun isNetworkAvailable(): Boolean {

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setupSlideButton()
    {
        slide_button.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                val pos = general_news.currentItem
                if ((pos + 1) != general_news.adapter?.itemCount) {
                    general_news.currentItem = pos+1
                }
                else
                    Snackbar.make(drawerLayout,"End of the list",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun observeNetworkState()
    {
        viewModel.networkAvailable(this).observe(this) { isNetworkAvailable ->

            val snackBar = Snackbar.make(list, "No", Snackbar.LENGTH_INDEFINITE)
            val view = snackBar.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params

            if (!isNetworkAvailable) {
                snackBar.apply {
                    setText("No Internet Connection")
                    duration = Snackbar.LENGTH_INDEFINITE
                    setTextColor(Color.WHITE)
                    setBackgroundTint(Color.RED)
                    show()
                }

            } else {
                if (no_internet_iv.visibility == View.VISIBLE || progress.visibility == View.VISIBLE) {
                    no_internet_iv.visibility = View.GONE
                    list.visibility = View.VISIBLE
                    getArticles()
                }
                if (!ifFirstTime()) {
                    snackBar.apply {
                        setText("Online")
                        duration = 500
                        setTextColor(Color.BLACK)
                        setBackgroundTint(Color.GREEN)
                        show()
                    }
                }
                if (ifFirstTime()) {
                    editor.putBoolean("isFirst", false)
                    editor.commit()
                }
            }
        }
    }


    private fun getArticles()
    {
        viewModel.getData(this).observe(this, Observer
        {  articles ->
            d("size of the list : ", articles!!.size.toString())
            println("size of the list : ${articles.size}")

            if (articles.isNotEmpty()) {
                newsAdapter = NewsListAdapter(this, articles as ArrayList<News>,this)
                binding.generalNews.adapter=newsAdapter
                newsAdapter.notifyDataSetChanged()
                setupSlideButton()
            } else
                Snackbar.make(drawerLayout,"Unable to fetch News at the moment",Snackbar.LENGTH_LONG).show()

            progress.visibility = View.GONE

        })
    }

    override fun onItemClicked(item: News) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            true
        } else

        return super.onOptionsItemSelected(item)
    }

    override fun onItemShareClicked(item: News) {
        val intent =Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, Checkout this news I read from News Daily App ${item.url} ")
        val chooser= Intent.createChooser(intent,"Share this news using...")
        startActivity(chooser)
    }

    override fun onItemClicked(item: Category) {
        val intent = Intent(this,CategoryActivity::class.java)
        intent.putExtra(CATEGORY_KEY,item.title1)
        startActivity(intent)
    }

}