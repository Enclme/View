package io.github.username.project.config

import io.github.username.project.ExamplePlugin.bukkitPlugin
import io.github.username.project.config.util.addWatcher
import io.github.username.project.config.util.getFiles
import io.github.username.project.config.util.getType
import io.github.username.project.config.util.toConfig
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class Config private constructor(
    val path: String,
    val defaultFiles: MutableList<String>,
    val fileTypes: MutableList<Type>
) {

    private val configMap: ConcurrentHashMap<String, Configuration> = ConcurrentHashMap()

    fun getConfig(id: String): Configuration {
        return configMap[id] ?: throw IllegalStateException("Configuration $id not found.")
    }

    fun init() = this.apply {
        defaultFiles.forEach { defaultFile ->
            val path = if (this.path.isNotBlank()) "${this.path}/${defaultFile}" else defaultFile
            val file = File(bukkitPlugin.dataFolder, path)
            val resourceUrl = javaClass.classLoader.getResource(path)
            if (!file.exists() && resourceUrl != null) {
                releaseResourceFile(path)
            }
        }
    }

    fun load(autoReload: Boolean = false) = this.apply {
        configMap.clear()
        val path = if (this.path.isNotBlank()) "${bukkitPlugin.dataFolder.path}/${this.path}" else bukkitPlugin.dataFolder.path
        File(path).getFiles().forEach { file ->
            val fileType = file.getType()
            if (this.fileTypes.contains(fileType)) {
                val id = file.relativeTo(bukkitPlugin.dataFolder).path.replace(File.separator, "/")
                configMap[id] = file.toConfig(fileType)
                if(autoReload){
                    file.addWatcher(false) { onFileChanged(id, file, fileType) }
                }
            }
        }
    }

    private fun onFileChanged(id: String, file: File, fileType: Type) {
        configMap[id] = file.toConfig(fileType)
    }

    class Builder {
        var path = ""
        private val defaultFiles = mutableListOf<String>()
        private val fileTypes = mutableListOf<Type>()

        fun setPath(path: String) = apply { this.path = path }
        fun addDefaultFile(defaultFile: String) = apply { this.defaultFiles.add(defaultFile) }
        fun addFileType(fileType: Type) = apply { this.fileTypes.add(fileType) }

        fun build() = Config(
            path,
            if (defaultFiles.isEmpty()) mutableListOf("config.yml") else defaultFiles,
            if (fileTypes.isEmpty()) mutableListOf(Type.YAML) else fileTypes
        )
    }
}