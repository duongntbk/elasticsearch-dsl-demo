import co.elastic.clients.elasticsearch.core.SearchResponse
import java.io.FileInputStream
import java.util.Properties

fun printResults(response: SearchResponse<Footballer>) {
    println("==========================================================")
    println("Hits: ${response.hits().total()?.value()}")
    for (hit in response.hits().hits()) {
        println("Name: ${hit.source()?.name}, Score: ${hit.score()}")
    }
}

fun loadConfigKeys(): Triple<String, String, String> {
    val properties = Properties()

    val localFile = "src/main/resources/application-env-local.yml"
    val file = if (java.io.File(localFile).exists()) localFile else "src/main/resources/application.yml"

    FileInputStream(file).use { inputStream ->
        properties.load(inputStream)
    }

    val login = properties.getProperty("login")
    val password = properties.getProperty("password")
    val fingerprint = properties.getProperty("fingerprint")
    return Triple(login, password, fingerprint)
}
