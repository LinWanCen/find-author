package com.github.linwancen.plugin.author.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.linwancen.plugin.author.settings.InputState",
    storages = [Storage("find-author/InputState.xml")]
)
class InputState : PersistentStateComponent<InputState?> {
    var input = ""

    override fun getState(): InputState {
        return this
    }

    override fun loadState(state: InputState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        @JvmStatic
        fun of(project: Project? = null): InputState {
            val manager = project ?: ApplicationManager.getApplication()
            return manager.getService(InputState::class.java) ?: return InputState()
        }
    }
}