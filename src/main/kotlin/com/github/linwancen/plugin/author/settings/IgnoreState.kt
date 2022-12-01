package com.github.linwancen.plugin.author.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.linwancen.plugin.author.settings.IgnoreState",
    storages = [Storage("find-author/IgnoreState.xml")]
)
class IgnoreState : PersistentStateComponent<IgnoreState?> {
    var userPattern = ""
    var user = ""
        set(value) {
            field = value
            userPattern = value.trim().replace(LINE_REGEXP, "|")
        }
    var msgPattern = ""
    var msg  = ""
        set(value) {
            field = value
            msgPattern = value.trim().replace(LINE_REGEXP, "|")
        }
    var revPattern = ""
    var rev  = ""
        set(value) {
            field = value
            revPattern = value.trim().replace(LINE_REGEXP, "|")
        }

    override fun getState(): IgnoreState {
        return this
    }

    override fun loadState(state: IgnoreState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        @JvmStatic
        val LINE_REGEXP = Regex("\\s*[\\r\\n]+\\s*")
        @JvmStatic
        fun of(project: Project? = null): IgnoreState {
            val manager = project ?: ApplicationManager.getApplication()
            return manager.getService(IgnoreState::class.java) ?: return IgnoreState()
        }
    }
}