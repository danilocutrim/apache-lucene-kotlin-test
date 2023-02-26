package br.com.luce.sample.lucene

import br.com.luce.sample.request.RequestModel
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.WildcardQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import kotlin.io.path.Path


@Component
class LuceneSearch(
    @Value("\${lucene.index.directory}")
    private val indexDirectory: String,
    private val objectMapper: ObjectMapper
) {

    /**
     * Example of a TermQuery. Finds all tweets by the user "scotthamilton"
     */
    @Throws(Exception::class)
    fun termSearch(numHits: Int, requestModel: RequestModel): JsonNode? {
        val directory: Directory = FSDirectory.open(Path(indexDirectory))
        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
        val indexSearcher = IndexSearcher(directoryReader)
        val term = Term(PATH_FIELD, requestModel.path.replace("/virtual",""))
        val query: Query = TermQuery(term)
        val topDocs = indexSearcher.search(query, numHits)
        return getBody(topDocs.scoreDocs, indexSearcher)
    }

    private fun getBody(results: Array<ScoreDoc>, indexSearcher: IndexSearcher): JsonNode? {
            val docId = results[0].doc
            val foundDocument = indexSearcher.doc(docId)
            println(foundDocument[PATH_FIELD].plus(foundDocument[FILE_PATH]))

        return objectMapper.readTree(foundDocument["body"])
    }

    /**
     * Example of a wildcard query. Finds tweets that are replies (begin with an @).
     */
//    @Throws(Exception::class)
//    private fun wildcardQuery(indexDir: File, numHits: Int) {
//        println("Find tweets that mention another user:")
//        val directory: Directory = FSDirectory.open(indexDirectory)
//        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
//        val indexSearcher = IndexSearcher(directoryReader)
//        val term = Term(TEXT, "*@*")
//        val query: Query = WildcardQuery(term)
//        val topDocs = indexSearcher.search(query, numHits)
//        printResults(topDocs.scoreDocs, indexSearcher)
//    }

//    /**
//     * Example of a boolean query. Finds tweets that are positve AND include a hash tag.
//     */
//    @Throws(Exception::class)
//    private fun booleanQuery(indexDir: File, numHits: Int) {
//        println("Find tweets with a positive polarity that include a #hashtag:")
//        val directory: Directory = FSDirectory.open(indexDirectory)
//        val directoryReader: DirectoryReader = DirectoryReader.open(directory)
//        val indexSearcher = IndexSearcher(directoryReader)
//        val booleanQuery = BooleanQuery.Builder()
//        val term = Term(TEXT, "*#*")
//        val query: Query = WildcardQuery(term)
//        val term1 = Term(POLARITY, "4")
//        val query1: Query = TermQuery(term1)
//        booleanQuery.add(query, BooleanClause.Occur.MUST)
//        booleanQuery.add(query1, BooleanClause.Occur.MUST)
//        val topDocs = indexSearcher.search(booleanQuery, numHits)
//        printResults(topDocs.scoreDocs, indexSearcher)
//    }

    /**
     * Prints out the user and the content of the tweet.
//     */
//    @Throws(Exception::class)
    private fun printResults(results: Array<ScoreDoc>, indexSearcher: IndexSearcher) {
        println("----------------------------------------------------------------------")
        for (i in results.indices) {
            val docId = results[i].doc
            val foundDocument = indexSearcher.doc(docId)
            println(foundDocument[PATH_FIELD].plus(foundDocument[FILE_PATH]))
        }
        println("Found " + results.size + " results")
        println("----------------------------------------------------------------------")
    }

    companion object {
        const val PATH_FIELD = "path"
        const val BODY_FIELD = "requestBody"
        const val HEADERS_FIELD = "requestHeaders"
        const val FILE_PATH = "filePath"
    }
}