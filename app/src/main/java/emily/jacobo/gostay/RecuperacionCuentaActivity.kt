package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecuperacionCuentaActivity : AppCompatActivity() {

    // Variable estática que guarda un código de recuperación aleatorio de 6 dígitos
    companion object variablesGobalesRecuperacion {
        var codigoRecuperacion = (100000..999999).random()
        lateinit var Correo: String
    }

    // Un requerimiento de api P
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_cuenta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mando a llamar los elementos de la vista
        val imvAtras = findViewById<ImageView>(R.id.imvAtras)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val btnRecuperacion = findViewById<Button>(R.id.btnRecuperacion)

        // Navegación para ir a metodos_contras
        imvAtras.setOnClickListener {
            val volverAtras = Intent(this, metodos_contras::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }

        btnRecuperacion.setOnClickListener {
            Correo = txtCorreo.text.toString()

            // Variables en las que se guardarán los posibles errores
            var hayVacios = false
            var hayErrores = false

            // Validación para el campo de correo
            if (Correo.isEmpty()) {
                setErrorWithCustomFont(txtCorreo, "Llena este campo", R.font.poppins)
                hayVacios = true
            } else if (!Correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.][a-z]+"))) {
                setErrorWithCustomFont(txtCorreo, "El formato del correo no es válido", R.font.poppins)
                hayErrores = true
            }

            // Si hay errores se mostrará una toast
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG).show()
            } else {

                codigoRecuperacion = (100000..999999).random()  // Aquí se genera y almacena el código

                // Usa la variable global en el correo
                val htmlCorreo = generarHTMLCorreo(codigoRecuperacion.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    enviarCorreo(Correo, "Recuperacion de contraseña", htmlCorreo)
                }

                // Para ir a Confirmacion_Cuenta
                val siguientePantalla = Intent(this, Confirmacion_Cuenta::class.java)
                startActivity(siguientePantalla)
                overridePendingTransition(0, 0)
            }
        }
    }

    // Para que las validaciones se muestren bonitas con poppins
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
        val typeface = ResourcesCompat.getFont(this, fontResId)
        val spannableString = android.text.SpannableString(errorMessage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            spannableString.setSpan(
                typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        editText.error = spannableString
    }

// Función que le da diseño al correo que manda el código
private fun generarHTMLCorreo(codigoRecuperacion: String): String{
                    return """
<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
<head>

  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="x-apple-disable-message-reformatting">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title></title>
  
    <style type="text/css">
      @media only screen and (min-width: 620px) {
  .u-row {
    width: 600px !important;
  }
  .u-row .u-col {
    vertical-align: top;
  }

  .u-row .u-col-100 {
    width: 600px !important;
  }

}

@media (max-width: 620px) {
  .u-row-container {
    max-width: 100% !important;
    padding-left: 0px !important;
    padding-right: 0px !important;
  }
  .u-row .u-col {
    min-width: 320px !important;
    max-width: 100% !important;
    display: block !important;
  }
  .u-row {
    width: 100% !important;
  }
  .u-col {
    width: 100% !important;
  }
  .u-col > div {
    margin: 0 auto;
  }
}
body {
  margin: 0;
  padding: 0;
}

table,
tr,
td {
  vertical-align: top;
  border-collapse: collapse;
}

p {
  margin: 0;
}

.ie-container table,
.mso-container table {
  table-layout: fixed;
}

* {
  line-height: inherit;
}

a[x-apple-data-detectors='true'] {
  color: inherit !important;
  text-decoration: none !important;
}

table, td { color: #000000; } @media (max-width: 480px) { #u_column_1 .v-col-background-color { background-color: #5cb5c4 !important; } #u_content_text_3 .v-color { color: #000000 !important; } #u_content_text_3 .v-text-align { text-align: center !important; } #u_content_text_4 .v-color { color: #000000 !important; } #u_column_2 .v-col-background-color { background-color: #5cb5c4 !important; } #u_content_text_12 .v-color { color: #ffffff !important; } #u_content_text_11 .v-color { color: #000000 !important; } }
    </style>
  
<link href="https://fonts.googleapis.com/css?family=Lato:400,700&display=swap" rel="stylesheet" type="text/css">

</head>

<body class="clean-body u_body" style="margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #f9f9f9;color: #000000">

  <table style="border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #f9f9f9;width:100%" cellpadding="0" cellspacing="0">
  <tbody>
  <tr style="vertical-align: top">
    <td style="word-break: break-word;border-collapse: collapse !important;vertical-align: top">
    
<div class="u-row-container" style="padding: 0px;background-color: #f9f9f9">
  <div class="u-row" style="margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #f9f9f9;">
    <div style="border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;">
      
<div class="u-col u-col-100" style="max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;">
  <div class="v-col-background-color" style="height: 100%;width: 100% !important;">
<div style="box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;">
  
<table style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:15px;font-family:'Lato',sans-serif;" align="left">
        
  <table height="0px" align="center" border="0" cellpadding="0" cellspacing="0" width="100%" style="border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;border-top: 1px solid #f9f9f9;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%">
    <tbody>
      <tr style="vertical-align: top">
        <td style="word-break: break-word;border-collapse: collapse !important;vertical-align: top;font-size: 0px;line-height: 0px;mso-line-height-rule: exactly;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%">
          <span>&#160;</span>
        </td>
      </tr>
    </tbody>
  </table>

      </td>
    </tr>
  </tbody>
</table>

</div>
  </div>
</div>
    </div>
  </div>
  </div>
  
<div class="u-row-container" style="padding: 0px;background-color: transparent">
  <div class="u-row" style="margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;">
    <div style="border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;">

<div id="u_column_1" class="u-col u-col-100" style="max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;">
  <div class="v-col-background-color" style="height: 100%;width: 100% !important;">
<div style="box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;">
  
<table style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:25px 10px;font-family:'Lato',sans-serif;" align="left">
        
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td class="v-text-align" style="padding-right: 0px;padding-left: 0px;" align="center">
      
      
      <a href="https://imgbb.com/"><img src="https://i.ibb.co/nr1HHQH/image-1.png" alt="image-1" title="Image" style="outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 29%;max-width: 168.2px;" width="168.2"/>

    </td>
  </tr>
</table>

      </td>
    </tr>
  </tbody>
</table>


  </div>
</div>

    </div>
  </div>
  </div>

<div class="u-row-container" style="padding: 0px;background-color: transparent">
  <div class="u-row" style="margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;">
    <div style="border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;">
      
<div class="u-col u-col-100" style="max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;">
  <div class="v-col-background-color" style="height: 100%;width: 100% !important;">
<div style="box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;">
  
<table id="u_content_text_3" style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:0px 10px 30px;font-family:'Lato',sans-serif;" align="left">
        
  <div class="v-color v-text-align" style="font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;">
    <p style="line-height: 140%;"> </p>
<p style="line-height: 140%;"><strong> BIENVENIDO SOMOS EL EQUIPO</strong></p>
<p style="line-height: 140%; text-align: center;"><strong>GoStay!</strong></p>
  </div>

      </td>
    </tr>
  </tbody>
</table>

<table id="u_content_text_4" style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:40px 40px 30px;font-family:'Lato',sans-serif;" align="left">
        
  <div class="v-color v-text-align" style="font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;">
    <p style="line-height: 140%;">¿Olvidaste tu contraseña?</p>
<p style="line-height: 140%;"> </p>
<p style="line-height: 140%;">No te preocupes, basta con que coloques el código que te enviamos en este correo para poder cambiar tu contraseña</p>
  
  <br>
  <br>
  
      <div class="code" style="display: inline-block;
                padding: 15px 30px; 
                font-size: 24px; 
                color: #000;
                background-color: #5cb5c4;
                border-radius: 8px; 
                margin-bottom: 50px; 
                text-decoration: none;">$codigoRecuperacion</div>
  
  </div>
      </td>
    </tr>
  </tbody>
</table>

</div>
  </div>
</div>

    </div>
  </div>
  </div>
  
<div class="u-row-container" style="padding: 0px;background-color: #f9f9f9">
  <div class="u-row" style="margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #1c103b;">
    <div style="border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;">

<div id="u_column_2" class="u-col u-col-100" style="max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;">
  <div class="v-col-background-color" style="height: 100%;width: 100% !important;">
<div style="box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;">
  
<table id="u_content_text_12" style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Lato',sans-serif;" align="left">
        
  <div class="v-color v-text-align" style="font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;">
    <p style="line-height: 140%;">Contáctanos: <br /><span translate="no" data-hovercard-id="gostay2024@gmail.com" name="gostay2024@gmail.com" role="gridcell" data-hovercard-owner-id="27" style="line-height: 19.6px;" tabindex="-1">gostay2024@gmail.com</span> </p>
  </div>

      </td>
    </tr>
  </tbody>
</table>

</div>
  </div>
</div>

    </div>
  </div>
  </div>
  
<div class="u-row-container" style="padding: 0px;background-color: transparent">
  <div class="u-row" style="margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #f9f9f9;">
    <div style="border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;">

<div class="u-col u-col-100" style="max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;">
  <div class="v-col-background-color" style="height: 100%;width: 100% !important;">
<div style="box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;">
  
<table id="u_content_text_11" style="font-family:'Lato',sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
  <tbody>
    <tr>
      <td style="overflow-wrap:break-word;word-break:break-word;padding:0px 40px 30px 20px;font-family:'Lato',sans-serif;" align="left">
        
  <div class="v-color v-text-align" style="font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;">
    <p style="line-height: 140%;">Derechos reservados Equipo GoStay!</p>
  </div>

      </td>
    </tr>
  </tbody>
</table>
</div>
  </div>
</div>
    </div>
  </div>
  </div>
    </td>
  </tr>
  </tbody>
  </table>
</body>
</html>
""".trimIndent()
                }

            }