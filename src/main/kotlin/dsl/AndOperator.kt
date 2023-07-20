package dsl

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query

class AndOperator: BaseOperator() {
    override fun build(): Query {
        val builder = BoolQuery.Builder()
        builder.must(children)
        return builder.build()._toQuery()
    }
}