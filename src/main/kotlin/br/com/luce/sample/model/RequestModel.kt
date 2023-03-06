package br.com.luce.sample.model

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpHeaders
import org.springframework.util.MultiValueMap

class RequestModel(
    val path: String,
    val requestBody: JsonNode? = null,
    val requestHeaders: Map<String,String>,
    val queryParams: MultiValueMap<String, String>? = null,
    val method: String
)