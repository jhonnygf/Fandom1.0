package edu.jonathangf.fandom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.jonathangf.fandom.R
import edu.jonathangf.fandom.databinding.ItemListBinding
import edu.jonathangf.fandom.model.Fandom

/**
 * Class FandomAdapter.kt
 * FandomAdapter class is the adapter that provides the view of the RecyclerView objects and
 * links the object data with that of the RecyclerView object list.
 * @author Jonathan Gómez Fraile
 */
class FandomAdapter(
    val fandomList:
    MutableList<Fandom>,
    private val listenerFav: (pos: Int) -> Unit,
    private val delFandom: (pos: Int) -> Unit,
    private val showFandom: (pos: Int) -> Unit
) : RecyclerView.Adapter<FandomAdapter.FanViewHolder>() {

    /**
     * Class FandomAdapter.kt
     * Creates a new ViewHolder to represent a list item.
     * @author Jonathan Gómez Fraile
     *
     * @param parent The parent ViewGroup to which the new view will be added.
     * @param viewType The view type of the new ViewHolder.
     * @return A new FanViewHolder instance with the views initialized.
     */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FanViewHolder {

        val binding = ItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FanViewHolder(binding)
    }

    /**
     * Class FandomAdapter.kt
     * Binds data from a Fandom object to the ViewHolder's views.
     * @author Jonathan Gómez Fraile
     *
     * @param holder The ViewHolder that should be updated with the contents of the element at the given position.
     * @param position The position of the element within the adapter data set.
     */

    override fun onBindViewHolder(holder: FanViewHolder, position: Int) {
        holder.bind(fandomList[position], position)
    }

    /**
     * Class FandomAdapter.kt
     * Method to obtain the size of the list
     * @author Jonathan Gómez Fraile
     *
     * @return The size of the fandomList list.
     */
    override fun getItemCount(): Int = fandomList.size

    /**
     * Class FandomAdapter.kt
     * Custom ViewHolder to render each fandom list item.
     * @author Jonathan Gómez Fraile
     *
     * @property bindingFandom The binding object generated for each element's layout.
     */

    inner class FanViewHolder(
        private val bindingFandom: ItemListBinding
    ) : RecyclerView.ViewHolder(bindingFandom.root) {

        /**
         * Class FandomAdapter.kt
         * Method that links an object and its events to the view
         * @author Jonathan Gómez Fraile
         *
         * @param fandom The Fandom object containing the data to display.
         * @param position The position of the element in the list.
         */

        fun bind(fandom: Fandom, position: Int) {
            bindingFandom.tvName.text = fandom.name
            bindingFandom.tvUniverse.text = fandom.universe
            updateFavoriteIcon(fandom.fav)

            Glide.with(itemView)
                .load(fandom.image)
                .centerCrop()
                .circleCrop()
                .into(bindingFandom.ivItem)

            bindingFandom.root.setOnClickListener {
                showFandom(adapterPosition)
            }

            bindingFandom.root.setOnLongClickListener {
                delFandom(adapterPosition)
                true
            }

            bindingFandom.btnFavorite.setOnClickListener {
                fandom.fav = !fandom.fav
                updateFavoriteIcon(fandom.fav)
                listenerFav(adapterPosition)
                notifyItemChanged(adapterPosition)
            }
        }

        /**
         * Class FandomAdapter.kt
         * Method to modify the icon image depending on the state of the boolean
         * @author Jonathan Gómez Fraile
         *
         * @param isFavorite True if the fandom is favorited, False otherwise.
         */

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            bindingFandom.btnFavorite.setImageState(
                intArrayOf(if (isFavorite) R.attr.state_fav_on else -R.attr.state_fav_on),
                true
            )
        }
    }
}