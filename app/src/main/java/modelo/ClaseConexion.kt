package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {
            //cambiar ip dependiendo de la computadora
            val ipLeonardo = "jdbc:oracle:thin:@192.168.0.11:1521:xe"
            val ipEmily = "jdbc:oracle:thin:@192.168.0.13:1521:xe"
            val ipGabriela = "jdbc:oracle:thin:@192.168.1.13:1521:xe"
            val ipAbigail = "jdbc:oracle:thin:@192.168.0.8:1521:xe"
            val ipSofia = "jdbc:oracle:thin:@172.24.192.1:1521:xe"
            val ipDaniel = "jdbc:oracle:thin:@192.168.56.1:1521:xe"

            val usuario = "GoStay"
            val contrasena = "ricaldone2024e"

            //Conexión ip de Leo
            val conexion = DriverManager.getConnection(ipLeonardo, usuario, contrasena)
            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }

}