package dk.itu.mips.bikeshare.viewmodel.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.BikeInformation
import dk.itu.mips.bikeshare.viewmodel.dialogs.EditBikeDialog
import dk.itu.mips.bikeshare.viewmodel.dialogs.NewBikeDialog
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class BikeSelectionFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var bike: Bike? = null
    private var bikeIndex: Int = 0

    private lateinit var bikeInfo: BikeInformation
    private lateinit var bikeSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<Any>
    private lateinit var bikes: Array<Any>
    private lateinit var startRide: Button
    private lateinit var editBike: Button
    private lateinit var addBike: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        updateSpinner(view)
        this.setListeners()
        this.updateUI()
    }

    private fun initVariables(view: View) {
        this.bikeInfo = BikeInformation(this)
        this.bikeSpinner = view.findViewById(R.id.spinner_bike)
        this.startRide = view.findViewById(R.id.btn_start_ride)
        this.editBike = view.findViewById(R.id.btn_edit_bike)
        this.addBike = view.findViewById(R.id.btn_add_new_bike)
        this.bikeSpinner.onItemSelectedListener = this
    }

    private fun updateSpinner(view: View) {
        val realm = Realm.getInstance(Main.getRealmConfig())
        this.bikes = realm.where<Bike>().sort("id").findAll().toArray()


        this.adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, bikes)
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.bikeSpinner.adapter = adapter
    }

    fun addBike(bike: Bike) {
        val realm = Realm.getInstance(Main.getRealmConfig())
        realm.executeTransaction { realm ->
            bike.id = this.newBikeIndex(realm)
            realm.insertOrUpdate(bike)
        }

        this.updateSpinner(this.view!!)
        Main.makeToast(this.context!!, "Bike Added!")
    }

    fun deleteBike(id: Long) {
        val realm = Realm.getInstance(Main.getRealmConfig())
        realm.executeTransaction {
            val bike = realm.where<Bike>().equalTo("id", id).findFirst()
            this.bikeInfo.getPhotoFile(bike!!).delete()
            bike.deleteFromRealm()
        }

        this.updateSpinner(this.view!!)
        Main.makeToast(this.context!!, "Bike Deleted!")
    }

    private fun newBikeIndex(realm: Realm) : Long {
        val latestBike = realm.where<Bike>().sort("id", Sort.DESCENDING).findFirst()
        val index = latestBike?.id ?: 0
        return index + 1
    }

    private fun setListeners() {
        this.startRide.setOnClickListener {
            Main.replaceFragment(ActiveRideFragment.newInstance(this.bike!!.id, Main.getDate()), fragmentManager!!)
        }

        this.addBike.setOnClickListener {
            val dialog = NewBikeDialog()
            dialog.setTargetFragment(this,1)
            dialog.show(fragmentManager,"New Bike")
        }

        this.editBike.setOnClickListener {
            val dialog = EditBikeDialog.newInstance(this.bike!!)
            dialog.setTargetFragment(this,1)
            dialog.show(fragmentManager, "Edit Bike")
        }
    }

    fun updateUI() {
        if (this.bike != null) this.bikeInfo.bind(this.bike!!)
        this.adapter.notifyDataSetChanged()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            val uri = FileProvider.getUriForFile(activity!!, "dk.itu.mips.bikeshare.fileprovider", bikeInfo.photoFile)
            activity!!.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            bikeInfo.updatePhotoView()
        }
    }
}
