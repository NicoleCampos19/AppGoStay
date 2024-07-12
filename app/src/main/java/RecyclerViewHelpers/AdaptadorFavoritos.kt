package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel

class AdaptadorFavoritos (var Datos: List<tbFavoritos>, val clickListener: (tbFavoritos) -> Unit): RecyclerView.Adapter<ViewHolderFavoritos>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFavoritos {
        val vistaFav = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_fav, parent, false)
        return ViewHolderFavoritos(vistaFav)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderFavoritos, position: Int) {
        val item = Datos[position]

        val correoIngresado = activity_iniciar_sesion.correoIngresado

        //Función para que el recycleview se me actualice automáticamente
        fun actualizarRecyclerView(nuevaLista: List<tbFavoritos>){
            Datos = nuevaLista
            notifyDataSetChanged() //Notifica que hay datos nuevos
        }

        suspend fun obtenerIdUsuario(correo: String): Int? {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val getId =
                    objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")
                getId?.setString(1, correo)
                val resultSet = getId?.executeQuery()
                if (resultSet != null && resultSet.next()) {
                    resultSet.getInt("id_usuario")
                } else {
                    null
                }
            }
        }
        //Función para mostrar los datos
        fun obtenerFavoritos(): List<tbFavoritos> {

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbPreferenciales")!!
            val tbFavoritos = mutableListOf<tbFavoritos>()

            //Recorrer todos los datos que me trajo el select
            while (resultSet.next()){
                val id_preferenciales = resultSet.getInt("id_preferenciales")
                val id_hoteles = resultSet.getInt("id_hoteles")
                val id_usuario = resultSet.getInt("id_usuario")
                val favorito = tbFavoritos(id_preferenciales, id_hoteles, id_usuario)
                tbFavoritos.add(favorito)
            }
            return tbFavoritos
        }

        //Ejecutamos la función
        CoroutineScope(Dispatchers.IO).launch {
            val ejecutarFuncion = obtenerFavoritos()

            withContext(Dispatchers.Main){
                //Asigno el adaptador mi RecyclerView
                //(Uno mi Adaptador con el RecyclerView)
               // val miAdaptador = AdaptadorFavoritos(ejecutarFuncion)
               //rvcFavoritos.AdaptadorFavoritos = miAdaptador
            }
        }
    }
}