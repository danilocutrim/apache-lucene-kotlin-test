package br.com.luce.sample.controller

import br.com.luce.sample.lucene.LuceneSearch
import br.com.luce.sample.model.ResponseModel
import br.com.luce.sample.service.StorageFileService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange


@RestController
class RequestFileController(private val storageFileService: StorageFileService, private val luceneSearch: LuceneSearch) {

    @RequestMapping(path = ["/teste/**"], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun handler(request: ServerWebExchange): ResponseModel {
        return storageFileService.writeFile(request)
    }

    @RequestMapping(path = ["/virtual/**"], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun handler2(request: ServerWebExchange): JsonNode? {
        return luceneSearch.termSearch(1,storageFileService.requestToPayload(request).requestModel)
    }
}

