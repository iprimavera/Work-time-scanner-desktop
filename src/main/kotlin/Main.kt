import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            App(logic)
        }
    }
}

// las funciones composable se ejecutan cada vez que detectan un cambio en la interfaz
@Composable
fun App(logic: MainLogic) {
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

    if (codigo.isNotEmpty() && codigo.last() == '\n') {
        codigo = codigo.removeSuffix("\n")

        onContinuar(codigo, logic.procesarCodigo(codigo))
    }

    Column {
        TextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Escanea tu codigo") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Has escrito: $codigo")
    }
}

@Composable
fun pantallaCrearUsuario(logic: MainLogic, codigo: String, onContinuar: (Pantallas) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    Column {
        Text("No estas registrado en el sistema.")
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Indica tu nombre completo") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Indica tu correo") }
        )
        Button(
            onClick = {
                onContinuar(logic.crearUsuario(codigo, nombre, correo)) //TODO tener precauciones
            }
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun pantallaPostCodigo(logic: MainLogic, codigo: String, onContinuar: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onContinuar()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (logic.isConectado(codigo)) {
            Text("Bienvenido!")
        } else {
            Text("Hasta luego!")
        }
    }
}