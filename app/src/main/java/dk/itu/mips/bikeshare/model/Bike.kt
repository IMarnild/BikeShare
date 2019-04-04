package dk.itu.mips.bikeshare.model

import android.graphics.Bitmap
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

@RealmClass
open class Bike(
    @PrimaryKey
    var id: Long = 0,
    var name: String? = null,
    var location: String? = null,
    var price: String? = null,
    var photo: ByteArray? = ByteArray(0),
    var available: Boolean = true
) : RealmObject() {

    override fun toString(): String {
        return "" + this.id + ": " + this.name
    }
}