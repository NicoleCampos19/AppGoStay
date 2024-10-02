// Campos de la tbHoteles

package modelo

import java.io.Serializable

data class tbHotel(
    val id_hoteles: Int,
    val nombreHotel: String,
    val descripcion: String,
    val direccion: String,
    val latitudHotel: Double,
    val longitudHotel: Double,
    val correo: String,
    val img_url: String,
    val id_usuario : Int
) : Serializable
