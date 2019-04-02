package dk.itu.mips.bikeshare.model

import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class BikeRealm(val realm: Realm) {

    fun create(bike: Bike): Long {
        this.realm.executeTransaction { r ->
            bike.id = this.newBikeIndex(realm)
            realm.insertOrUpdate(bike)
        }
        return bike.id
    }

    fun read(id: Long): Bike? {
        return this.realm.where<Bike>().equalTo("id", id).findFirst()
    }

    fun update(bike: Bike): Long {
        val realmBike = realm.where<Bike>().equalTo("id", bike.id).findFirst()
        if (realmBike != null) {
            this.realm.executeTransaction {
                this.realm.insertOrUpdate(bike)
            }
        }
        return bike.id
    }

    fun delete(bike: Bike) {
        this.realm.executeTransaction {
            val realmBike = realm.where<Bike>().equalTo("id", bike.id).findFirst()
            realmBike!!.deleteFromRealm()
        }
    }

    private fun newBikeIndex(realm: Realm) : Long {
        val latestBike = realm.where<Bike>().sort("id", Sort.DESCENDING).findFirst()
        val index = latestBike?.id ?: 0
        return index + 1
    }
}