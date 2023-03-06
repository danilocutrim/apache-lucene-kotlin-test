package br.com.luce.sample.model

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpHeaders

data class ResponseModel(
    val body: JsonNode? = null,
    val headers: HttpHeaders,
)