package br.com.luce.sample.controller

import br.com.luce.sample.service.SearchService
import br.com.luce.sample.service.StubService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange


@RestController
class MockController(
    private val stubService: StubService
) {

    @RequestMapping(path = ["/virtual/**"], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun handler2(request: ServerWebExchange): ResponseEntity<JsonNode> {
        return stubService.getStubResponse(request)
    }

}

