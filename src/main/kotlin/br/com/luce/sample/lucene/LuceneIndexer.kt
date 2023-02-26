package br.com.luce.sample.lucene

import br.com.luce.sample.request.Payload
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StringField
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import kotlin.io.path.Path

@Component
class LuceneIndexer(
    @Value("\${lucene.index.directory}")
    private val indexDirectory: String,
    private val objectMapper: ObjectMapper
) {
    @Throws(Exception::class)
    fun index(payload: Payload, filePath: String) {
        val indexWriter = IndexWriter(
            FSDirectory.open(Path(indexDirectory)),
            IndexWriterConfig(StandardAnalyzer())
        )
        indexFile(indexWriter, payload, filePath)
        indexWriter.close()
    }

    @Throws(IOException::class)
    private fun indexFile(indexWriter: IndexWriter, payload: Payload, filePath: String) {
        val fieldType = FieldType()
        fieldType.setStored(true)
        fieldType.setIndexOptions(IndexOptions.DOCS)
        fieldType.setTokenized(false)
        val document = Document()
        document.add(StringField(PATH_FIELD, payload.requestModel.path, Field.Store.YES))
        document.add(StringField(BODY_FIELD, objectMapper.writeValueAsString(payload.requestModel.requestBody)?.trimIndent(), Field.Store.YES))
        document.add(
            Field(
                HEADERS_FIELD,
                objectMapper.writeValueAsString(payload.responseModel.headers).trimIndent(),
                fieldType
            )
        )
        document.add(StringField(FILE_PATH, filePath, Field.Store.YES))
        document.add(Field(RESPONSE_BODY, objectMapper.writeValueAsString(payload.responseModel.body), fieldType))
        indexWriter.addDocument(document)
    }

    companion object {
        const val PATH_FIELD = "path"
        const val BODY_FIELD = "requestBody"
        const val HEADERS_FIELD = "requestHeaders"
        const val FILE_PATH = "filePath"
        const val RESPONSE_BODY = "body"
    }
}