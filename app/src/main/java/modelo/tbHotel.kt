package modelo

data class tbHotel(
    val id_hoteles: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val correo: String,
    val cantidad_habitaciones: Int,
    val img_url: String,
    val id_habitacion: Int,
    val id_servicio_hotel: Int
)
