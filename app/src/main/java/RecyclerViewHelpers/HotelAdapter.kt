package RecyclerViewHelpers

import android.content.Intent
import android.provider.Settings.Global
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
<<<<<<< HEAD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
=======
import emily.jacobo.gostay.hotel_detalles
>>>>>>> origin/Leonardo
import modelo.tbHotel
import java.util.UUID

class HotelAdapter(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit): RecyclerView.Adapter<ViewHolderHotel>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {


        //holder.tbToogleFavoritos.setOnCheckedChangeListener { buttonView, isChecked ->
            //Si el corazoncito esta lleno
            //if (isChecked){
                GlobalScope.launch(Dispatchers.Main){

                   // val objConexion = ClaseConexion().cadenaConexion()

                    //2- Crear una variable que sea igual a un PrepareStatement
                   // val agregarFavoritos = objConexion?.prepareStatement("insert into tbPreferenciales where id_preferencial = ?, id_hoteles = ?, id_usuario = ?) values(?, ?, ?)")!!
                    //agregarFavoritos.setInt(1, txtPrecio.text.toString().toInt())

                }
           // } else{

          //  }
       // }

        val itemHotel = Datos[position]
        holder.bind(itemHotel, clickListener)

        Glide.with(holder.itemView)
            .load(itemHotel.img_url)
            .into(holder.imgHotelCard)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, hotel_detalles::class.java).apply {
                putExtra("id_hoteles", itemHotel.id_hoteles)
                putExtra("hotel", itemHotel)
            }
            context.startActivity(intent)
        }
    }
}