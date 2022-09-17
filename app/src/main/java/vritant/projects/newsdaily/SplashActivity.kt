package vritant.projects.newsdaily

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeout : Long = 1357
    private lateinit var introManager: IntroManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        introManager = IntroManager(this)

        Handler(Looper.getMainLooper()).postDelayed({

            if(introManager.check())
            {
                val intent = Intent(this,IntroActivity::class.java)
                startActivity(intent)
                finish()

            }
            else
            {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, splashTimeout)
    }

}