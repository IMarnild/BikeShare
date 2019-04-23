package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Ride
import dk.itu.mips.bikeshare.viewmodel.util.RideArrayAdapter
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.dialog_ride_info.view.*
import kotlinx.android.synthetic.main.fragment_ride_history.*

class RideHistoryFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.viewManager = LinearLayoutManager(activity)
        this.viewAdapter = RideArrayAdapter(this.read()) { ride: Ride ->
            onListItemClicked(ride)
        }

        this.recycler_view_rides
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
        return realm.where<Ride>().sort("endTime", Sort.DESCENDING).findAll()
    }

    private fun onListItemClicked(ride: Ride) {
        this.showRideInfoDialog(ride)
    }

    private fun showRideInfoDialog(ride: Ride) {
        val view = layoutInflater.inflate(R.layout.dialog_ride_info, null)
        val dialog = AlertDialog.Builder(this.activity!!)
            .setView(view)
            .setTitle("Ride information")
            .setPositiveButton("close") { _, _ ->
            }
        dialog.create()
        view.bike_name.text = ride.bikeName
        view.ride_location_start.text = ride.startLocation
        view.ride_location_end.text = ride.endLocation
        view.ride_time_start.text = ride.startTime
        view.ride_time_end.text = ride.endTime
        view.ride_cost.text = String.format("%.2f", ride.cost).plus(" Dkk.")
        dialog.show()
    }
}
