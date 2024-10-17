package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {
            //cambiar ip dependiendo de la computadora
            val ipLeonardo = "jdbc:oracle:thin:@192.168.0.11:1521:xe"
            val ipEmily = "jdbc:oracle:thin:@192.168.0.10:1521:xe"
            val ipGabriela = "jdbc:oracle:thin:@192.168.0.91:1521:xe"
            val ipAbigail = "jdbc:oracle:thin:@172.20.10.3:1521:xe"
            val ipSofia = "jdbc:oracle:thin:@192.168.1.5:1521:xe"
            val ipDaniel = "jdbc:oracle:thin:@192.168.56.1:1521:xe"
            val ipBryan = "jdbc:oracle:thin:@192.168.56.1:1521:xe"
            val ipMirna = "jdbc:oracle:thin:@10.10.0.57:1521:xe"

            // Variables que contienen el usuario y contraseña de la base de datos
            val usuario = "GoStay"
            val contrasena = "ricaldone2024e"

            // Todo lo necesario para la conexión
            val conexion = DriverManager.getConnection(ipGabriela, usuario, contrasena)

            
            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }
}

