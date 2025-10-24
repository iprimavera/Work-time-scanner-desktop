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
            files.switchConectado(codigo)
            files.actualizarRegistro()
            return Pantallas.SPLASH_ENTRADA

        } else {
            val usuario = usuarios.find { it.codigo == codigo }!!

            files.addTiempo(usuario)
            files.switchConectado(codigo)
            files.actualizarRegistro()

            return Pantallas.SPLASH_ENTRADA
        }
    }

    fun getTotalTime(codigo: String): String = files.getTotalTime(codigo)

    fun isConectado(codigo: String): Boolean = files.isConectado(codigo)

    fun crearUsuario(codigo: String, nombre: String, correo: String): Pantallas {
        val newUsuario = Usuario(codigo,nombre,correo)
        usuarios.add(newUsuario)
        files.guardarUsuario(newUsuario)
        return Pantallas.LEER_CODIGO
    }

    private fun existeUsuario(codigo: String): Boolean = usuarios.any { it.codigo == codigo }
}