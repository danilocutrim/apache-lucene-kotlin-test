package br.com.luce.sample.request

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpHeaders
import org.springframework.util.MultiValueMap

data class ResponseModel(
    val body: JsonNode? = null,
    val headers: HttpHeaders,
)