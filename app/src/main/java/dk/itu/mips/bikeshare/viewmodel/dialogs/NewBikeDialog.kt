package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.content.FileProvider
import android.widget.ImageButton
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.fragments.BikeSelectionFragment

class NewBikeDialog : DialogFragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikePrice: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_bike_new, null)

            builder.setView(layout)

            this.bikeName = layout.findViewById(R.id.bike_name)
            this.bikeLocation = layout.findViewById(R.id.bike_location)
            this.bikePrice = layout.findViewById(R.id.bike_price)

            builder.setPositiveButton(R.string.add
            ) { _, _ ->
                if (bikeName.text.isNotBlank() && bikeLocation.text.isNotBlank() && bikePrice.text.isNotBlank()) {
                    val parent = targetFragment as BikeSelectionFragment
                    parent.addBike(this.bikeName.text.toString(), this.bikeLocation.text.toString(), this.bikePrice.text.toString())
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