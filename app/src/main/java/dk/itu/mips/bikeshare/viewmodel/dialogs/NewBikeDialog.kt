package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.viewmodel.fragments.BikeInformationFragment
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where

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
            ) { _, _ -> this.addBikeToRealm()

                // TODO: Find a better way to refresh ArrayAdapter
                val parent = targetFragment as BikeInformationFragment
                parent.updateSpinner(parent.view!!)
            }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addBikeToRealm() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        val bike = realm.where<Bike>().sort("id", Sort.DESCENDING).findFirst()
        val index = bike?.id ?: 0

        realm.executeTransaction { realm ->
            // Add a bike
            val bike = realm.createObject<Bike>(index+1)
            bike.name = bikeName.text.toString()
            bike.location = bikeLocation.text.toString()
        }
    }
}