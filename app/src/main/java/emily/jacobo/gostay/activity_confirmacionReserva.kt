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
import emily.jacobo.gostay.activity_iniciar_sesion.variableGloalLogin.correoIngresado
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
import java.util.Properties
import java.util.concurrent.TimeUnit
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
            overridePendingTransition(0,0)
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
            overridePendingTransition(0,0)
        }
    }

    // Función para poder aceptar la reserva
    private fun aceptarReserva() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@activity_confirmacionReserva)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_alerta_reserva)

            val btnAceptar = dialog.findViewById<Button>(R.id.btnAceptarReserva)
            val btnNoAceptar = dialog.findViewById<Button>(R.id.btnNoAceptarReserva)

            btnAceptar.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val fechaEntrada1 = activity_reserva.fechaEntrada
                        val fechaSalida2 = activity_reserva.fechaSalida
                        val idTipoHabitacionRecivido2 = AdaptorTipoHabitacion.idTipoHabitacionGlobal
                        val precioHabitacion = obtenerPrecioHabitacion(idTipoHabitacionRecivido2)

                        val diasEstancia = if (fechaEntrada1 != null && fechaSalida2 != null) {
                            calcularDiasEstancia(fechaEntrada1, fechaSalida2)
                        } else {
                            0L
                        }

                        val idHotelRecibido = PaginaInicio.hotelIdGlobal
                        val idTipoHabitacionRecibido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
                        val cvv = activity_reserva.cvv
                        val fechaCaducidad = activity_reserva.fechaCaducidad
                        val numeroTarjeta = activity_reserva.numeroTarjeta
                        val nombreTitular = activity_reserva.nombreTitular
                        val totalI = diasEstancia * precioHabitacion
                        val descuentoTotal = AdaptadorOfertas.descuentoTotalGlobal
                        val cantidadHabitaciones = activity_reserva.cantidadHabitaciones
                        val totalDescuento = totalI * (1 - descuentoTotal / 100)
                        val fechaEntrada = activity_reserva.fechaEntrada
                        val fechaSalida = activity_reserva.fechaSalida
                        val idUsuario = idUsuarioGlobalL
                        val idDepartamento = activity_reserva.idDepartamento

                        if (idHotelRecibido == null || idUsuario == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@activity_confirmacionReserva, "Error: Datos incompletos.", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        val conexion = ClaseConexion().cadenaConexion()
                        val query = """
                            INSERT INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, cantidad_habitaciones_reservadas, id_tipo_habitacion, id_departamento, id_usuario) 
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """
                        val statement = conexion?.prepareStatement(query)
                        statement?.apply {
                            setInt(1, idHotelRecibido)
                            setString(2, fechaEntrada)
                            setString(3, fechaSalida)
                            setString(4, numeroTarjeta)
                            setString(5, fechaCaducidad)
                            setString(6, nombreTitular)
                            setInt(7, cvv ?: 0)
                            setDouble(8, totalDescuento)
                            setInt(9, cantidadHabitaciones ?: 0)
                            setInt(10, idTipoHabitacionRecibido)
                            setInt(11, idDepartamento ?: 0)
                            setInt(12, idUsuario)
                            executeUpdate()
                        }

                        withContext(Dispatchers.Main) {
                            val txtCorreoI = activity_iniciar_sesion.correoIngresado
                            val nombreUsuario = PaginaInicio.nombreUsuarioGlobalL
                            val fechaEntrada = activity_reserva.fechaEntrada
                            val fechaSalida = activity_reserva.fechaSalida
                            enviarCorreoConfirmacion(nombreUsuario!!, correoIngresado, fechaEntrada!!, fechaSalida!!, totalDescuento)
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

            btnNoAceptar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            overridePendingTransition(0,0)
        }
    }

    private suspend fun enviarCorreoConfirmacion(nombreUsuario: String, correoReceptor: String, fechaEntrada: String, fechaSalida: String, totalDescuento: Double) {
        val sujeto = "Confirmación de Reserva - GoStay"
        val mensaje = """
        <!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: 'Lato', sans-serif;
            background-color: #f9f9f9;
            color: #333;
            padding: 20px;
            line-height: 1.6;
        }
        h1 {
            color: #5cb5c4;
        }
        h2 {
            color: #5cb5c4;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        .details {
            padding: 15px 30px;
            font-size: 18px;
            background-color: #e3f2fd;
            border-left: 5px solid #5cb5c4;
            margin: 20px 0;
        }
        .footer {
            margin-top: 30px;
            font-size: 14px;
            color: #777;
            text-align: center;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            font-size: 16px;
            color: #fff;
            background-color: #5cb5c4;
            border-radius: 5px;
            text-decoration: none;
            margin-top: 20px;
        }
        .button:hover {
            background-color: #4caea2;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Estimado/a $nombreUsuario,</h1>
        <p>Gracias por su reserva en <strong>GoStay</strong>. Aquí están los detalles de su reserva:</p>
        <div class="details">
            <ul>
                <li><strong>Fecha de entrada:</strong> $fechaEntrada</li>
                <li><strong>Fecha de salida:</strong> $fechaSalida</li>
                <li><strong>Total pagado:</strong> ${'$'}$totalDescuento</li>
            </ul>
        </div>
        <p>Esperamos que disfrute de su estancia.</p>
        <p class="footer">Atentamente,</p>
        <p class="footer">El equipo de GoStay</p>
    </div>
</body>
</html>
    """.trimIndent()

        // Llama a la función enviarCorreo
        enviarCorreo(correoReceptor, sujeto, mensaje)
    }

    private suspend fun enviarCorreo(receptor: String, sujeto: String, mensaje: String) {
        withContext(Dispatchers.IO) {
            val props = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    // Consider using environment variables or a secure method for storing credentials
                    return PasswordAuthentication("gostay2024@gmail.com", "dekt szbp iwoe swut")
                }
            })

            // Hacemos el envío
            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress("gostay2024@gmail.com"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(receptor))
                    setSubject(sujeto)
                    setContent(mensaje, "text/html; charset=utf-8")
                }

                Transport.send(message)
                Log.d("CorreoConfirmacion", "Correo enviado satisfactoriamente")
            } catch (e: MessagingException) {
                Log.e("CorreoConfirmacion", "Error al enviar el correo: ${e.message}")
                // Aquí puedes mostrar un Toast o un AlertDialog para informar al usuario
            }
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
                overridePendingTransition(0,0)

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
                overridePendingTransition(0,0)

            }
            // Muestra el diálogo en pantalla
            dialog.show()
        }
    }
}