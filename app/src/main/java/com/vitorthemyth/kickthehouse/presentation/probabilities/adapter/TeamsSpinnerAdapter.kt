import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.vitorthemyth.components.R
import com.vitorthemyth.components.R.drawable.soccer_ball_item

class TeamSpinnerAdapter(
    context: Context,
    private val teams: List<String>,
    private var selectedTeam: String? = null
) : ArrayAdapter<String>(context, 0, teams) {

    private val layoutInflater = LayoutInflater.from(context)
    private var itemSpinnerView: View? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_spinner, parent, false)
        } else {
            view = convertView
        }
        getItem(position)?.let { team ->
            setItemForTeam(view, team)
        }
        itemSpinnerView = view
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (position == 0) {
            val tvTeam = itemSpinnerView?.findViewById<TextView>(R.id.tvTeam)
            val ivFlag = itemSpinnerView?.findViewById<ImageView>(R.id.ivFlag)
            tvTeam?.text = ""
            ivFlag?.setImageResource(0)
            view = layoutInflater.inflate(R.layout.item_spinner_header, parent, false)
            val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
            tvHeader.text = if (teams.isEmpty()) context.getString(com.vitorthemyth.kickthehouse.R.string.empty_list)
            else context.getString(com.vitorthemyth.kickthehouse.R.string.select_option)
            view.setBackgroundColor(Color.TRANSPARENT) // set the background to be transparent
            view.setOnClickListener {
                val root = parent.rootView
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        } else {
            view = layoutInflater.inflate(R.layout.item_drop_down, parent, false)
            getItem(position)?.let { team ->
                setItemForTeam(view, team)
            }
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            0 // for header
        } else {
            teams[position - 1].hashCode().toLong() // for other items
        }
    }

    override fun getItem(position: Int): String? {
        return if (position == 0) {
            null
        } else {
            teams[position - 1]
        }
    }

    override fun getCount() = teams.size + 1

    override fun isEnabled(position: Int) = position != 0

    fun setSelectedTeam(team: String?) {
        selectedTeam = team
        notifyDataSetChanged()
    }

    private fun setItemForTeam(view: View, team: String) {
        val tvTeam = view.findViewById<TextView>(R.id.tvTeam)
        val ivFlag = view.findViewById<ImageView>(R.id.ivFlag)
        tvTeam?.text = team
        ivFlag?.setImageResource(soccer_ball_item)
    }
}
