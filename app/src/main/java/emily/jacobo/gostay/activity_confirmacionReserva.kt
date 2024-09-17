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
        val btnConfirmar = findViewById<TextView>(R.id.btnConfirmar)

        //recividos de verdad XD

        val idHotelRecivido = PaginaInicio.hotelIdGlobal
        val idTipoHabitacionRecivido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
        val cvv = activity_reserva.cvv
        val fechaCaducidad = activity_reserva.fechaCaducidad
        val numeroTarjeta = activity_reserva.numeroTarjeta
        val nombreTitular = activity_reserva.nombreTitular
        val fechaEntrada = activity_reserva.fechaEntrada
        val fechaSalida = activity_reserva.fechaSalida
        val idUsuario = idUsuarioGlobalL
        val idDepartamento = activity_reserva.idDepartamento

        val nombreUsuario = PaginaInicio.nombreUsuarioGlobalL
        Log.d("ConfirmacionDepartamento", "Departamento recibido: $idDepartamento")
        Log.d("ConfirmacionUsuario", "nombre del usuario recivido: $nombreUsuario")
        Log.d("ConfirmacionUsuario", "ID del usuario recivido: $idUsuario")
        Log.d("ConfirmacionTipoHabitacion", "ID del tipo habitacion recivido: $idTipoHabitacionRecivido")












        //mostrar
        val tvTotalAmount = findViewById<TextView>(R.id.tvTotalAmount)

        // Calcular la cantidad de días
        val diasEstancia = if (fechaEntrada != null && fechaSalida != null) {
            calcularDiasEstancia(fechaEntrada, fechaSalida)
        }else {
            0L // Valor predeterminado si alguna fecha es nula
        }

        // Obtener el precio de la habitación
        CoroutineScope(Dispatchers.IO).launch {
            val precioHabitacion = obtenerPrecioHabitacion(idTipoHabitacionRecivido)

            // Calcular el total y mostrarlo en el TextView
            withContext(Dispatchers.Main) {
                val total = diasEstancia * precioHabitacion
                val descuentoTotal = AdaptadorOfertas.descuentoTotalGlobal
                val totalDescuento = total * (1- descuentoTotal/100)
                tvTotalAmount.text = "$$totalDescuento + impuestos"
            }
        }
        findViewById<TextView>(R.id.tvEntradaDate).text = fechaEntrada
        findViewById<TextView>(R.id.tvSalidaDate).text = fechaSalida





       val volverAtras = findViewById<ImageView>(R.id.imgVolverTrasxd)

        volverAtras.setOnClickListener {
            finish()
        }

        if (idHotelRecivido != -1 && idTipoHabitacionRecivido != -1 && nombreUsuario != null) {
            obtenerDireccionHotelEnTv(idHotelRecivido ?: -1)
            obtenerNombreHotelEnTv(idHotelRecivido ?: -1)
            obtenerNombreTipoHabitacionEnTv(idTipoHabitacionRecivido ?: -1)
            findViewById<TextView>(R.id.tvReservaNombre).text = nombreUsuario
        }



        /*btnConfirmar.setOnClickListener {
            // Construir el AlertDialog inicial
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Estás seguro de realizar tu reserva?")
            builder.setMessage("No se podrá cancelar ni reembolsar la reserva una vez realizada.")

            // Configurar el botón "Sí"
            builder.setPositiveButton("Sí") { dialog, which ->
                // Acción al presionar "Sí"
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Obtener los valores a insertar
                        val idHotelRecibido = PaginaInicio.hotelIdGlobal
                        val idTipoHabitacionRecibido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
                        val cvv = activity_reserva.cvv
                        val fechaCaducidad = activity_reserva.fechaCaducidad
                        val numeroTarjeta = activity_reserva.numeroTarjeta
                        val nombreTitular = activity_reserva.nombreTitular
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

                        // Aquí realizas la inserción directamente
                        val conexion = ClaseConexion().cadenaConexion()
                        val query = """
                INSERT INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, id_tipo_habitacion, id_departamento, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
                        val statement = conexion?.prepareStatement(query)
                        statement?.apply {
                            setInt(1, idHotelRecibido)
                            setString(2, fechaEntrada) //tiene que ser date
                            setString(3, fechaSalida) // tiene que ser date
                            setString(4, numeroTarjeta)
                            setString(5, fechaCaducidad) // tiene que ser date
                            setString(6, nombreTitular)
                            setInt(7, cvv ?: 0) // Manejo de null, asume 0 si es null
                            setInt(8, idTipoHabitacionRecibido)
                            setInt(9, idDepartamento ?: 0) // Manejo de null, asume 0 si es null
                            setInt(10, idUsuario)
                            executeUpdate()

                        }


                        withContext(Dispatchers.Main) {
                            // Redirigir a PaginaInicio si la inserción fue exitosa
                            val intent = Intent(this@activity_confirmacionReserva, Reservas::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@activity_confirmacionReserva, "Error al realizar la reserva", Toast.LENGTH_SHORT).show()
                        }
                        e.printStackTrace()
                    }
                }
            }

            // Configurar el botón "No"
            builder.setNegativeButton("No") { dialog, which ->
                // Acción al presionar "No"
                dialog.dismiss() // Solo se cierra el diálogo
            }

            // Mostrar el diálogo
            val dialog = builder.create()
            dialog.show()
        }*/



        btnConfirmar.setOnClickListener{
            aceptarReserva()
        }


    }

    private fun aceptarReserva() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@activity_confirmacionReserva)
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
                        INSERT INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)
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
                            setInt(9, idTipoHabitacionRecibido)
                            setInt(10, idDepartamento ?: 0) // Si idDepartamento es null, se asume 0
                            setInt(11, idUsuario)
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
            }

            // Configurar acción al presionar "No Aceptar"
            btnNoAceptar.setOnClickListener {
                dialog.dismiss()
            }

            // Mostrar el diálogo personalizado
            dialog.show()
        }
    }

    private fun reservaHecha() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@activity_confirmacionReserva)
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

//encontrar direccion del hotel
    private fun obtenerDireccionHotelEnTv(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val direccionHotel = cargarDireccionHotel(idHotel)
            Log.d("ConfirmacionReserva", "Dirección del hotel: $direccionHotel")
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvHotelAddress).text = direccionHotel
            }
        }
    }
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


    //encontrar nombre del hotel
    private fun obtenerNombreHotelEnTv(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreHotel = cargarNombreHotel(idHotel)
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.tvHotelName).text = nombreHotel
            }
        }
    }
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







}