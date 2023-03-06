package br.com.luce.sample.service

import br.com.luce.sample.constants.PATH_SEPARATOR
import br.com.luce.sample.extensions.toPath
import br.com.luce.sample.file.FileStub
import br.com.luce.sample.model.RequestModel
import br.com.luce.sample.model.ResponseModel
import br.com.luce.sample.model.StubFileDir
import br.com.luce.sample.model.StubPayload
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.util.*

@Service
class RequestFileService(
    private val objectMapper: ObjectMapper,
    @Value("\${stub.directory.work-dir}")
    private val workDir: String,
    @Value("\${stub.directory.request-files}")
    private val requestRootDir: String,
    @Value("\${stub.directory.response-files}")
    private val responseRootDir: String,
    private val indexerService: IndexerService,
    @Value("\${stub.directory.lucene-index}")
    private val indexDir: String,
) {
    suspend fun saveAndIndexStub(request:ServerWebExchange){
        val stubFileDir = writeFile(request)
        indexerService.indexRequest(workDir
            .plus(stubFileDir.requestFilePath.toString()).toPath(), indexDir.toPath())
    }
    suspend fun writeFile(request: ServerWebExchange): StubFileDir {
        val stubRequest = request.toStubRequest()
        val response = objectMapper.readTree(
            objectMapper.writeValueAsString(
                mapOf(
                    "id" to UUID.randomUUID().toString().plus(stubRequest.method)
                )
            )
        )
        val responseModel = ResponseModel(response, HttpHeaders())

        return saveStubs(StubPayload(stubRequest, responseModel))

    }

    suspend fun saveStubs(stubPayload: StubPayload): StubFileDir {
        val stubId = UUID.randomUUID().toString()
        val requestFilePath = writeRequestPayloadAsJson(stubPayload, stubId).toPath()
        val responseFilePath = writeResponsePayloadAsJson(stubPayload, stubId).toPath()
        return StubFileDir(requestFilePath, responseFilePath)
    }

    private suspend fun writeRequestPayloadAsJson(stubPayload: StubPayload, stubId: String): String {
        val stubFilePath = getRequestFilePath(stubPayload.requestModel.path)
        val fileStub = FileStub(objectMapper, workDir.toPath())
        fileStub.writeFile(stubPayload.requestModel, stubFilePath, stubId)
        return stubFilePath
    }

    private suspend fun writeResponsePayloadAsJson(stubPayload: StubPayload, stubId: String): String {
        val stubFilePath = getResponseFilePath(stubPayload.requestModel.path)
        val fileStub = FileStub(objectMapper, workDir.toPath())
        fileStub.writeFile(stubPayload.responseModel, stubFilePath, stubId)
        return stubFilePath
    }
    private fun getResponseFilePath(requestPath: String): String {
        return responseRootDir.plus(requestPath).plus(PATH_SEPARATOR)
    }

    private fun getRequestFilePath(requestPath: String): String {
        return requestRootDir.plus(requestPath).plus(PATH_SEPARATOR)
    }

    suspend fun ServerWebExchange.toStubRequest(): RequestModel {
        val req = this.request
        val bodyIs = req.body.awaitFirstOrNull()?.asInputStream()
        val byteArray = withContext(Dispatchers.IO) {
            bodyIs.use { it?.readAllBytes() }
        }
        val map = mutableMapOf<String, String>()
        var header = req.headers.map {
            if (!it.key.equals("Postman-Token")) map.put(it.key, it.value.toString())
        }

        return RequestModel(
            path = req.path.toString().replaceFirst("/test", ""),
            requestBody = objectMapper.readTree(byteArray?.let { String(it).trimIndent() }),
            requestHeaders = map,
            queryParams = req.queryParams,
            method = req.method.name(),
        )
    }
}