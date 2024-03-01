package io.github.username.project.config.util

import taboolib.common5.FileWatcher
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

fun File.toConfig(type: Type): Configuration {
    return Configuration.loadFromFile(this, type)
}

fun File.getType(): Type {
    return Configuration.getTypeFromFile(this)
}

fun File.getFiles(dir: File = this): List<File> {
    val files = mutableListOf<File>()
    dir.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            files.addAll(getFiles(file))
        } else {
            files.add(file)
        }
    }
    return files
}

inline fun File.addWatcher(runFirst: Boolean = false, crossinline func: (File.() -> Unit)): File {
    if (FileWatcher.INSTANCE.hasListener(this)) return this
    FileWatcher.INSTANCE.addSimpleListener(this, { func(this) }, runFirst)
    return this
}
