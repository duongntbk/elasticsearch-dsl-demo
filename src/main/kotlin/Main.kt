import co.elastic.clients.elasticsearch.core.SearchRequest
import dsl.and
import dsl.or

fun main(args: Array<String>) {
    val (login, password, fingerprint) = loadConfigKeys()

    ElasticsearchClientWrapper(login, password, fingerprint).use { client ->
        val query = and {
            +or {
                +(AGE lte 23)
                +((SALARY lte 200) boost 2F)
            }
            +or {
                +((POSITION eq "rw") boost 2F)
                +(POSITION eq "lw")
            }
        }

        val request = SearchRequest.Builder()
            .index("footballer")
            .query(query)
            .build()
        val response = client.search(request, Footballer::class.java)
        printResults(response)
    }
}
