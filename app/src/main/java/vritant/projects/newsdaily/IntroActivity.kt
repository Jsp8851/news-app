package vritant.projects.newsdaily

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: SliderAdapter
    private val layouts = ArrayList<Int>()
    private lateinit var btnNext:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intro)

        hideSystemUI()

        viewPager = findViewById(R.id.view_pager)


        btnNext = findViewById(R.id.btn_next)

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts.add(R.layout.slider_1)
        layouts.add(R.layout.slider_2)


        adapter = SliderAdapter(this,layouts)
        viewPager.adapter=adapter


        viewPager.addOnPageChangeListener(viewListener)


        btnNext.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current = viewPager.currentItem + 1
            if (current < layouts.size) {
                // move to next screen
                viewPager.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }


    private fun Activity.hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {


            window.insetsController?.let {
                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                // and show it again upon user's touch. We just want the user to be able to show the
                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // make navigation bar translucent (alternative to deprecated
                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                // - do this already in hideSystemUI() so that the bar
                // is translucent if user swipes it up
                //window.navigationBarColor = getColor(R.color.transparent)
                // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and SYSTEM_UI_FLAG_FULLSCREEN.
                //it.hide(WindowInsets.Type.systemBars())
                //it.hide(WindowManager.LayoutParams.TYPE_STATUS_BAR)
                window.statusBarColor=getColor(R.color.slide_bck)
                supportActionBar?.hide()
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                @Suppress("DEPRECATION")
                window.decorView.apply {
                    systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            // Do not let system steal touches for showing the navigation bar
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    //Hide the nav bar and status bar
                    //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    // Keep the app content behind the bars even if user swipes them up
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // make navbar translucent - do this already in hideSystemUI() so that the bar
                    // is translucent if user swipes it up
                    @Suppress("DEPRECATION")
                    //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                    supportActionBar?.hide()
                    window.statusBarColor = getColor(R.color.slide_bck)
                }

        }
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    //  viewpager change listener
    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {}

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

}