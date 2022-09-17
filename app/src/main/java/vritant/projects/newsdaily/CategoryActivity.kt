package vritant.projects.newsdaily

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_category.*
import vritant.projects.newsdaily.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity(),NewsItemClicked,NewsItemShareClicked {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: NewsListAdapter
    private lateinit var binding : ActivityCategoryBinding
    private lateinit var category: String

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
        supportActionBar?.displayOptions= androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.heading)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)


        val cat  = intent.getStringExtra(CATEGORY_KEY)?.lowercase()
        if (cat != null) {
            category=cat
        }

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        if (!isNetworkAvailable())
        {
            article_progress.visibility = View.GONE
            article_rv.visibility = View.GONE
            no_internet_iv.visibility = View.VISIBLE
        }
        else
        {
            getArticles()
        }

        sharedPreferences = getSharedPreferences("articleFirstTime",Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putBoolean("isFirst",true)
        editor.commit()
        editor.apply()

        observeNetworkState()

    }

    private fun ifFirstTime() : Boolean
    {
        return sharedPreferences.getBoolean("isFirst",true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isNetworkAvailable(): Boolean {

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun observeNetworkState()
    {
        viewModel.networkAvailable(this).observe(this) { isNetworkAvailable ->

            val snackBar = Snackbar.make(article_rv, "No", Snackbar.LENGTH_INDEFINITE)
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
                if (no_internet_iv.visibility == View.VISIBLE || article_progress.visibility == View.VISIBLE) {
                    no_internet_iv.visibility = View.GONE
                    article_rv.visibility = View.VISIBLE
                    article_progress.visibility = View.VISIBLE
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
        article_progress.visibility = View.VISIBLE
        viewModel.getData(this, category).observe(this
        ) { articles ->

            Log.d("size of the list : ", articles!!.size.toString())

            if (articles.isNotEmpty())
            {
                adapter = NewsListAdapter(this, articles as ArrayList<News>, this)
                binding.articleRv.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            else
                Snackbar.make(article_rv,"Unable to Fetch News at the moment",Snackbar.LENGTH_LONG)
                    .show()

            article_progress.visibility = View.GONE
        }
    }

    override fun onItemClicked(item: News) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }

    override fun onItemShareClicked(item: News) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, Checkout this news I read from News Daily App ${item.url} ")
        val chooser= Intent.createChooser(intent,"Share this news using...")
        startActivity(chooser)
    }

}