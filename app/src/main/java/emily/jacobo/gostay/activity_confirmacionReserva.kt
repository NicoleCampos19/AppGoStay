package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorOfertas
import RecyclerViewHelpers.AdaptorTipoHabitacion
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import emily.jacobo.gostay.PaginaInicio.Companion.idUsuarioGlobalL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel
import java.lang.reflect.Array.setInt
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class activity_confirmacionReserva : AppCompatActivity() {

    companion object {
        lateinit var direccionHotelGlobal: String
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_reserva)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_confirmacion_reserva)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Se manda a llamar los elementos de la vista
        val btnConfirmar = findViewById<TextView>(R.id.btnConfirmar)

        // Asignar el ID del hotel desde una variable global que se obtiene de la página de inicio
        val idHotelRecivido = PaginaInicio.hotelIdGlobal
        // Asignar el ID del tipo de habitación desde una variable global del adaptador de tipos de habitación
        val idTipoHabitacionRecivido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
        // Asignar la cantidad de habitaciones desde una variable global de la actividad de reserva
        val cantidadHabitaciones = activity_reserva.cantidadHabitaciones
        // Obtener el CVV de la tarjeta desde la actividad de reserva
        val cvv = activity_reserva.cvv
        // Obtener la fecha de caducidad de la tarjeta desde la actividad de reserva
        val fechaCaducidad = activity_reserva.fechaCaducidad
        // Obtener el número de la tarjeta de crédito desde la actividad de reserva
        val numeroTarjeta = activity_reserva.numeroTarjeta
        // Obtener el nombre del titular de la tarjeta desde la actividad de reserva
        val nombreTitular = activity_reserva.nombreTitular
        // Obtener la fecha de entrada de la reserva desde la actividad de reserva
        val fechaEntrada = activity_reserva.fechaEntrada
        // Obtener la fecha de salida de la reserva desde la actividad de reserva
        val fechaSalida = activity_reserva.fechaSalida
        // Asignar el ID del usuario desde una variable global obtenida en la página de inicio
        val idUsuario = idUsuarioGlobalL
        // Obtener el ID del departamento (probablemente relacionado con el destino o tipo de habitación) desde la actividad de reserva
        val idDepartamento = activity_reserva.idDepartamento
        // Asignar el nombre del usuario desde una variable global obtenida en la página de inicio
        val nombreUsuario = PaginaInicio.nombreUsuarioGlobalL

        // Mostrar en el log los datos recibidos (departamento, usuario, tipo de habitación)
        Log.d("ConfirmacionDepartamento", "Departamento recibido: $idDepartamento")
        Log.d("ConfirmacionUsuario", "nombre del usuario recivido: $nombreUsuario")
        Log.d("ConfirmacionUsuario", "ID del usuario recivido: $idUsuario")
        Log.d("ConfirmacionTipoHabitacion", "ID del tipo habitacion recivido: $idTipoHabitacionRecivido")

        // Referencia al TextView que muestra el monto total
        val tvTotalAmount = findViewById<TextView>(R.id.tvTotalAmount)

        // Calcular la cantidad de días de estancia si las fechas no son nulas
        val diasEstancia = if (fechaEntrada != null && fechaSalida != null) {
            calcularDiasEstancia(fechaEntrada, fechaSalida)
        }else {
            0L // Si alguna fecha es nula, se asigna 0
        }

        // Obtener el precio de la habitación en un hilo secundario
        CoroutineScope(Dispatchers.IO).launch {
            val precioHabitacion = obtenerPrecioHabitacion(idTipoHabitacionRecivido)

            // Calcular el total con descuento y mostrarlo en el TextView en el hilo principal
            withContext(Dispatchers.Main) {
                val total = diasEstancia * precioHabitacion * cantidadHabitaciones!! // Multiplicar por cantidadHabitaciones
                val descuentoTotal = AdaptadorOfertas.descuentoTotalGlobal
                val totalDescuento = total * (1 - descuentoTotal / 100)
                tvTotalAmount.text = "$$totalDescuento + impuestos"
            }
        }
        // Mostrar las fechas de entrada y salida en los TextViews correspondientes
        findViewById<TextView>(R.id.tvEntradaDate).text = fechaEntrada
        findViewById<TextView>(R.id.tvSalidaDate).text = fechaSalida

       // Mando a llamar la img desde la vista
        val volverAtras = findViewById<ImageView>(R.id.imgVolverTrasxd)

        // Para poder volver atrás
        volverAtras.setOnClickListener {
            finish()
        }

