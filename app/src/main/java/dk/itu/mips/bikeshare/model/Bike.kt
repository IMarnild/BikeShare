package dk.itu.mips.bikeshare.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Bike (
    @PrimaryKey
    var id: Long = 0,
    var name: String? = null,
    var location: String? = null,
    var pricePerHour: Double = 0.0,
    var photo: ByteArray? = ByteArray(0),
    var available: Boolean = true,
    var text: String? = "hello"
) : RealmObject()