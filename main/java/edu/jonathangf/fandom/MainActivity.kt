package edu.jonathangf.fandom

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.snackbar.Snackbar
import edu.jonathangf.fandom.adapter.FandomAdapter
import edu.jonathangf.fandom.databinding.ActivityMainBinding
import edu.jonathangf.fandom.model.Fandom
import edu.jonathangf.fandom.utils.deleteFilesOptions
import edu.jonathangf.fandom.utils.readRawFile
import edu.jonathangf.fandom.utils.updateFilesOptions

/**
 * Class ActivityMain.kt
 * MainActivity class is the main class of the program, in this application a list of objects is
 * loaded from a file file, its images and information are displayed and it gives you the option
 * to add them as favorites or delete them from the list.
 * @author Jonathan Gómez Fraile
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fandomAdapter: FandomAdapter
    private var fandomList: MutableList<Fandom> = mutableListOf()

    /**
     * Class ActivityMain.kt
     * Method that initializes the application, loads the ActivityMain view, loads data from a
     * file using Utils, initializes the adapter and allows the list to be refreshed using
     * SwipeRefreshLayout
     * @author Jonathan Gómez Fraile
     *
     * @param savedInstanceState If the activity is being recreated after being previously
     * destroyed,This Bundle contains the most recent data supplied in onSaveInstanceState.
     * Otherwise, it is null.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        fandomList = readRawFile(this)
        fandomAdapter = FandomAdapter(
            fandomList = fandomList,
            listenerFav = { pos ->
                val fandom = fandomList[pos]
                updateFilesOptions(this, R.string.filenameFavs, fandom.id, fandom.fav)
                fandomAdapter.notifyItemChanged(pos)
            },
            delFandom = { pos ->
                val fandom = fandomAdapter.fandomList[pos]

                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.txt_delete, fandom.name),
                    Snackbar.LENGTH_LONG
                )

                snackbar.setAction(getString(R.string.txt_doit)) {
                    fandomAdapter.fandomList.removeAt(pos)
                    fandomAdapter.notifyItemRemoved(pos)

                    updateFilesOptions(this, R.string.filenameDeleted, fandom.id, true)
                }
                snackbar.show()
            },
            showFandom = { pos ->

                val selectedFandom = fandomList[pos]
                DetailActivity.navigateToDetail(this, selectedFandom)
            }
        )
        binding.mRecycler.adapter = fandomAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            deleteFilesOptions(this)
            onResume()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * Class ActivityMain.kt
     * Method that reloads and refreshes the display of the RecyclerView object list.
     * @author Jonathan Gómez Fraile
     */

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        fandomList.clear()
        fandomList.addAll(readRawFile(this))
        fandomAdapter.notifyDataSetChanged()
    }
}