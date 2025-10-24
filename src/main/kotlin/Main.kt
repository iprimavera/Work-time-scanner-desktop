import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

enum class Estado {
    LEER_CODIGO,
    CREAR_USUARIO
}

var estado: Estado = Estado.LEER_CODIGO

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
    var codigo by remember { mutableStateOf("") }

    if (codigo.isNotEmpty() && codigo.last() == '\n') {
        logic.procesarCodigo(codigo.removeSuffix("\n"))
        codigo = ""
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
