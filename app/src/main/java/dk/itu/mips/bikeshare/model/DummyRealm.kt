package dk.itu.mips.bikeshare.model

import android.content.res.Resources
import android.graphics.BitmapFactory
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.util.BikeCamera
import java.util.*

class DummyRealm(val ressources: Resources) {

    private fun newDummyBike(name: String, location: String, price: Double, available: Boolean): Bike {
        val dummy = Bike()
        dummy.name = name
        dummy.location = location
        dummy.pricePerHour = price
        dummy.available = available
        val logo = BitmapFactory.decodeResource(ressources, R.mipmap.smallbike)
        dummy.photo = BikeCamera.bitmapToByteArray(logo)
        return dummy
    }

    private fun testBikes(): LinkedList<Bike> {
        val dummies = LinkedList<Bike>()

        dummies.add( newDummyBike("BikeOne", "Arne Jacobsens Allé 12, 2300 København", 15.0, true))
        dummies.add( newDummyBike("BikeTwo", "Rued Langgaards Vej 7, 2300 København", 35.0, true))
        dummies.add( newDummyBike("BikeThree", "Gothersgade 19, 1123 København", 10.0, true))
        dummies.add( newDummyBike("BikeFour", "Rued Langgaards Vej 7, 2300 København", 5.0, false))
        dummies.add( newDummyBike("BikeFve", "Gothersgade 19, 1123 København", 8.0, false))
        dummies.add( newDummyBike("BikeSix", "Kongens Nytorv 16, 1050 København\n", 20.0, true))
        dummies.add( newDummyBike("BikeSeven", "Arne Jacobsens Allé 12, 2300 København", 15.0, true))
        dummies.add( newDummyBike("BikeEight", "Gothersgade 19, 1123 København", 10.0, false))
        dummies.add( newDummyBike("BikeNine", "Rued Langgaards Vej 7, 2300 København", 25.0, true))
        dummies.add( newDummyBike("BikeTen", "Kongens Nytorv 16, 1050 København\n", 250.0, true))

        return dummies
    }

    fun load() {
        val realm = BikeRealm()
        this.testBikes().forEach { bike -> realm.create(bike) }
    }
}