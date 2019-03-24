package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.dialogs.EditBikeDialog
import dk.itu.mips.bikeshare.viewmodel.dialogs.NewBikeDialog
import io.realm.Realm
import io.realm.kotlin.where


class BikeInformationFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var bike: Bike? = null
    private lateinit var bikeName: TextView

    private lateinit var bikeLocation: TextView
    private lateinit var bikeSpinner: Spinner
    private var bikeIndex: Int = 0
    private lateinit var adapter: ArrayAdapter<Any>
    private lateinit var bikes: Array<Any>
    private lateinit var startRide: Button
    private lateinit var editBike: Button
    private lateinit var addBike: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_infomation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        updateSpinner(view)
        this.setListeners()
        this.updateUI()
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeSpinner = view.findViewById(R.id.spinner_bike)
        this.startRide = view.findViewById(R.id.btn_start_ride)
        this.editBike = view.findViewById(R.id.btn_edit_bike)
        this.addBike = view.findViewById(R.id.btn_add_new_bike)
        this.bikeSpinner.onItemSelectedListener = this
    }

    fun updateSpinner(view: View) {
        val realm = Realm.getInstance(Main.getRealmConfig())
        this.bikes = realm.where<Bike>().sort("id").findAll().toArray()

        this.adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, bikes)
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.bikeSpinner.adapter = adapter
    }

    fun deleteBike(id: Long) {
        this.adapter.remove(this.bikeIndex)
        this.bikeSpinner.adapter = this.adapter

        val realm = Realm.getInstance(Main.getRealmConfig())
        realm.executeTransaction {
            val bike = realm.where<Bike>().equalTo("id", id).findFirst()
            bike!!.deleteFromRealm()
        }

        this.updateSpinner(this.view!!)
    }

    private fun setListeners() {
        this.startRide.setOnClickListener {
            Main.replaceFragment(ActiveRideFragment.newInstance(this.bike!!, Main.getDate()), fragmentManager!!)
        }

        this.addBike.setOnClickListener {
            val dialog = NewBikeDialog()
            dialog.setTargetFragment(this,1)
            dialog.show(fragmentManager,"New Bike")
        }

        this.editBike.setOnClickListener {
            val dialog = EditBikeDialog()
            dialog.setTargetFragment(this,1)
            dialog.bike = this.bike
            dialog.show(fragmentManager, "Edit Bike")
        }
    }

    private fun updateUI() {
        this.bikeName.text = this.bike?.name
        this.bikeLocation.text = this.bike?.location
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        this.bike = bikes[0] as Bike
        this.updateUI()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.bike = bikes[position] as Bike
        this.bikeIndex = position
        this.updateUI()
    }
}
