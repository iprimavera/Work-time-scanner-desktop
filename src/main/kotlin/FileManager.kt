import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.time.Duration

class FileManager {

    private val properties = Properties()
    private val defaultProperties = ClassLoader.getSystemResourceAsStream("config.properties")

    private val internalDir = File(System.getProperty("user.home"),".workTimeScanner")

    private val lastTimeExec = File(internalDir,"config.properties")
    private val jsonReg = File(internalDir,"registry.json")
    private val userInfo = File("userInfo.csv")
    private val data = File("data.csv")

    private val registry: MutableSet<Registro>

    init {
        // crear los archivos y carpetas que no haya
        if (!internalDir.exists()) internalDir.mkdirs()

        if (!lastTimeExec.exists()) properties.load(defaultProperties)
        else FileInputStream(lastTimeExec).use { properties.load(it) }
        if (!data.exists()) data.writeText("Codigo,Usuario,Fecha,Tiempo")
        if (!jsonReg.exists()) {
            jsonReg.writeText("[]")
        }

        // si es el dia siguiente
        if (LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
            != properties.getProperty("lastTimeExec").toInt()) {

            jsonReg.writeText("[]")

            // guardar nuevo lastTimeExec
            properties.setProperty("lastTimeExec",LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE).toString())
            FileOutputStream(lastTimeExec).use { properties.store(it, null) }
        }

        registry = Json.decodeFromString<MutableSet<Registro>>(jsonReg.readText())
    }

    fun guardarUsuario(usuario: Usuario) {
        userInfo.appendText("${usuario.codigo},${usuario.nombre},${usuario.correo}\n")
    }

    fun cargarUsuarios(usuarios: MutableSet<Usuario>) {
        if (userInfo.exists()) {
            for (usuario in userInfo.readLines()) {
                val datos = usuario.split(",")
                usuarios.add(Usuario(datos[0], datos[1], datos[2]))
            }
        }
    }

    fun isConectado(codigo: String): Boolean {
        return registry.any { it.codigo == codigo && it.isConectado }
    }

    fun switchConectado(codigo: String) {
        if (!registry.any { it.codigo == codigo }) registry.add(Registro(codigo,false,""))
        val usuario = registry.find { it.codigo == codigo }!!
        usuario.isConectado = usuario.isConectado.not()
        usuario.ultimaConexion = LocalTime.now().toString()
    }

    fun actualizarRegistro() {
        jsonReg.writeText(Json.encodeToString(registry))
    }

    fun addTiempo(usuario: Usuario) {

        if (data.readLines().none { it.split(",").first() == usuario.codigo &&
            it.split(",")[2] == LocalDate.now().toString() }) {
            data.appendText("\n${usuario.codigo},${usuario.nombre},${LocalDate.now()},00:00")
        }
        val lineas = data.readLines().toMutableList()
        val index = lineas.indexOfFirst { usuario.codigo == it.split(",").first() &&
            LocalDate.now().toString() == it.split(",")[2]}

        // ultima conexion (registry) hasta localtime now + tiempo que ya estuvo (lineas)
        val periodoActual = Duration.between(
            LocalTime.parse(registry.find { it.codigo == usuario.codigo }!!.ultimaConexion),LocalTime.now())

        val totalTime = LocalTime.parse(
            lineas[index].split(",").last()
        ).plus(periodoActual)

        lineas[index] = "${usuario.codigo},${usuario.nombre},${LocalDate.now()},${totalTime}"

        data.writeText(lineas.joinToString("\n"))
    }

    fun getTotalTime(codigo: String): String {
        return data.readLines().find { it.split(",").first() == codigo &&
                LocalDate.now().toString() == it.split(",")[2]}!!.split(",").last()
    }

}