import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay

enum class Pantallas {
    LEER_CODIGO,
    CREAR_USUARIO,
    SPLASH_ENTRADA
}

fun main() = application {

    val logic = MainLogic()

    logic.cargarUsuarios()

    Window(onCloseRequest = ::exitApplication, title = "Work Time Scanner") {
        MaterialTheme {
            app(logic)
        }
    }
}

// las funciones composable se ejecutan cada vez que detectan un cambio en la interfaz
@Composable
fun app(logic: MainLogic) {
    // el remember sirve para guardar el estado entre composiciones
    var pantallaActual by remember { mutableStateOf(Pantallas.LEER_CODIGO) }
    var codigo by remember { mutableStateOf("") }

    when (pantallaActual) {
        Pantallas.LEER_CODIGO -> {
            pantallaLeerCodigo( logic,
                onContinuar = { codigoNuevo, pantallaNueva ->
                    codigo = codigoNuevo
                    pantallaActual = pantallaNueva
                }
            )
        }
        Pantallas.CREAR_USUARIO -> {

            pantallaCrearUsuario(logic, codigo,
                onContinuar = { pantallaNueva ->
                    pantallaActual = pantallaNueva
                }
            )
        }
        Pantallas.SPLASH_ENTRADA -> {
            pantallaPostCodigo(logic, codigo,
                onContinuar = {
                    pantallaActual = Pantallas.LEER_CODIGO
                }
            )
        }
    }
}

@Composable
fun pantallaLeerCodigo(logic: MainLogic, onContinuar: (String, Pantallas) -> Unit) {
    var codigo by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    if (codigo.isNotEmpty() && codigo.last() == '\n') {
        codigo = codigo.removeSuffix("\n")
        onContinuar(codigo, logic.procesarCodigo(codigo))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Escanea tu codigo") },
            modifier = Modifier.focusRequester(focusRequester).width(800.dp).height(120.dp),
            textStyle = TextStyle(fontSize = 80.sp),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                textColor = Color.White,
//                cursorColor = Color(0xFFBB86FC),
//                focusedBorderColor = Color(0xFFBB86FC),
//                unfocusedBorderColor = Color(0xFF8A2BE2),
//                focusedLabelColor = Color(0xFFBB86FC),
//                unfocusedLabelColor = Color(0xFF8A2BE2)
//            )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
fun pantallaCrearUsuario(logic: MainLogic, codigo: String, onContinuar: (Pantallas) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text("No estas registrado en el sistema.")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Indica tu nombre completo") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Indica tu correo") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onContinuar(logic.crearUsuario(codigo, nombre, correo))
                }
            ) {
                Text("Registrar")
            }
        }
    }
}

@Composable
fun pantallaPostCodigo(logic: MainLogic, codigo: String, onContinuar: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onContinuar()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (logic.isConectado(codigo)) {
            Text("Bienvenido!", fontSize = 64.sp, fontWeight = FontWeight.Bold)
        } else {
            Text("Hasta luego!\nHoy has trabajado un total de: ${logic.getTotalTime(codigo).split(".")[0]}",
                fontSize = 48.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 800.dp), lineHeight = 70.sp)
        }
    }
}