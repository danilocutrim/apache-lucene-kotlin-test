package br.com.luce.sample.lucene

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.json.JSONObject
import org.springframework.stereotype.Component
import kotlin.io.path.Path

@Component
class FileSearch(private val objectMapper: ObjectMapper) {


    suspend fun searchDocument(numHits: Int, json: JSONObject, indexDirectory: String): Document? = withContext(Dispatchers.IO) {
        val directory: Directory = FSDirectory.open(Path(indexDirectory))
        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
        val indexSearcher = IndexSearcher(directoryReader)
        val query = QueryFactory.query(json)
        val topDocs = indexSearcher.search(query, numHits)
        return@withContext getFoundDocuments(topDocs.scoreDocs, indexSearcher)
    }

    private fun getFoundDocuments(results: Array<ScoreDoc>, indexSearcher: IndexSearcher): Document? {
        val docId = results[0].doc
        return indexSearcher.doc(docId)
    }


//    private fun wildcardQuery(indexDir: File, numHits: Int, indexDirectory: String): JsonNode? {
//        val directory: Directory = FSDirectory.open(Path(indexDirectory))
//        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
//        val indexSearcher = IndexSearcher(directoryReader)
//        val term = Term(BODY_INBOUND_FIELD, "*@*")
//        val query: Query = WildcardQuery(term)
//        val topDocs = indexSearcher.search(query, numHits)
//        return getBody(topDocs.scoreDocs, indexSearcher)
//    }
//
//    private fun booleanQuery(indexDir: File, numHits: Int, indexDirectory: String): JsonNode? {
//        val directory: Directory = FSDirectory.open(Path(indexDirectory))
//        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
//        val indexSearcher = IndexSearcher(directoryReader)
//        val term = Term(BODY_INBOUND_FIELD, "*#*")
//        val query: Query = WildcardQuery(term)
//
//        val booleanQuery = BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST).build()
//        val topDocs = indexSearcher.search(booleanQuery, numHits)
//        return getBody(topDocs.scoreDocs, indexSearcher)
//    }
}