package com.github.linwancen.plugin.author.comment

import com.github.linwancen.plugin.author.git.GitInfo

abstract class DataService {
    companion object {
        @JvmStatic
        val SERVICES = mutableMapOf<String, DataService>()

        @JvmStatic
        fun needPutMap(format: String): Map<String, DataService> {
            val map = mutableMapOf<String, DataService>()
            for (service in SERVICES) {
                if (service.value.needPut(format)) {
                    map[service.key] = service.value
                }
            }
            return map
        }

        @JvmStatic
        fun putAllData(
            gitInfo: GitInfo,
            format: String,
            map: MutableMap<String, String>,
            needPutMap: Map<String, DataService>
        ): String {
            var result = format
            for (impl in needPutMap.values) {
                val newFormat = impl.putData(gitInfo, result, map)
                if (newFormat != null) {
                    result = newFormat
                }
            }
            return result
        }
    }

    protected abstract fun needPut(format: String): Boolean
    protected abstract fun putData(gitInfo: GitInfo, format: String, map: MutableMap<String, String>): String?
}