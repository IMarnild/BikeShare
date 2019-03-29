package dk.itu.mips.bikeshare.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass
open class Ride () : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var location_start: String? = null
    var location_end: String? = null
    var time_start: String? = null
    var time_end: String? = null
    var bike: Bike? = null
    var bikeName: String? = null
}