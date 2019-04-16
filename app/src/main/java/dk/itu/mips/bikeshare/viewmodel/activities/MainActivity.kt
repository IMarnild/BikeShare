package dk.itu.mips.bikeshare.viewmodel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.fragments.ActiveRideFragment
import dk.itu.mips.bikeshare.viewmodel.fragments.MainFragment
import dk.itu.mips.bikeshare.viewmodel.util.GPS

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GPS(this).requestLocationUpdates()

        if (this.findViewById<FrameLayout>(R.id.fragment_container) != null) {

            if (savedInstanceState != null) return

            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    MainFragment()
                )
                .commit()
        }
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        fragments.map { f ->
            when (f) {
                is ActiveRideFragment -> f.endRide.callOnClick()
                else -> super.onBackPressed()
            }
        }
    }
}