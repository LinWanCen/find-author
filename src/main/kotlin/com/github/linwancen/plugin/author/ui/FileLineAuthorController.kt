package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.module.fileline.FileLineAuthorService
import com.github.linwancen.plugin.common.TaskTool
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

object FileLineAuthorController {
    @JvmStatic
    fun getAuthor(
        authorWindow: AuthorWindow
    ) {
        if (authorWindow.input == null) {
            return
        }
        val project = authorWindow.project
        authorWindow.gitAuthor.isEnabled = false
        authorWindow.output.text = ""
        authorWindow.tab.selectedComponent = authorWindow.outTab
        val lines = authorWindow.input.text.split("\n")
        object : Task.Backgroundable(project, "Find author ${TaskTool.time()}") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val map = ConcurrentHashMap<Int, Channel<String>>()
                    val latch = CountDownLatch(lines.size)
                    val taskTool = TaskTool(indicator, lines.size)
                    for (index in lines.indices) {
                        map[index] = Channel()
                    }
                    // In this for change new git version
                    val gitInfo = GitInfo(project)
                    GlobalScope.launch {
                        for ((index, line) in lines.withIndex()) {
                            try {
                                authorWindow.output.text += map[index]?.receive()
                                taskTool.beforeNext(index, line)
                            } finally {
                                latch.countDown()
                            }
                        }
                    }
                    for ((index, line) in lines.withIndex()) {
                        if (indicator.isCanceled) {
                            return
                        }
                        GlobalScope.launch {
                            val result = if (line.isBlank() || indicator.isCanceled) {
                                "\n"
                            } else {
                                FileLineAuthorService.outLine(gitInfo, line, authorWindow.format.text) + "\n"
                            }
                            map[index]?.send(result)
                            map[index]?.close()
                        }
                    }
                    latch.await()
                } finally {
                    authorWindow.gitAuthor.isEnabled = true
                }
            }
        }.queue()
    }
}