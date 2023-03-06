package br.com.luce.sample.service

import br.com.luce.sample.lucene.FileSearch
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchService(
    private val fileSearch: FileSearch, private val objectMapper: ObjectMapper,
    @Value("\${stub.directory.lucene-index}")
    private val indexRootDir: String,
) {

    suspend fun searchScoredDocs(numHits: Int, json: JSONObject): String {
        return fileSearch.searchDocument(numHits, json, indexRootDir)?.get("fileName") ?: throw Exception("")
    }
}