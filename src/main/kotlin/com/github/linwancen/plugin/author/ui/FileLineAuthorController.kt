package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.author.comment.DataService
import com.github.linwancen.plugin.author.git.GitBlame
import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.git.GitLog
import com.github.linwancen.plugin.common.file.FileLine
import com.github.linwancen.plugin.common.text.Formats
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task

object FileLineAuthorController {
    @JvmStatic
    fun getAuthor(authorWindow: AuthorWindow) {
        if (authorWindow.input == null) {
            return
        }
        val project = authorWindow.project
        if (authorWindow.beforeRun()) {
            return
        }
        val lines = authorWindow.input.text.split("\n")
        object : Task.Backgroundable(project, "Find author ${lines.size}") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // In this for change new git version
                    val gitInfo = GitInfo(project)
                    val format = authorWindow.gitFormat.text
                    val needPutMap = DataService.needPutMap(format)
                    Output.output(indicator, authorWindow, lines,
                        { line: String -> line },
                        { line: String ->
                            if (line.isBlank()) "\n"
                            else {
                                val scope = gitInfo.state.projectScope
                                val fileLine = FileLine(line, gitInfo.state.option.fileEndList, project, scope)
                                var result = if (fileLine.lineNum != "0") {
                                    GitBlame.info(gitInfo, format, fileLine.filePath, fileLine.lineNum)
                                } else {
                                    GitLog.info(gitInfo, format, fileLine.filePath)
                                }
                                result = DataService.putAllData(gitInfo, result, fileLine.map, needPutMap)
                                Formats.text(result, fileLine.map)+ "\n"
                            }
                        }
                    )
                } finally {
                    authorWindow.finallyForRun()
                }
            }
        }.queue()
    }
}