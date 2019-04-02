package dk.itu.mips.bikeshare.model

import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class RideRealm(val realm: Realm) {

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

    private fun newRideIndex(realm: Realm) : Long {
        val latestRide = realm.where<Ride>().sort("id", Sort.DESCENDING).findFirst()
        val index = latestRide?.id ?: 0
        return index + 1
    }
}