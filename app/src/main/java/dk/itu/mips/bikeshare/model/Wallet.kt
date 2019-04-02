package dk.itu.mips.bikeshare.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Wallet (
    @PrimaryKey
    var id: Long = 0,
    var money: Long = 0
) : RealmObject()