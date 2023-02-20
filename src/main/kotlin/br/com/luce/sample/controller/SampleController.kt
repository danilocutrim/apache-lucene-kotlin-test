package br.com.luce.sample.controller

import br.com.luce.sample.request.RequestModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@RestController
class SampleController(private val objectMapper: ObjectMapper) {

    @RequestMapping(path = ["/**"], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun handler(request: ServerWebExchange): JsonNode? {
        val req = request.request
        val byteArray = req.body.awaitFirstOrNull()?.asInputStream()?.readAllBytes()
        val payload = RequestModel(
            path = req.path.toString(),
            body = objectMapper.readTree(byteArray?.let { String(it).trimIndent() }),
            headers = req.headers,
            queryParams = req.queryParams,
            method = req.method.name(),
        )

        withContext(Dispatchers.IO) {
            Files.write(
                Path.of(
                    "/home/danilo/Workspace/apache-lucene-kotlin-test/payloads/".plus(UUID.randomUUID()).plus(".json")
                ),
                objectMapper.writeValueAsBytes(payload)
            )
        }
        return payload.body


    }
}

//Qp@GAYqdcs3D