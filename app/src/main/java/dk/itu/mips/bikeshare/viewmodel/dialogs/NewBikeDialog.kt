package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.fragments.BikeSelectionFragment

class NewBikeDialog : DialogFragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_bike_new, null)

            builder.setView(layout)

            this.bikeName = layout.findViewById(R.id.bike_name)
            this.bikeLocation = layout.findViewById(R.id.bike_location)

            builder.setPositiveButton(R.string.add
            ) { _, _ ->
                if (bikeName.text.isNotBlank() && bikeLocation.text.isNotBlank()) {
                    val parent = targetFragment as BikeSelectionFragment
                    parent.addBike(this.bikeName.text.toString(), this.bikeLocation.text.toString())
                }
            }

            builder.setNegativeButton(R.string.cancel
            ) { _, _ ->
                dialog.cancel()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}