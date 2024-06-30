package modelo

data class Hotel(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val correo: String,
    val cantidadHabitaciones: Int,
    val imgUrl: String,
    val idHabitacion: Int?,
    val idServicioHotel: Int?
)
