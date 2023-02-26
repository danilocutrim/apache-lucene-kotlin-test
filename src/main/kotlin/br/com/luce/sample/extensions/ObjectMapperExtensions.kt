package br.com.luce.sample.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ObjectMapper.writeValueAsBytesCoroutine(value: Any): ByteArray = withContext(Dispatchers.IO) {
    return@withContext this@writeValueAsBytesCoroutine.writeValueAsBytes(value)
}