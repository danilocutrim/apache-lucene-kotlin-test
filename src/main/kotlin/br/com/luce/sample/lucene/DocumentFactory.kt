package br.com.luce.sample.lucene

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.json.JSONObject

class DocumentFactory {
    companion object {
        fun createDocument(json: JSONObject): Document {
            val document = Document()
            for (key in json.keys()) {
                val value = getKeyValue(json, key)
                val field = StringField(key, value.toString(), Field.Store.YES)
                document.add(field)
            }
            return document
        }

        private fun getKeyValue(json: JSONObject, key: String?): String? {
            val value: String
            if (key.equals("requestHeaders") || key.equals("requestBody") || key.equals("queryParams")){
                value = json.getJSONObject(key).toString()
                return value
            }
            value = json.getString(key)
            return value
        }
    }
}