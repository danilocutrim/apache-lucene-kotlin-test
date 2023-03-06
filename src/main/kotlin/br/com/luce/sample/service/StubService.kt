package br.com.luce.sample.service

import br.com.luce.sample.model.RequestModel
import br.com.luce.sample.model.ResponseModel
import br.com.luce.sample.model.StubPayload
import br.com.luce.sample.repository.impl.StubResponseRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.util.*

@Service
class StubService(
    private val objectMapper: ObjectMapper,
    private val searchService: SearchService,
    private val stubResponseRepository: StubResponseRepository
) {
    suspend fun getStubResponse(request: ServerWebExchange): ResponseEntity<JsonNode> {
        val req = requestToPayload(request).requestModel
        val requestModel = objectMapper.writeValueAsString(req)
        val requestModelJson = JSONObject(requestModel)
        val id = searchService.searchScoredDocs(1, requestModelJson)
        return newResponseEntity(findResponseById(id,req.path))
    }

    private fun newResponseEntity(json: JsonNode): ResponseEntity<JsonNode> {
        val header = convertHeader(json["headers"])

        return ResponseEntity<JsonNode>(json.get("body"), header, HttpStatusCode.valueOf(200))
    }

    fun convertHeader(json: JsonNode): HttpHeaders {
        val responseHeaders = HttpHeaders()
        for (field in json.fieldNames()) {
            val value = json[field]
            responseHeaders[field] = value.textValue()
        }
        return responseHeaders
    }


    private suspend fun findResponseById(fileName: String, path: String): JsonNode {
        return stubResponseRepository.findResponseByIdAndPath(id = fileName, path)?:throw Exception("")
    }

    suspend fun requestToPayload(request: ServerWebExchange): StubPayload {
        val req = request.request
        val bodyIs = req.body.awaitFirstOrNull()?.asInputStream()
        val byteArray = withContext(Dispatchers.IO) {
            bodyIs.use { it?.readAllBytes() }
        }
        val map = mutableMapOf<String, String>()
        req.headers.map {
            if (!it.key.equals("Postman-Token")) map.put(it.key, it.value.toString())
        }
        val request1 = RequestModel(
            path = req.path.toString().replace("/virtual", ""),
            requestBody = objectMapper.readTree(byteArray?.let { String(it).trimIndent() }),
            requestHeaders = map,
            queryParams = req.queryParams,
            method = req.method.name(),
        )
        val res = objectMapper.writeValueAsString(mapOf("id" to UUID.randomUUID().toString().plus(request1.method)))


        val response = ResponseModel(body = objectMapper.readTree(res), HttpHeaders())
        return StubPayload(request1, response)
    }
}