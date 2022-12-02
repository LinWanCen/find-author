package com.github.linwancen.plugin.author.settings

import com.github.linwancen.plugin.author.git.GitLog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.linwancen.plugin.author.settings.IgnoreState",
    storages = [Storage("find-author/OptionState.xml")]
)
class OptionState : PersistentStateComponent<OptionState?> {
    var format = GitLog.DEFAULT_FORMAT
    var fileEnds = "|.java"
        set(value) {
            field = value
            fileEndList = value.trim().split("|").map(String::trim)
            if (fileEndList.isEmpty()) {
                fileEndList = listOf("")
            }
        }
    var fileEndList = emptyList<String>()

    override fun getState(): OptionState {
        return this
    }

    override fun loadState(state: OptionState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        @JvmStatic
        fun of(project: Project? = null): OptionState {
            val manager = project ?: ApplicationManager.getApplication()
            return manager.getService(OptionState::class.java) ?: return OptionState()
        }
    }
}