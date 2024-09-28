package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {
            //cambiar ip dependiendo de la computadora
            val ipLeonardo = "jdbc:oracle:thin:@192.168.0.11:1521:xe"
            val ipEmily = "jdbc:oracle:thin:@192.168.0.10:1521:xe"
            val ipGabriela = "jdbc:oracle:thin:@192.168.1.13:1521:xe"
            val ipAbigail = "jdbc:oracle:thin:@172.20.10.3:1521:xe"
            val ipSofia = "jdbc:oracle:thin:@192.168.1.5:1521:xe"
            val ipDaniel = "jdbc:oracle:thin:@192.168.56.1:1521:xe"
            val ipDatosEmily = "jdbc:oracle:thin:@172.20.10.2:1521:xe"
            val ipBryan = "jdbc:oracle:thin:@192.168.56.1:1521:xe"
            val ipMirna = "jdbc:oracle:thin:@10.10.0.57:1521:xe"
            val ipprueba = "jdbc:oracle:thin:@192.168.1.20:1521:xe"

            val usuario = "system"
            val contrasena = "ITR2024"

            val conexion = DriverManager.getConnection(ipEmily, usuario, contrasena)

            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }
}

