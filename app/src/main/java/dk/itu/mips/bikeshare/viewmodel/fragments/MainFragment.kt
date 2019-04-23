package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setListeners()
    }

    private fun setListeners() {
        this.btn_new_ride.setOnClickListener {
          Main.replaceFragment(BikeSelectionFragment(), fragmentManager!!)
        }
        this.btn_history_ride.setOnClickListener {
            Main.replaceFragment(RideHistoryFragment(), fragmentManager!!)
        }

        this.btn_wallet.setOnClickListener {
            Main.replaceFragment(WalletFragment(), fragmentManager!!)
        }
    }
}
