package br.com.luce.sample.service

import br.com.luce.sample.extensions.toPath
import br.com.luce.sample.lucene.FileIndexer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*

@Service
class IndexerService(
    private val luceneIndexer: FileIndexer,
) {

    suspend fun indexRequest(dataDir: Path, indexDir: Path) =
        withContext(Dispatchers.IO) {
            luceneIndexer.indexer(indexDir, dataDir)
        }
}