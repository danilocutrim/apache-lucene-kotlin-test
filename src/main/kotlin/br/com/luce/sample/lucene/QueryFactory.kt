package br.com.luce.sample.lucene

import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.json.JSONObject

class QueryFactory {
    companion object {
        fun query(json: JSONObject): BooleanQuery {
            val booleanQuery = BooleanQuery.Builder()
            for (key in json.keys()) {
                val value = getKeyValue(json, key)
                val term = TermQuery(Term(key, value))
                booleanQuery.add(term, BooleanClause.Occur.MUST)
            }
            return booleanQuery.build()
        }

        private fun getKeyValue(json: JSONObject, key: String?): String? {
            val value: String
            if (key.equals("requestHeaders") || key.equals("requestBody") || key.equals("queryParams")) {
                value = json.getJSONObject(key).toString()
                return value
            }
            value = json.getString(key)
            return value
        }
    }


}