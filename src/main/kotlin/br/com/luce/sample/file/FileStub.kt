package br.com.luce.sample.file

import br.com.luce.sample.extensions.toPath
import br.com.luce.sample.extensions.writeValueAsBytesCoroutine
import br.com.luce.sample.model.ResponseModel
import br.com.luce.sample.model.StubPayload
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

class FileStub(private val objectMapper: ObjectMapper, private val rootDirectory: Path) {

    suspend fun writeFile(stubPayload: StubPayload): ResponseModel {
        val directory = createStubRequestDirIfNotExists(stubPayload.requestModel.path.plus("/").toPath())
        writeFileCoroutine(directory.toString(), stubPayload)
        return stubPayload.responseModel
    }

    private suspend fun writeFileCoroutine(directory: String, stubPayload: StubPayload) = withContext(Dispatchers.IO) {
        val path = Path(directory.plus(UUID.randomUUID()).plus(".json"))
        Files.write(
            path,
            objectMapper.writeValueAsBytesCoroutine(stubPayload)
        )
    }

    private fun createStubRequestDirIfNotExists(stubRequestPath: Path): Path {
        val path = rootDirectory.toString().plus(stubRequestPath.toString()).toPath()
        Files.createDirectories(path)
        return path
    }

}