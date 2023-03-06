package br.com.luce.sample.repository.`interface`

import br.com.luce.sample.constants.JSON_EXTENSION
import br.com.luce.sample.repository.impl.StubResponseRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class StubResponseRepositoryImpl(
    @Value("\${stub.directory.work-dir}")
    private val workDir: String,
    @Value("\${stub.directory.response-files}")
    private val responseRootDir: String,
    private val objectMapper: ObjectMapper
) : StubResponseRepository {
    override suspend fun findResponseByIdAndPath(id: String, path: String): JsonNode? = withContext(Dispatchers.IO){
        val findPath = workDir.plus(responseRootDir).plus("/").plus(path).plus("/").plus(id).plus(JSON_EXTENSION)
        val file = File(findPath)
        return@withContext objectMapper.readTree(file)
    }
}