package edu.jonathangf.fandom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class DetailActivity.kt
 * Data class Fandom represents the objects of the application, it only contains attributes without
 * any methods.
 * @author Jonathan Gómez Fraile
 */

@Parcelize
data class Fandom(
    var id: Int,
    var name: String,
    var universe: String,
    var description: String,
    var image: String,
    var info: String,
    var fav: Boolean = false,
    var visible: Boolean = true
) : Parcelable
