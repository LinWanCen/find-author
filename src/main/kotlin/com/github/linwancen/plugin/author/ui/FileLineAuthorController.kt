package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.module.fileline.FileLineAuthorService
import com.github.linwancen.plugin.common.TaskTool
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task

object FileLineAuthorController {
    @JvmStatic
    fun getAuthor(
        authorWindow: AuthorWindow,
    ) {
        if (authorWindow.input == null) {
            return
        }
        val project = authorWindow.project
        authorWindow.beforeRun()
        val lines = authorWindow.input.text.split("\n")
        object : Task.Backgroundable(project, "Find author ${TaskTool.time()}") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // In this for change new git version
                    val gitInfo = GitInfo(project)
                    Output.output(indicator, authorWindow, lines,
                        { line: String -> line },
                        { line: String ->
                            if (line.isBlank()) "\n"
                            else FileLineAuthorService.outLine(gitInfo, line, authorWindow.gitFormat.text) + "\n"
                        }
                    )
                } finally {
                    authorWindow.finallyForRun()
                }
            }
        }.queue()
    }
}