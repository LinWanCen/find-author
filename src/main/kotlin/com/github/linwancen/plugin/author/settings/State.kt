package com.github.linwancen.plugin.author.settings

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope

data class State(val project : Project) {
    val ignore = IgnoreState.of(project)
    val input = InputState.of(project)
    val option = OptionState.of()
    val projectScope = GlobalSearchScope.projectScope(project)
}
