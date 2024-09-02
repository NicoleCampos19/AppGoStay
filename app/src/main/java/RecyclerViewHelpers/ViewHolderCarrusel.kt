package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderCarrusel (view: View):RecyclerView.ViewHolder(view){

    val imageView = view.findViewById<ImageView>(R.id.imageView)
}