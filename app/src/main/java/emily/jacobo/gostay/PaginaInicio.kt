package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel
import java.sql.SQLException

class PaginaInicio : AppCompatActivity() {

    // Estas variables dentro del companion object son globales y pueden ser accedidas
// desde cualquier parte del código sin necesidad de una instancia de la clase.
    companion object {
        var hotelIdGlobal: Int? = null
        var nombreUsuarioGlobalL: String? = null
        var idUsuarioGlobalL: Int? = null
    }

    // `correoIngresado` es una variable que se inicializa más adelante (lateinit)
    lateinit var correoIngresado: String

    // Variable SQL global
    var sql: String = "SELECT * FROM tbHoteles"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_inicio)

        // Recuperar el correo del usuario desde SharedPreferences
        val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        correoIngresado = userPreferences.getString("email", "") ?: ""

        if (correoIngresado.isNotEmpty()) {
            // Cargar nombre de usuario y ID
            obtenerNombreUsuarioEnGl(correoIngresado)
            obteneridUsuarioEnGl(correoIngresado)
            cargarImagenperfil(correoIngresado)
        } else {
            // Si no se encuentra el correo, redirigir al login
            val intent = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración del RecyclerView
        val rcvHotel = findViewById<RecyclerView>(R.id.rcvHotel)
        rcvHotel.layoutManager = LinearLayoutManager(this)

        // Cargar los hoteles inicialmente
        cargarHoteles(sql)

        // Configuración de los botones de navegación
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscar)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvFotoPerfil = findViewById<ImageView>(R.id.imvPerfilInicio)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)
        val txtAggBusquedad = findViewById<TextView>(R.id.txtAggBusquedad)
        val imgFiltro = findViewById<ImageButton>(R.id.imgFiltros)

        imgFiltro.setOnClickListener {
            showBottomSheet()
        }

        //Para poder ir a opcionesdebusquedad
        txtAggBusquedad.setOnClickListener {
            val siguientepantalla = Intent(this, opcionesdebusquedad::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        //Para poder ir a PaginaInicio
        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        //Para poder ir a Favoritos
        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        //Para poder ir a Reservas
        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        //Para poder ir a Perfil
        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }
    }

    // Método para cargar la imagen de perfil del usuario
    private fun cargarImagenperfil(correoIngresado: String) {
        val imvFotoPerfil = findViewById<ImageView>(R.id.imvPerfilInicio)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conexion = ClaseConexion().cadenaConexion()
                val query = "SELECT imgFoto FROM tbUsuarios WHERE correo = ?"
                val preparedStatement = conexion!!.prepareStatement(query)
                preparedStatement.setString(1, correoIngresado)

                val resultSet = preparedStatement.executeQuery()
                if (resultSet.next()) {
                    val imgFotoUrl = resultSet.getString("imgFoto")
                    withContext(Dispatchers.Main) {
                        Glide.with(this@PaginaInicio)
                            .load(imgFotoUrl)
                            .apply(RequestOptions().circleCrop())
                            .into(imvFotoPerfil)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PaginaInicio, "No se encontró la imagen de perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                resultSet.close()
                preparedStatement.close()
                conexion.close()
            } catch (e: SQLException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PaginaInicio, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Método para obtener el nombre del usuario
    private fun obtenerNombreUsuarioEnGl(correoUsuario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreUsuario = cargarNombreUsuario(correoUsuario)
            withContext(Dispatchers.Main) {
                nombreUsuarioGlobalL = nombreUsuario
            }
        }
    }

    // Para cargar el nombre del usuario
    private fun cargarNombreUsuario(correoUsuario: String): String? {
        var nombreUsuario: String? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = "SELECT nombre_usuario FROM tbUsuarios WHERE correo = ?"
        val statement = conexion?.prepareStatement(query)
        statement?.setString(1, correoUsuario)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            nombreUsuario = resultSet.getString("nombre_usuario")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return nombreUsuario
    }

    // Método para obtener el ID del usuario
    private fun obteneridUsuarioEnGl(correoUsuario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val idUsuario = cargaridUsuario(correoUsuario)
            withContext(Dispatchers.Main) {
                idUsuarioGlobalL = idUsuario
            }
        }
    }

    private fun cargaridUsuario(correoUsuario: String): Int? {
        var idUsuario: Int? = null
        val conexion = ClaseConexion().cadenaConexion()

        val query = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"
        val statement = conexion?.prepareStatement(query)
        statement?.setString(1, correoUsuario)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            idUsuario = resultSet.getInt("id_usuario")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return idUsuario
    }

    // Método para cargar hoteles con la consulta SQL proporcionada
    private fun cargarHoteles(sqlQuery: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val hotelesDB = obtenerHoteles(sqlQuery)
            withContext(Dispatchers.Main) {
                val adapter = HotelAdapter(hotelesDB, false) { hotel ->
                    hotelIdGlobal = hotel.id_hoteles
                    val intent = Intent(this@PaginaInicio, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                findViewById<RecyclerView>(R.id.rcvHotel).adapter = adapter
            }
        }
    }

    // Método para obtener hoteles con la consulta SQL personalizada
    private fun obtenerHoteles(sqlQuery: String): List<tbHotel> {
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.createStatement()
        val resultSet = statement?.executeQuery(sqlQuery)!!

        val listaHoteles = mutableListOf<tbHotel>()
        while (resultSet.next()) {
            val id_hoteles = resultSet.getInt("id_hoteles")
            val nombre = resultSet.getString("nombre")
            val descripcion = resultSet.getString("descripcion")
            val direccion = resultSet.getString("direccion")
            val correo = resultSet.getString("correo")
            val cantidad_habitaciones = resultSet.getInt("cantidad_habitaciones")
            val img_url = resultSet.getString("img_url")
            val id_usuario = resultSet.getInt("id_usuario")

            val hotel = tbHotel(id_hoteles, nombre, descripcion, direccion, correo, cantidad_habitaciones, img_url, id_usuario)
            listaHoteles.add(hotel)
        }
        resultSet.close()
        statement.close()
        objConexion.close()

        return listaHoteles
    }

    // Método para mostrar el BottomSheet
    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.behavior.isDraggable = false

        bottomSheetDialog.setContentView(bottomSheetView)

        val buttonClose: ImageView? = bottomSheetView.findViewById(R.id.buttonClose)
        buttonClose?.setOnClickListener {
            sql = "SELECT * FROM tbHoteles"
            cargarHoteles(sql)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnShowListener {
            val bottomSheetInternal = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                sheet.layoutParams.height = (resources.displayMetrics.heightPixels * 0.99).toInt()
                sheet.requestLayout()
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        // Aplicar filtros cuando se hace clic en el botón Aplicar
        val buttonAplicarFiltros = bottomSheetView.findViewById<Button>(R.id.btnAplicar)
        buttonAplicarFiltros.setOnClickListener {
            sql = construirConsultaSQL(bottomSheetView)
            cargarHoteles(sql)
            bottomSheetDialog.dismiss()
        }


        val rangeSlider = bottomSheetView.findViewById<RangeSlider>(R.id.rangeSlider)
        val thumbDrawable = ContextCompat.getDrawable(this, R.drawable.circulo_thumb)

        thumbDrawable?.let {
            rangeSlider.setCustomThumbDrawable(it)
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    // Método para construir la consulta SQL basada en los filtros seleccionados
    private fun construirConsultaSQL(bottomSheetView: View): String {
        var sqlQuery = """
SELECT 
    h.id_hoteles, 
    h.nombre , 
    h.descripcion, 
    h.direccion, 
    h.correo, 
    h.cantidad_habitaciones, 
    h.img_url,
    u.id_usuario
FROM 
    tbFiltros f
INNER JOIN 
    tbHoteles h ON f.id_hoteles = h.id_hoteles
INNER JOIN 
    tbTiposHabitaciones sa ON f.id_tipo_habitacion = sa.id_tipo_habitacion
INNER JOIN 
    tbServiciosHotel sh ON f.id_servicio_hotel = sh.id_servicio_hotel
INNER JOIN 
    tbServiciosHabitacion ha ON f.id_servicio_habitacion = ha.id_servicio_habitacion
INNER JOIN 
    tbUsuarios u ON h.id_usuario = u.id_usuario
WHERE 
    1=1
        """.trimIndent()

        // Filtro basado en la selección de servicios hotel
        val cbPetfriendly = bottomSheetView.findViewById<CheckBox>(R.id.cbPetfriendly)
        if (cbPetfriendly.isChecked) {
            sqlQuery += " AND sh.id_servicio_hotel = 1"
        }

        val cbWifigratis = bottomSheetView.findViewById<CheckBox>(R.id.cbWifigratis)
        if (cbWifigratis.isChecked) {
            sqlQuery += " OR sh.id_servicio_hotel = 2"
        }

        val cbRestaurantes = bottomSheetView.findViewById<CheckBox>(R.id.cbRestaurantes)
        if (cbRestaurantes.isChecked){
            sqlQuery += " OR sh.id_servicio_hotel = 3"
        }

        val cbParqueo = bottomSheetView.findViewById<CheckBox>(R.id.cbParqueo)
        if (cbParqueo.isChecked){
            sqlQuery += " OR sh.id_servicio_hotel = 4"
        }

        val cbPiscina = bottomSheetView.findViewById<CheckBox>(R.id.cbPiscina)
        if (cbPiscina.isChecked){
            sqlQuery += " OR sh.id_servicio_hotel = 5"
        }

        // Filtro basado en la selección de servicios habitación
        val cbAC = bottomSheetView.findViewById<CheckBox>(R.id.cbAC)
        if (cbAC.isChecked){
            sqlQuery += " AND ha.id_servicio_habitacion = 1"
        }

        val cbTV = bottomSheetView.findViewById<CheckBox>(R.id.cbTV)
        if (cbTV.isChecked){
            sqlQuery += " AND ha.id_servicio_habitacion = 2"
        }

        val cbVistaMar = bottomSheetView.findViewById<CheckBox>(R.id.cbVistaMar)
        if (cbVistaMar.isChecked){
            sqlQuery += " OR ha.id_servicio_habitacion = 3"
        }

        val cbJacuzzi = bottomSheetView.findViewById<CheckBox>(R.id.cbJacuzzi)
        if (cbJacuzzi.isChecked){
            sqlQuery += " AND ha.id_servicio_habitacion = 4"
        }

        val cbCafe = bottomSheetView.findViewById<CheckBox>(R.id.cbCafe)
        if (cbCafe.isChecked){
            sqlQuery += " OR ha.id_servicio_habitacion = 5"
        }

        // Filtro basado en la selección de capacidad de personas
        val cbCant2 = bottomSheetView.findViewById<CheckBox>(R.id.cbCant2)
        if (cbCant2.isChecked){
            sqlQuery += " AND sa.capacidad_habitacion = 2"
        }

        val cbCant4 = bottomSheetView.findViewById<CheckBox>(R.id.cbCant4)
        if (cbCant4.isChecked){
            sqlQuery += " AND sa.capacidad_habitacion = 4"
        }

        val cbCant1 = bottomSheetView.findViewById<CheckBox>(R.id.cbCant1)
        if (cbCant1.isChecked){
            sqlQuery += " AND sa.capacidad_habitacion = 1"
        }

        // Filtro basado en el precio de habitacion
        val rangeSlider = bottomSheetView.findViewById<RangeSlider>(R.id.rangeSlider)
        val values = rangeSlider.values
        val minValue = values[0]
        val maxValue = values[1]

        if (minValue != 0f || maxValue != 0f) {
            sqlQuery += " AND sa.precio_habitacion BETWEEN $minValue AND $maxValue"
        }
        return sqlQuery
    }
}