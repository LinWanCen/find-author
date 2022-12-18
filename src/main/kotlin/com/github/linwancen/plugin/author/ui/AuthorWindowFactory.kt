package com.github.linwancen.plugin.author.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class AuthorWindowFactory : ToolWindowFactory {
    companion object{
       val winMap = mutableMapOf<Project, AuthorWindow>()
    }
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val authorWindow = AuthorWindow(project, toolWindow)
        val iterator = winMap.iterator()
        for (entry in iterator) {
            if (entry.key.isDisposed) {
                iterator.remove()
            }
        }
        winMap[project] = authorWindow
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(authorWindow.mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}