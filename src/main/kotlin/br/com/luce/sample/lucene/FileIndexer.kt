package br.com.luce.sample.lucene

import br.com.luce.sample.file.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.store.FSDirectory
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path

@Component
class FileIndexer {

    suspend fun indexer(indexDir: Path, dataDir: Path) = coroutineScope {
        FileUtil.createDirIfNotExists(indexDir)
        FileUtil.createDirIfNotExists(dataDir)
        val indexDirectory = FSDirectory.open(indexDir)
        val analyzer = StandardAnalyzer()
        val config = IndexWriterConfig(analyzer)
        config.openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND
        val writer = IndexWriter(indexDirectory, config)
        val dataDirtory = File(dataDir.toString())
        val files = dataDirtory.listFiles { file -> file.isFile && file.name.endsWith(".json") }
        val coroutines = files.map { file ->
            async {
                indexFile(writer, file)
            }
        }

        coroutines.awaitAll()
        writer.commit()
        writer.forceMerge(1)
        writer.close()
        indexDirectory.close()
    }


    private suspend fun indexFile(writer: IndexWriter, file: File) = withContext(Dispatchers.IO) {
        val json = JSONObject(file.readText())
        val doc = DocumentFactory.createDocument(json)
        val fileNameField = StringField("fileName", file.absolutePath, Field.Store.YES)
        doc.add(fileNameField)
        val term = Term("fileName", file.name)
        writer.updateDocument(term, doc)
    }
}