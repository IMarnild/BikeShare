package dk.itu.mips.bikeshare.viewmodel

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
import io.realm.kotlin.where

class EditBikeDialog : DialogFragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    var bike: Bike? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_bike_new, null)

            builder.setView(layout)

            this.bikeName = layout.findViewById(R.id.bike_name)
            this.bikeName.text = this.bike?.name

            this.bikeLocation = layout.findViewById(R.id.bike_location)
            this.bikeLocation.text = this.bike?.location

            builder.setPositiveButton("Save") { _, _ -> this.updateBike() }
            builder.setNegativeButton("Delete") { _, _ ->
                val parent = targetFragment as BikeInformationFragment
                parent.deleteBike(bike?.id!!)
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateBike() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        val realmBike = realm.where<Bike>().equalTo("id", this.bike?.id).findFirst()

        realm.executeTransaction { _ ->
            realmBike!!.name = this.bikeName.text.toString()
            realmBike.location = this.bikeLocation.text.toString()
        }
    }
}