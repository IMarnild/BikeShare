package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Ride
import dk.itu.mips.bikeshare.viewmodel.RideArrayAdapter
import dk.itu.mips.bikeshare.viewmodel.RideInfoDialog
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

class RideHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.viewManager = LinearLayoutManager(activity)
        this.viewAdapter = RideArrayAdapter(this.read()) { ride: Ride ->
            onListItemClicked(ride)
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_rides)
            .apply {
                setHasFixedSize(true)
                layoutManager =viewManager
                adapter = viewAdapter
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_ride_history, container, false)
    }

    private fun read(): RealmResults<Ride> {
        val realm = Realm.getInstance(Main.getRealmConfig())
        return realm.where<Ride>().findAll()
    }

    private fun onListItemClicked(ride: Ride) {
        println("clicked " + ride.bike?.name)
        val dialog = RideInfoDialog.newInstance(ride)
        dialog.show(fragmentManager, "Ride info")
    }
}
