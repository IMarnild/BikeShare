package dk.itu.mips.bikeshare.model

import dk.itu.mips.bikeshare.Main
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where

class BikeRealm {

    private val realm = Realm.getInstance(Main.getRealmConfig())

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

    fun read(): RealmResults<Bike> {
        return this.realm.where<Bike>().sort("id", Sort.ASCENDING).findAll()
    }

    fun update(bike: Bike): Long {
        val realmBike = realm.where<Bike>().equalTo("id", bike.id).findFirst()
        if (realmBike != null) {
            this.realm.executeTransaction {
                this.realm.copyToRealmOrUpdate(bike)
            }
        }
        return bike.id
    }

    fun updateLocation(id: Long, location: String) {
        realm.executeTransaction {
            val realmBike = realm.where<Bike>().equalTo("id", id).findFirst()
            realmBike!!.location = location
        }
    }

    fun delete(bike: Bike) {
        this.realm.executeTransaction {
            val realmBike = realm.where<Bike>().equalTo("id", bike.id).findFirst()
            realmBike!!.deleteFromRealm()
        }
    }

    fun toggleAvailability(bike: Bike) {
        this.realm.executeTransaction {
            val realmBike = realm.where<Bike>().equalTo("id", bike.id).findFirst()
            realmBike!!.available = !realmBike.available
        }
    }

    private fun newBikeIndex(realm: Realm) : Long {
        val latestBike = realm.where<Bike>().sort("id", Sort.DESCENDING).findFirst()
        val index = latestBike?.id ?: 0
        return index + 1
    }
}