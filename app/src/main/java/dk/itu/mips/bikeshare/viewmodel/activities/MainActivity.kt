package dk.itu.mips.bikeshare.viewmodel.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.fragments.ActiveRideFragment
import dk.itu.mips.bikeshare.viewmodel.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        var fragments = supportFragmentManager.fragments
        var handled = false

        fragments.map { f ->
            if (f is ActiveRideFragment) {
                handled = f.endLocationIsBlank()
                if (handled) f.noEndLocationWarning()
            }
        }

        if (!handled) super.onBackPressed()
    }
}