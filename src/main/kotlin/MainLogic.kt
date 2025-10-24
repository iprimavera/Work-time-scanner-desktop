import kotlinx.serialization.Serializable

data class Usuario(val codigo: String, val nombre: String, val correo: String)

@Serializable
data class Registro(val codigo: String, var isConectado: Boolean, var ultimaConexion: String)

class MainLogic() {

    private val reset = "\u001B[0m"
    private val rojo = "\u001B[31m"
    private val verde = "\u001B[32m"
    private val amarillo = "\u001B[33m"
    private val cyan = "\u001B[36m"

    val usuarios = mutableSetOf<Usuario>()

    val files = FileManager()

    fun cargarUsuarios() = files.cargarUsuarios(usuarios)

    fun procesarCodigo(codigo: String) {


        if (existeUsuario(codigo)) { // si no existe lo creo
            val newUsuario = crearUsuario(codigo)
            usuarios.add(newUsuario)
            files.guardarUsuario(newUsuario)
            println("${rojo}Actualmente estas desconectado, vuelve a pasar tu codigo si quieres empezar a inputar$reset")

        } //else if (!files.isConectado(codigo)) {
//            val usuario = usuarios.find { it.codigo == codigo }!!
//
//            println("${verde}Bienvenid@ ${usuario.nombre}!$reset")
////            println("Hoy has trabajado en total ${} hasta ahora")
//            files.switchConectado(codigo)
//            files.actualizarRegistro()
//
//        } else {
//            val usuario = usuarios.find { it.codigo == codigo }!!
//
//            files.addTiempo(usuario)
//            files.switchConectado(codigo)
//            files.actualizarRegistro()
//
//            println("${cyan}Hasta luego ${usuario.nombre}!$reset")
//            println("${cyan}Hoy has trabajado en total ${files.getTotalTime(codigo)} hasta ahora$reset")
//        }

    }

    fun crearUsuario(codigo: String): Usuario {
        while (true) {
            println("${rojo}No estas registrado en el sistema.$reset")
            print("Indica tu nombre completo: ")
            val nombre = readln()
            print("Indica tu correo electronico: ")
            val correo = readln()
            clear()
            println("$amarillo *** La informacion es correcta?")
            println("     Nombre completo: $nombre")
            println("     Correo electronico: $correo$reset")
            println()
            print(" [y/n] : ")
            clear()
            if (readln() == "y") return Usuario(codigo,nombre,correo)
        }
    }

    fun existeUsuario(codigo: String): Boolean = usuarios.none { it.codigo == codigo }

    fun clear() = print("\u001b[H\u001b[2J")
}