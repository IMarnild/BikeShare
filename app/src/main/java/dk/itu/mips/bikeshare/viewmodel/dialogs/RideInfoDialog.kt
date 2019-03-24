package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Ride

class RideInfoDialog : DialogFragment() {

    private val ARG_RIDE = "ride"

    private lateinit var bikeName: TextView
    private lateinit var startLocation: TextView
    private lateinit var endLocation: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var ride: Ride

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.ride = it.getSerializable(ARG_RIDE) as Ride
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_ride_info, null)

            builder.setView(layout)
            builder.setTitle("Ride information")

            this.initVariables(layout)
            this.setVariables()
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.startLocation = view.findViewById(R.id.ride_location_start)
        this.endLocation = view.findViewById(R.id.ride_location_end)
        this.startTime = view.findViewById(R.id.ride_time_start)
        this.endTime = view.findViewById(R.id.ride_time_end)
    }

    fun setVariables() {
        this.bikeName.text = ride.bikeName
        this.startLocation.text = ride.location_start
        this.endLocation.text = ride.location_end
        this.startTime.text = ride.time_start
        this.endTime.text = ride.time_end
    }

    companion object {
        @JvmStatic
        fun newInstance(ride: Ride) =
            RideInfoDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_RIDE, ride)
                }
            }
    }
}