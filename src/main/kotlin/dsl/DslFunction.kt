package dsl

import co.elastic.clients.elasticsearch._types.query_dsl.Query

fun and(
    addQuery: AndOperator.() -> Unit
): Query {
    val operator = AndOperator()
    operator.addQuery()
    return operator.build()
}

fun or(
    addQuery: OrOperator.() -> Unit
): Query {
    val operator = OrOperator()
    operator.addQuery()
    return operator.build()
}