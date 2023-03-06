package br.com.luce.sample.file

import br.com.luce.sample.extensions.toPath
import br.com.luce.sample.extensions.writeValueAsBytesCoroutine
import br.com.luce.sample.model.RequestModel
import br.com.luce.sample.model.ResponseModel
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

class FileStub(private val objectMapper: ObjectMapper, private val stubRootDir: Path) {

//    suspend fun writeFile(stubPayload: RequestModel) {
//        val directory = createStubRequestDirIfNotExists(stubPayload.path)
//        writeFileCoroutine(directory.toString(), stubPayload)
//    }

    suspend fun writeFile(requestModel: RequestModel, filePath: String, stubId: String) {
        val directory = createStubRequestDirIfNotExists(filePath)
        writeFileCoroutine(directory.toString(), requestModel, stubId)
    }

    suspend fun writeFile(responseModel: ResponseModel, filePath: String, stubId: String) {
        val directory = createStubRequestDirIfNotExists(filePath)
        writeFileCoroutine(directory.toString(), responseModel, stubId)
    }

    private suspend fun writeFileCoroutine(directory: String, requestModel: RequestModel, stubId: String) =
        withContext(Dispatchers.IO) {
            val path = Path(directory.plus("/").plus(stubId).plus(".json"))
            Files.write(
                path,
                objectMapper.writeValueAsBytesCoroutine(requestModel)
            )
        }

    private suspend fun writeFileCoroutine(directory: String, responseModel: ResponseModel, stubId: String) =
        withContext(Dispatchers.IO) {
            val path = Path(directory.plus("/").plus(stubId).plus(".json"))
            Files.write(
                path,
                objectMapper.writeValueAsBytesCoroutine(responseModel)
            )
        }

    private fun createStubRequestDirIfNotExists(stubRequestPath: String): Path {
        val path = stubRootDir.toString().plus(stubRequestPath).toPath()
        Files.createDirectories(path)
        return path
    }

}