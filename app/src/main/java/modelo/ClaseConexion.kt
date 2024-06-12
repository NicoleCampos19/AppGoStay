package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {               //cambiar ip dependiendo de la computadora
            val ip = "jdbc:oracle:thin:@10.10.1.119:1521:xe"
            val usuario = "system"
            val contrasena = "desarrollo"

            val conexion = DriverManager.getConnection(ip, usuario, contrasena)
            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }

}