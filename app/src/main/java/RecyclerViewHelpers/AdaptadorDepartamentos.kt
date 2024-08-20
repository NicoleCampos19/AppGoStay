package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbDepartamentos

class AdaptadorDepartamentos (var Datos : List<tbDepartamentos>): RecyclerView.Adapter<ViewHolderDepartamento>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDepartamento {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_departamento, parent, false)
        return ViewHolderDepartamento(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderDepartamento, position: Int) {

        val item = Datos[position]
    }
}