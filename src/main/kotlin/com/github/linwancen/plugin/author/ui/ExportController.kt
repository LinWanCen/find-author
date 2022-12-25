package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.author.comment.DataService
import com.github.linwancen.plugin.author.find.ExportFind
import com.github.linwancen.plugin.author.git.GitBlame
import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.common.text.Formats
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.usages.UsageView

object ExportController {

    @JvmStatic
    fun print(project: Project, usageView: UsageView) {
        // let it init
        ToolWindowManager.getInstance(project).getToolWindow("Author")?.activate(null)
        val authorWindow = AuthorWindowFactory.winMap[project] ?: return
        if (authorWindow.beforeRun()) {
            // tip it
            return
        }
        object : Task.Backgroundable(project, "Export find ${usageView.usagesCount}") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    authorWindow.exportName = ExportFind.key(usageView)
                    val mapList = mutableListOf<MutableMap<String, String>>()
                    ExportFind.scan(usageView) {
                        mapList.add(it)
                    }
                    // In this for change new git version
                    val gitInfo = GitInfo(project)
                    var format = gitInfo.state.option.findFormat
                    val needPutMap = DataService.needPutMap(format)
                    val needBlame = format.contains("\${git")
                    if (needBlame) {
                        format = Formats.text(format, GitBlame.BLAME_MAP, null)
                    }
                    Output.output(indicator, authorWindow, mapList,
                        { map: MutableMap<String, String> -> map["fileName"] ?: "" },
                        { map: MutableMap<String, String> ->
                            var result = format
                            if (needBlame) {
                                result = GitBlame.info(gitInfo, result, map["filePath"], map["lineNum"])
                            }
                            result = DataService.putAllData(gitInfo, result, map, needPutMap)
                            Formats.text(result, map) + "\n"
                        }
                    )
                } finally {
                    authorWindow.finallyForRun()
                }
            }
        }.queue()
    }
}