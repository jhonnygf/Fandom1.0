package edu.jonathangf.fandom

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.jonathangf.fandom.databinding.ActivityDetailBinding
import edu.jonathangf.fandom.model.Fandom
import edu.jonathangf.fandom.utils.updateFilesOptions

/**
 * Class DetailActivity.kt
 * The DetailActivity class is an Activity in which the attributes of each object are shown
 * in more detail. In it we can access the URL of an object, modify its favorite attribute and view
 * its image and description.
 * @author Jonathan Gómez Fraile
 */

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var fandom: Fandom

    companion object {
        const val EXTRA_FANDOM = "extra_fandom"

        /**
         * Class DetailActivity.kt
         * Method that allows the navigability of an Activity view to its Detail view, the
         * Activity and the object are passed as parameters to access its attributes
         * @author Jonathan Gómez Fraile
         *
         * @param activity The current activity from which the navigation is performed.
         * @param fandom The Fandom object that contains the data to display in the DetailActivity.
         */

        fun navigateToDetail(activity: AppCompatActivity, fandom: Fandom) {

            activity.startActivity(Intent(activity, DetailActivity::class.java).apply {
                putExtra(EXTRA_FANDOM, fandom)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

    /**
     * Class DetailActivity.kt
     * Handles selection of options menu items.
     * @author Jonathan Gómez Fraile
     *
     * @param item The selected menu item.
     * @return True if the event was consumed here, or the result of the superclass if not.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Class DetailActivity.kt
     * Method called when the activity is created for the first time. Set the view, recover the
     * object, shows the interface with the object data such as description and image, allows
     * modify the boolean value and allow access to the url with the most content of the object
     * @author Jonathan Gómez Fraile
     *
     * @param savedInstanceState If the activity is being re-initialized after having been
     * previously destroyed, This Bundle contains the most recent data supplied in
     * onSaveInstanceState. Otherwise it is null
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fandom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_FANDOM, Fandom::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_FANDOM)
        } ?: return finish()

        fandomData()
        modifyFavorite()
        accessTheUrl()
    }

    /**
     * Class DetailActivity.kt
     * Method to load the object information view in the detail view.
     * @author Jonathan Gómez Fraile
     */

    private fun fandomData() {
        binding.mToolbar.title = fandom.name
        binding.tvDescription.text = fandom.description
        binding.txtUniverse.text = fandom.universe

        Glide.with(this)
            .load(fandom.image)
            .fitCenter()
            .transform(RoundedCorners(16))
            .into(binding.ivDetail)
    }

    /**
     * Class DetailActivity.kt
     * Method to modify the boolean value of the object and add it to the list of the corresponding
     * file.
     * @author Jonathan Gómez Fraile
     */

    private fun modifyFavorite() {
        updateFavoriteIcon(fandom.fav)

        binding.ibFav.setOnClickListener {
            fandom.fav = !fandom.fav
            updateFilesOptions(this, R.string.filenameFavs, fandom.id, fandom.fav)
            updateFavoriteIcon(fandom.fav)
        }
    }

    /**
     * Class DetailActivity.kt
     * Method to update the favorite icon associated with the object.
     * @author Jonathan Gómez Fraile
     */

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.ibFav.setImageState(intArrayOf(R.attr.state_fav_on), isFavorite)
    }

    /**
     * Class DetailActivity.kt
     * Method to access a url associated with the object.
     * @author Jonathan Gómez Fraile
     */

    private fun accessTheUrl() {
        binding.tvUrlDetail.text = getString(R.string.txt_more_info, fandom.info)
        binding.tvUrlDetail.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fandom.info))
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    "No se encontró una aplicación para abrir este enlace",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

