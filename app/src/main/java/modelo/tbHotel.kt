package modelo

import java.io.Serializable

data class tbHotel(
    val id_hoteles: Int,
    val nombreHotel: String,
    val descripcion: String,
    val direccion: String,
    val correo: String,
    val cantidad_habitaciones: Int,
    val img_url: String,
    val id_tipo_habitacion: Int,
    val id_servicio_hotel: Int,
    val id_valoracion: Int
) : Serializable
