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
    val id_usuario : Int
) : Serializable
