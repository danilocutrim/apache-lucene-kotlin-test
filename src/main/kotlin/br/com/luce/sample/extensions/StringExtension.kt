package br.com.luce.sample.extensions

import kotlin.io.path.Path

fun String.toPath() = Path(this)
fun String.normalizePath(): String {
    return this.removePrefix("/test").removePrefix("/indexer")
}