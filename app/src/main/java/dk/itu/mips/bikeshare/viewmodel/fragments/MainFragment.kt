package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R

class MainFragment : Fragment() {

    private lateinit var newRide: Button
    private lateinit var rideHistory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.setListeners()
    }

    private fun initVariables(view: View) {
        this.newRide= view.findViewById(R.id.btn_new_ride)
        this.rideHistory = view.findViewById(R.id.btn_history_ride)
    }

    private fun setListeners() {
        this.newRide.setOnClickListener {
          Main.replaceFragment(BikeSelectionFragment(), fragmentManager!!)
        }

        this.rideHistory.setOnClickListener {
            Main.replaceFragment(RideHistoryFragment(), fragmentManager!!)
        }
    }
}
