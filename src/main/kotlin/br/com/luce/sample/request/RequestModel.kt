package br.com.luce.sample.request

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpHeaders
import org.springframework.util.MultiValueMap

data class RequestModel(
    val path: String,
    val requestBody: JsonNode? = null,
    val requestHeaders: HttpHeaders,
    val queryParams: MultiValueMap<String, String>? = null,
    val method: String,
)