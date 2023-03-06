package br.com.luce.sample.repository.impl

import com.fasterxml.jackson.databind.JsonNode

interface StubResponseRepository {
    suspend fun findResponseByIdAndPath(id: String, path: String): JsonNode?
}