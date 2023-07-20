package dsl

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query

class OrOperator: BaseOperator() {
    override fun build(): Query {
        val builder = BoolQuery.Builder()
        builder.should(children)
        return builder.build()._toQuery()
    }
}