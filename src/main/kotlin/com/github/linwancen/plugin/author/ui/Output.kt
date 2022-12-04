package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.common.TaskTool
import com.intellij.openapi.progress.ProgressIndicator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

object Output {
    @JvmStatic
    fun <I, T : Collection<I>> output(
        indicator: ProgressIndicator,
        authorWindow: AuthorWindow,
        lines: T,
        toText2: (I) -> String,
        toLineStr: (I) -> String,
    ): Boolean {
        val map = ConcurrentHashMap<Int, Channel<String>>()
        val latch = CountDownLatch(lines.size)
        val taskTool = TaskTool(indicator, lines.size)
        for (index in lines.indices) {
            map[index] = Channel()
        }
        GlobalScope.launch {
            for ((index, line) in lines.withIndex()) {
                try {
                    authorWindow.output.text += map[index]?.receive()
                    taskTool.beforeNext(index, toText2.invoke(line))
                } finally {
                    latch.countDown()
                }
            }
        }
        for ((index, line) in lines.withIndex()) {
            if (indicator.isCanceled) {
                return false
            }
            GlobalScope.launch {
                val result = if (indicator.isCanceled) {
                    "\n"
                } else {
                    toLineStr.invoke(line)
                }
                map[index]?.send(result)
                map[index]?.close()
            }
        }
        latch.await()
        return true
    }
}