package dsl

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.json.JsonData

abstract class BaseOperator {
    protected val children = mutableListOf<Query>()

    operator fun Query.unaryPlus() {
        children.add(this)
    }

    infix fun String.eq(value: String): Query =
        TermQuery.Builder().field(this).value(value).build()._toQuery()

    infix fun String.lte(value: Int): Query =
        RangeQuery.Builder().field(this).lte(JsonData.of(value)).build()._toQuery()

    infix fun Query.boost(factor: Float): Query = if (factor == 1F) {
        this
    } else BoolQuery.Builder().must(this).boost(factor).build()._toQuery()

    abstract fun build(): Query
}