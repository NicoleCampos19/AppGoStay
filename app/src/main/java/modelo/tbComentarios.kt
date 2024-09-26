package modelo

data class tbComentarios(
    val id_valoracion: Int,
    var comentario: String,
    var id_usuario: Int,
    var nombre_usuario: String,
    var foto_usuario: String
)