// Verifica si el ID del hotel, el ID del tipo de habitación y el nombre de usuario son válidos
        if (idHotelRecivido != -1 && idTipoHabitacionRecivido != -1 && nombreUsuario != null) {
            // Llama a funciones para obtener la dirección, nombre del hotel y tipo de habitación
            obtenerDireccionHotelEnTv(idHotelRecivido ?: -1)
            obtenerNombreHotelEnTv(idHotelRecivido ?: -1)
            obtenerNombreTipoHabitacionEnTv(idTipoHabitacionRecivido ?: -1)

            // Asigna el nombre de usuario al TextView de la reserva
            findViewById<TextView>(R.id.tvReservaNombre).text = nombreUsuario
        }

        // Configura el botón "Confirmar" para que al hacer clic se ejecute la función de aceptar reserva
        btnConfirmar.setOnClickListener{
            aceptarReserva()
        }
    }

    // Función para poder aceptar la reserva
    private fun aceptarReserva() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@activity_confirmacionReserva)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_alerta_reserva)

            // Configurar los botones del diálogo personalizado
            val btnAceptar = dialog.findViewById<Button>(R.id.btnAceptarReserva)
            val btnNoAceptar = dialog.findViewById<Button>(R.id.btnNoAceptarReserva)

            // Configurar acción al presionar "Aceptar"
            btnAceptar.setOnClickListener {
                // Acción al presionar "Aceptar"
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val fechaEntrada1 = activity_reserva.fechaEntrada
                        val fechaSalida2 = activity_reserva.fechaSalida
                        val idTipoHabitacionRecivido2 = AdaptorTipoHabitacion.idTipoHabitacionGlobal


                        val precioHabitacion = obtenerPrecioHabitacion(idTipoHabitacionRecivido2)


                        val diasEstancia = if (fechaEntrada1 != null && fechaSalida2 != null) {
                            calcularDiasEstancia(fechaEntrada1, fechaSalida2)
                        }else {
                            0L // Valor predeterminado si alguna fecha es nula
                        }

                        // Obtener los valores a insertar
                        val idHotelRecibido = PaginaInicio.hotelIdGlobal
                        val idTipoHabitacionRecibido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
                        val cvv = activity_reserva.cvv
                        val fechaCaducidad = activity_reserva.fechaCaducidad
                        val numeroTarjeta = activity_reserva.numeroTarjeta
                        val nombreTitular = activity_reserva.nombreTitular
                        val totalI = diasEstancia * precioHabitacion
                        val descuentoTotal = AdaptadorOfertas.descuentoTotalGlobal
                        val cantidadHabitaciones = activity_reserva.cantidadHabitaciones
                        val totalDescuento = totalI * (1- descuentoTotal/100)
                        val fechaEntrada = activity_reserva.fechaEntrada
                        val fechaSalida = activity_reserva.fechaSalida
                        val idUsuario = idUsuarioGlobalL
                        val idDepartamento = activity_reserva.idDepartamento

                        // Verifica que los campos idHotelRecibido e idUsuario no sean nulos
                        if (idHotelRecibido == null || idUsuario == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@activity_confirmacionReserva, "Error: Datos incompletos.", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        // Realizar la inserción en la base de datos
                        val conexion = ClaseConexion().cadenaConexion()
                        val query = """
                        INSERT INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total,cantidad_habitaciones_reservadas, id_tipo_habitacion, id_departamento, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)
                    """
                        val statement = conexion?.prepareStatement(query)
                        statement?.apply {
                            setInt(1, idHotelRecibido)
                            setString(2, fechaEntrada) // Fecha de entrada
                            setString(3, fechaSalida)  // Fecha de salida
                            setString(4, numeroTarjeta)
                            setString(5, fechaCaducidad) // Fecha de caducidad
                            setString(6, nombreTitular)
                            setInt(7, cvv ?: 0) // Si cvv es null, se asume 0
                            setDouble(8, totalDescuento)
                            setInt(9, cantidadHabitaciones ?: 0) // Si cantidadHabitaciones es null, se asume 0
                            setInt(10, idTipoHabitacionRecibido)
                            setInt(11, idDepartamento ?: 0) // Si idDepartamento es null, se asume 0
                            setInt(12, idUsuario)
                            executeUpdate()
                        }

                        withContext(Dispatchers.Main) {
                            reservaHecha()
                            AdaptadorOfertas.descuentoTotalGlobal = 0.0
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@activity_confirmacionReserva, "Error al realizar la reserva", Toast.LENGTH_SHORT).show()
                        }
                        e.printStackTrace()
                    }
                }
                dialog.dismiss()
            }

            // Configurar acción al presionar "No Aceptar"
            btnNoAceptar.setOnClickListener {
                dialog.dismiss()
            }

            // Mostrar el diálogo personalizado
            dialog.show()
        }
    }

    // Función de cuando la reserva este realizada
    private fun reservaHecha() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@activity_confirmacionReserva)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_reserva_hecha)

            val btnClose = dialog.findViewById<Button>(R.id.btnDialogClose)
            btnClose.setOnClickListener {
                dialog.dismiss()

                // Redirigir a la pantalla de reservas después de cerrar el diálogo de éxito
                val intent = Intent(this@activity_confirmacionReserva, Reservas::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            dialog.show()
        }
    }

    // Función para calcular la diferencia de días entre dos fechas
    private fun calcularDiasEstancia(fechaEntrada: String, fechaSalida: String): Long {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val entrada = formatoFecha.parse(fechaEntrada)
        val salida = formatoFecha.parse(fechaSalida)
        val diff = salida.time - entrada.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    // Función para obtener el precio de la habitación desde la base de datos
    private fun obtenerPrecioHabitacion(idTipoHabitacion: Int): Double {
        var precioHabitacion = 0.0
        val conexion = ClaseConexion().cadenaConexion()

        val query = """
            SELECT precio_habitacion FROM tbTiposHabitaciones WHERE id_tipo_habitacion = ?
        """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idTipoHabitacion)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            precioHabitacion = resultSet.getDouble("precio_habitacion")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return precioHabitacion
    }

    // Función para mostrar la alerta de éxito
    private fun mostrarAlertaExito() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        builder.setMessage("Reserva realizada con éxito")
        builder.setIcon(R.drawable.ic_exitoso) // Asegúrate de tener un icono adecuado o puedes eliminar esta línea

        // Configurar botón "OK"
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

