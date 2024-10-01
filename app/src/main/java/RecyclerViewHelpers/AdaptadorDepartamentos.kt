package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbDepartamentos

// Adaptador para un RecyclerView que muestra una lista de departamentos
class AdaptadorDepartamentos (var Datos : List<tbDepartamentos>): RecyclerView.Adapter<ViewHolderDepartamento>() {
    // Crea y devuelve un ViewHolder cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDepartamento {
        // Infla el layout para cada elemento en el RecyclerView
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_departamento, parent, false)
        return ViewHolderDepartamento(vista)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderDepartamento, position: Int) {
        val item = Datos[position]
    }
}