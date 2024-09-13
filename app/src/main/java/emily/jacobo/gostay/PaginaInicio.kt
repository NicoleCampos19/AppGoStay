package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel

class PaginaInicio : AppCompatActivity() {

    companion object {
        var hotelIdGlobal: Int? = null
        var nombreUsuarioGlobalL: String? = null
        var idUsuarioGlobalL: Int? = null
    }

    val correUsuarioRecivido  = activity_iniciar_sesion.txtCorreoInciarSesionV

    // Variable SQL global
    var sql: String = "SELECT * FROM tbHoteles"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_inicio)

        // Configuración del RecyclerView
        val rcvHotel = findViewById<RecyclerView>(R.id.rcvHotel)
        rcvHotel.layoutManager = LinearLayoutManager(this)

        // Cargar los hoteles inicialmente
        cargarHoteles(sql)

        // Configuración de los botones de navegación
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscar)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)
        val txtAggBusquedad = findViewById<TextView>(R.id.txtAggBusquedad)
        val imgFiltro = findViewById<ImageButton>(R.id.imgFiltros)

        if (correUsuarioRecivido != null) {
            obtenerNombreUsuarioEnGl(correUsuarioRecivido)
            obteneridUsuarioEnGl(correUsuarioRecivido)
        }

        imgFiltro.setOnClickListener {
            showBottomSheet()
        }

        txtAggBusquedad.setOnClickListener {
            val siguientepantalla = Intent(this, opcionesdebusquedad::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }
    }

    //buscar nombre usuario
    private fun obtenerNombreUsuarioEnGl(correoUsuario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreUsuario = cargarNombreUsuario(correoUsuario)
            withContext(Dispatchers.Main) {

                nombreUsuarioGlobalL = nombreUsuario

            }
        }
    }
    private fun cargarNombreUsuario(correoUsuario: String): String? {
        var nombreUsuario: String? = null
        val conexion = ClaseConexion().cadenaConexion()

        val query = """
        SELECT nombre_usuario FROM tbUsuarios WHERE correo = ?
    """
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

    //buscar id usuario
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

        val query = """
        SELECT id_usuario FROM tbUsuarios WHERE correo = ?
    """
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
        val thumbDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circulo_thumb, null)

        thumbDrawable?.let {
            rangeSlider.setCustomThumbDrawable(it)
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    // Método para construir la consulta SQL basada en los filtros seleccionados
    private fun construirConsultaSQL(bottomSheetView: View): String {
        val sqlQuery = StringBuilder("""
        SELECT DISTINCT
            h.id_hoteles, 
            h.nombre, 
            h.descripcion, 
            h.direccion, 
            h.correo, 
            h.cantidad_habitaciones, 
            h.img_url, 
            u.id_usuario 
        FROM 
            tbHoteles h 
        LEFT JOIN 
            tbUsuarios u ON h.id_usuario = u.id_usuario
        LEFT JOIN 
            tbIntermedia_Hoteles_Servicios ihs ON h.id_hoteles = ihs.id_hoteles
        LEFT JOIN 
            tbServiciosHotel sh ON ihs.id_servicio_hotel = sh.id_servicio_hotel
        LEFT JOIN 
            tbIntermedia_Hoteles_TipoHabitacion iht ON h.id_hoteles = iht.id_hoteles
        LEFT JOIN 
            tbTiposHabitaciones th ON iht.id_tipo_habitacion = th.id_tipo_habitacion
        LEFT JOIN 
            tbServiciosHabitacion sha ON sha.id_tipo_habitacion = th.id_tipo_habitacion
        WHERE 1=1
    """.trimIndent())

        // Filtro por rango de precio de la habitación
        val rangeSlider = bottomSheetView.findViewById<RangeSlider>(R.id.rangeSlider)
        val minValue = rangeSlider.values[0]
        val maxValue = rangeSlider.values[1]

        if (minValue != 0f || maxValue != 0f) {
            sqlQuery.append(" AND th.precio_habitacion BETWEEN $minValue AND $maxValue")
        }

        // Filtro basado en servicios de hotel (CheckBox)
        val serviciosHotel = mutableListOf<Int>()
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbPetfriendly).isChecked) serviciosHotel.add(1)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbWifigratis).isChecked) serviciosHotel.add(2)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbRestaurantes).isChecked) serviciosHotel.add(3)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbParqueo).isChecked) serviciosHotel.add(4)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbPiscina).isChecked) serviciosHotel.add(5)

        if (serviciosHotel.isNotEmpty()) {
            sqlQuery.append(" AND (sh.id_servicio_hotel IN (${serviciosHotel.joinToString(",")}))")
        }

        // Filtro basado en servicios de habitación (CheckBox)
        val serviciosHabitacion = mutableListOf<Int>()
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbAC).isChecked) serviciosHabitacion.add(1)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbTV).isChecked) serviciosHabitacion.add(2)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbVistaMar).isChecked) serviciosHabitacion.add(3)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbJacuzzi).isChecked) serviciosHabitacion.add(4)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbCafe).isChecked) serviciosHabitacion.add(5)

        if (serviciosHabitacion.isNotEmpty()) {
            sqlQuery.append(" AND (sha.id_servicio_habitacion IN (${serviciosHabitacion.joinToString(",")}))")
        }

        // Filtro basado en capacidad de habitación (CheckBox)
        val capacidades = mutableListOf<Int>()
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbCant1).isChecked) capacidades.add(1)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbCant2).isChecked) capacidades.add(2)
        if (bottomSheetView.findViewById<CheckBox>(R.id.cbCant4).isChecked) capacidades.add(4)

        if (capacidades.isNotEmpty()) {
            sqlQuery.append(" AND th.capacidad_habitacion IN (${capacidades.joinToString(",")})")
        }

        Log.e("Consulta", "$sqlQuery")
        return sqlQuery.toString()
    }
}