package br.com.luce.sample.service

import br.com.luce.sample.extensions.writeValueAsBytesCoroutine
import br.com.luce.sample.lucene.LuceneIndexer
import br.com.luce.sample.model.StubPayload
import br.com.luce.sample.model.RequestModel
import br.com.luce.sample.model.ResponseModel
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path

@Service
class StorageFileService(
    @Value("\${file.directory}")
    private val fileDirectory: String,
    private val objectMapper: ObjectMapper,
    private val luceneIndexer: LuceneIndexer
) {
    suspend fun writeFile(request: ServerWebExchange): ResponseModel {
        val payload = requestToPayload(request)
        val directory = createDirectoryIfNotExist(payload.requestModel.path.plus("/"))
        writeFileCoroutine(directory, payload)
        return payload.responseModel
    }

    private suspend fun writeFileCoroutine(directory: String, stubPayload: StubPayload) = withContext(Dispatchers.IO) {
        val path = Path(directory.plus(UUID.randomUUID()).plus(".json"))
        Files.write(
            path,
            objectMapper.writeValueAsBytesCoroutine(stubPayload)
        )
        luceneIndexer.index(stubPayload, path.toString())
    }

    private fun createDirectoryIfNotExist(requestPath: String): String {
        val path = fileDirectory.plus(requestPath)
        val pathExists = Files.exists(Path(path))
        if (!pathExists) Files.createDirectories(Path(path))
        return path
    }

    suspend fun requestToPayload(request: ServerWebExchange): StubPayload {
        val req = request.request
        val bodyIs = req.body.awaitFirstOrNull()?.asInputStream()
        val byteArray = withContext(Dispatchers.IO) {
            bodyIs.use { it?.readAllBytes() }
        }
        val request1 = RequestModel(
            path = req.path.toString().replace("/teste", ""),
            requestBody = objectMapper.readTree(byteArray?.let { String(it).trimIndent() }),
            requestHeaders = req.headers,
            queryParams = req.queryParams,
            method = req.method.name(),
        )
        val res = objectMapper.writeValueAsString(mapOf("id" to UUID.randomUUID().toString().plus(request1.method)))


        val response = ResponseModel(body = objectMapper.readTree(res), HttpHeaders())
        return StubPayload(request1, response)
    }
}