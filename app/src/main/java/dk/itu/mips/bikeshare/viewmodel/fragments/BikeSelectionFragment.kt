package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.Util.BikeArrayAdapter


class BikeSelectionFragment : Fragment() {
    private val bikeRealm: BikeRealm = BikeRealm()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var newBikeButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.viewManager = LinearLayoutManager(activity)
        this.viewAdapter = BikeArrayAdapter(this.bikeRealm.read()) { bike: Bike ->
            onListItemClicked(bike)
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_bikes)
            .apply {
                setHasFixedSize(true)
                layoutManager =viewManager
                adapter = viewAdapter
            }

        this.newBikeButton = view.findViewById(R.id.btn_add_new_bike)
        this.newBikeButton.setOnClickListener {
            Main.replaceFragment(NewBikeFragment(), fragmentManager!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_selection, container, false)
    }

    private fun onListItemClicked(bike: Bike) {
        val bikeInfo = BikeInformationFragment.newInstance(bike.id)
        Main.replaceFragment(bikeInfo,this.fragmentManager!!)
    }
}
