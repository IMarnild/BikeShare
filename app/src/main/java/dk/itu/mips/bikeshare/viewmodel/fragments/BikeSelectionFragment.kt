package dk.itu.mips.bikeshare.viewmodel.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.activities.NewBikeActivity
import dk.itu.mips.bikeshare.viewmodel.util.BikeArrayAdapter
import kotlinx.android.synthetic.main.fragment_bike_selection.*

class BikeSelectionFragment : Fragment() {
    private val bikeRealm: BikeRealm = BikeRealm()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.viewManager = if (Main.isViewHorizontal(resources)) GridLayoutManager(this.context, 2) else LinearLayoutManager(activity)
        this.viewAdapter = BikeArrayAdapter(this.bikeRealm.read()) { bike: Bike ->
            onListItemClicked(bike)
        }

        this.recycler_view_bikes
            .apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        this.btn_add_new_bike.setOnClickListener {
            this.startActivity(Intent(this.context, NewBikeActivity::class.java))
        }
    }

    override fun onResume() {
        this.viewAdapter = BikeArrayAdapter(this.bikeRealm.read()) { bike: Bike ->
            onListItemClicked(bike)
        }
        this.recycler_view_bikes.adapter = this.viewAdapter
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_selection, container, false)
    }

    private fun onListItemClicked(bike: Bike) {
        val bikeInfo = BikeInformationFragment.newInstance(bike.id)
        Main.replaceFragment(bikeInfo, this.fragmentManager!!)
    }
}