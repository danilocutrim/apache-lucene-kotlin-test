package br.com.luce.sample.file

import java.nio.file.Files
import java.nio.file.Path

class FileUtil {
    companion object {
        fun createDirIfNotExists(dir: Path) {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir)
            }
        }
    }
}