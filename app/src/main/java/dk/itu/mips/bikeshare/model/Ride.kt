package dk.itu.mips.bikeshare.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass
open class Ride () : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var startLocation: String? = null
    var endLocation: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var bike: Bike? = null
    var bikeName: String? = null
    var cost: Double? = null
}