import kotlinx.serialization.Serializable

data class Usuario(val codigo: String, val nombre: String, val correo: String)

@Serializable
data class Registro(val codigo: String, var isConectado: Boolean, var ultimaConexion: String)

class MainLogic {

    private val usuarios = mutableSetOf<Usuario>()

    private val files = FileManager()

    fun cargarUsuarios() = files.cargarUsuarios(usuarios)

    fun procesarCodigo(codigo: String): Pantallas {

        if (!existeUsuario(codigo)) { // si no existe lo creo
            return Pantallas.CREAR_USUARIO
        } else if (!files.isConectado(codigo)) {
//            println("${verde}Bienvenid@ ${usuario.nombre}!$reset")
//            println("Hoy has trabajado en total ${} hasta ahora")
            files.switchConectado(codigo)
            files.actualizarRegistro()
            return Pantallas.SPLASH_ENTRADA

        } else {
            val usuario = usuarios.find { it.codigo == codigo }!!

            files.addTiempo(usuario)
            files.switchConectado(codigo)
            files.actualizarRegistro()

//            println("${cyan}Hasta luego ${usuario.nombre}!$reset")
//            println("${cyan}Hoy has trabajado en total ${files.getTotalTime(codigo)} hasta ahora$reset")
            return Pantallas.SPLASH_ENTRADA
        }
    }

    fun isConectado(codigo: String): Boolean {
        return files.isConectado(codigo)
    }

    fun crearUsuario(codigo: String, nombre: String, correo: String): Pantallas {
        val newUsuario = Usuario(codigo,nombre,correo)
        usuarios.add(newUsuario)
        files.guardarUsuario(newUsuario)
        return Pantallas.LEER_CODIGO
    }

    private fun existeUsuario(codigo: String): Boolean = usuarios.any { it.codigo == codigo }
}