// Función para encontrar la dirección del hotel
    private fun obtenerDireccionHotelEnTv(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val direccionHotel = cargarDireccionHotel(idHotel)
            Log.d("ConfirmacionReserva", "Dirección del hotel: $direccionHotel")
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvHotelAddress).text = direccionHotel
            }
        }
    }

    // Función para cargar la dirección del hotel (es un select)
    private fun cargarDireccionHotel(idHotel: Int): String? {
        var direccionHotel: String? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = """
        SELECT direccion 
        FROM tbHoteles 
        WHERE id_hoteles = ?
    """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idHotel)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            direccionHotel = resultSet.getString("direccion")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return direccionHotel
    }
    // Obtener el nombre del hotel
    private fun obtenerNombreHotelEnTv(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreHotel = cargarNombreHotel(idHotel)
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvHotelName).text = nombreHotel
            }
        }
    }
    // Función para cargar el nombre del hotel (es un select)
    private fun cargarNombreHotel(idHotel: Int): String? {
        var nombreHotel: String? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = """
        SELECT nombre
        FROM tbHoteles 
        WHERE id_hoteles = ?
    """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idHotel)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            nombreHotel = resultSet.getString("nombre")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return nombreHotel
    }

    //obtener nombre del tipo de habitacion
    private fun obtenerNombreTipoHabitacionEnTv(idTipoHabitacion: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreTipoHabitacion = cargarNombreTipoHabitacion(idTipoHabitacion)
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvSeleccionDetails).text = nombreTipoHabitacion
            }
        }
    }

    // Para cargar el nombre del tipo de habitación (es un select)
    private fun cargarNombreTipoHabitacion(idTipoHabitacion: Int): String? {
        var nombreTipoHabitacion: String? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = """
        SELECT nombre_tipo_habitacion FROM tbTiposHabitaciones WHERE id_tipo_habitacion = ?
    """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idTipoHabitacion)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            nombreTipoHabitacion = resultSet.getString("nombre_tipo_habitacion")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return nombreTipoHabitacion
    }

    // Función para mostrar un diálogo personalizado
    private fun showCustomDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            // Crea un nuevo dialog
            val dialog = Dialog(this@activity_confirmacionReserva)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_denuncia_realizada)

            val btnClose = dialog.findViewById<Button>(R.id.btnDialogClose)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            // Muestra el diálogo en pantalla
            dialog.show()
        }
    }
}