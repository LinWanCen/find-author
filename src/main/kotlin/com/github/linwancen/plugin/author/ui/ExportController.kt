package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.author.git.GitBlame
import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.module.export.ExportService
import com.github.linwancen.plugin.common.TaskTool
import com.github.linwancen.plugin.common.text.Formats
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.usages.UsageView

object ExportController {

    @JvmStatic
    fun print(
        project: Project,
        usageView: UsageView,
    ) {
        val authorWindow = AuthorWindowFactory.winMap[project] ?: return
        authorWindow.beforeRun()
        object : Task.Backgroundable(project, "Export find ${TaskTool.time()}") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // In this for change new git version
                    val gitInfo = GitInfo(project)
                    var format = gitInfo.state.option.findFormat
                    val needBlame = format.contains("\${git")
                    if (!needBlame) {
                        ExportService.scan(usageView) {
                            authorWindow.output.text += Formats.text(format, it)
                        }
                        return
                    }
                    format = Formats.text(format, ExportService.BLAME_MAP, null)
                    val lines = mutableListOf<MutableMap<String, String>>()
                    ExportService.scan(usageView) {
                        lines.add(it)
                    }
                    Output.output(indicator, authorWindow, lines,
                        { line: MutableMap<String, String> -> line["fileName"] ?: "" },
                        { line: MutableMap<String, String> ->
                            val filePath = line["filePath"] ?: return@output "\n"
                            val lineNum = line["lineNum"] ?: return@output "\n"
                            val info = GitBlame.info(gitInfo, format, filePath, lineNum)
                            Formats.text(info, line) + "\n"
                        }
                    )
                } finally {
                    authorWindow.finallyForRun()
                }
            }
        }.queue()
    }
}