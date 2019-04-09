package dk.itu.mips.bikeshare.model

import dk.itu.mips.bikeshare.Main
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class RideRealm() {

    private val realm = Realm.getInstance(Main.getRealmConfig())

    fun create(ride: Ride): Long {
        this.realm.executeTransaction { r ->
            ride.id = this.newRideIndex(realm)
            realm.insertOrUpdate(ride)
        }
        return ride.id
    }

    fun read(id: Long): Ride? {
        return this.realm.where<Ride>().equalTo("id", id).findFirst()
    }

    fun update(ride: Ride): Long {
        val realmBike = realm.where<Ride>().equalTo("id", ride.id).findFirst()
        if (realmBike != null) {
            this.realm.executeTransaction {
                this.realm.insertOrUpdate(ride)
            }
        }
        return ride.id
    }

    fun delete(ride: Ride) {
        this.realm.executeTransaction {
            val realmRide = realm.where<Ride>().equalTo("id", ride.id).findFirst()
            realmRide!!.deleteFromRealm()
        }
    }

    fun newRide(bike: Bike, startTime: String, location: String): Ride {
        val ride = Ride()
        ride.bike = bike
        ride.bikeName = bike.name
        ride.startLocation = bike.location
        ride.endLocation = location
        ride.startTime = startTime
        ride.endTime = Main.getDate()
        return ride
    }

    private fun newRideIndex(realm: Realm) : Long {
        val latestRide = realm.where<Ride>().sort("id", Sort.DESCENDING).findFirst()
        val index = latestRide?.id ?: 0
        return index + 1
    }
}