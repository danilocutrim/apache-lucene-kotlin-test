package br.com.luce.sample.controller

import br.com.luce.sample.model.StubFileDir
import br.com.luce.sample.service.IndexerService
import br.com.luce.sample.service.RequestFileService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange


@RestController
class IndexerController(
    private val indexerService: IndexerService, private val requestFileService: RequestFileService
) {

//    @RequestMapping(
//        path = ["/test/**"],
//        method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PUT]
//    )
//    suspend fun handler(request: ServerWebExchange) {
//        return indexerService.writeFileIndexed(request)
//    }

    @RequestMapping(
        path = ["/test/**"],
        method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PUT]
    )
    suspend fun handler(request: ServerWebExchange) {
        return requestFileService.saveAndIndexStub(request)
    }
}

