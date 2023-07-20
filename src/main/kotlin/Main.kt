import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.json.JsonData
import java.io.FileInputStream
import java.util.Properties

fun main(args: Array<String>) {
    val (login, password, fingerprint) = loadConfigKeys()

    ElasticsearchClientWrapper(login, password, fingerprint).use { client ->
        val query = and {
            +or {
                +("age" lte 23)
                +boost(2F) {
                    +("salary" lte 200)
                }
            }
            +or {
                +boost(2F) {
                    +("position" eq "rw")
                }
                +("position" eq "lw")
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

fun or(
    addQuery: OrOperator.() -> Unit
): Query {
    val operator = OrOperator()
    operator.addQuery()
    return operator.build()
}

class OrOperator: BaseOperator() {
    override fun build(): Query {
        val builder = BoolQuery.Builder()
        builder.should(children)
        return builder.build()._toQuery()
    }
}

fun and(
    addQuery: AndOperator.() -> Unit
): Query {
    val operator = AndOperator()
    operator.addQuery()
    return operator.build()
}

class AndOperator: BaseOperator() {
    override fun build(): Query {
        val builder = BoolQuery.Builder()
        builder.must(children)
        return builder.build()._toQuery()
    }
}

fun boost(
    factor: Float,
    addQuery: BoostOperator.() -> Unit
): Query {
    val operator = BoostOperator(factor)
    operator.addQuery()
    return operator.build()
}

class BoostOperator(
    private val factor: Float
): BaseOperator() {
    override fun build(): Query {
        if (children.count() != 1) {
            throw IllegalArgumentException("Boost operator must have exactly one child")
        }

        val builder = BoolQuery.Builder()
        builder.must(children)
        builder.boost(factor)
        return builder.build()._toQuery()
    }
}

abstract class BaseOperator {
    protected val children = mutableListOf<Query>()

    operator fun Query.unaryPlus() {
        children.add(this)
    }

    infix fun String.eq(value: String): Query =
        TermQuery.Builder().field(this).value(value).build()._toQuery()

    infix fun String.lte(value: Int): Query =
        RangeQuery.Builder().field(this).lte(JsonData.of(value)).build()._toQuery()

    abstract fun build(): Query
}

private fun printResults(response: SearchResponse<Footballer>) {
    println("==========================================================")
    println("Hits: ${response.hits().total()?.value()}")
    for (hit in response.hits().hits()) {
        println("Name: ${hit.source()?.name}, Score: ${hit.score()}")
    }
}

private fun loadConfigKeys(): Triple<String, String, String> {
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
