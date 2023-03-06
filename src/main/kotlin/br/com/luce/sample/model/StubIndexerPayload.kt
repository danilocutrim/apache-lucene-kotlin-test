package br.com.luce.sample.model

data class StubIndexerPayload(
    val path: String?,
    val bodyInbound: String?,
    val headersInbound: String?,
    val queryParams: String?,
    val method: String? = null,
    val bodyOutbound: String?,
    val responseCode: String?,
    val responseFilePath: String?,
    val stubFilePath: String?,
    val bodyOutBoundFilePath: String?
)