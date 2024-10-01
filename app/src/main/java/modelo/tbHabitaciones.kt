// Campos de la tbHabitaciones

package modelo

data class tbHabitaciones(
    val id_habitacion: Int,
    val entrada: String,
    val salida: String,
    val numero_tarjeta: String,
    val fecha_caducidad_tarjeta: String,
    val nombre_titular_tarjeta: String,
    val CVV: Int,
    val Total: Int,
    val id_tipo_habitacion: Int,
    val id_hoteles: Int,
    val id_departamento: Int
)
