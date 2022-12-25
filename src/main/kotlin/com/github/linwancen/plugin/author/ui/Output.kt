package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.common.TaskTool
import com.intellij.openapi.progress.ProgressIndicator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

class Output(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    companion object {
        private val o = Output()

        @JvmStatic
        fun <I, T : Collection<I>> output(
            indicator: ProgressIndicator,
            authorWindow: AuthorWindow,
            items: T,
            toText2: (I) -> String,
            toLineStr: (I) -> String,
        ): Boolean {
            return o.launch(indicator, authorWindow, items, toText2, toLineStr)
        }
    }

    fun <I, T : Collection<I>> launch(
        indicator: ProgressIndicator,
        authorWindow: AuthorWindow,
        items: T,
        toText2: (I) -> String,
        toLineStr: (I) -> String,
    ): Boolean {
        val map = ConcurrentHashMap<Int, Channel<String>>()
        val taskTool = TaskTool(indicator, items.size)
        for (index in items.indices) {
            map[index] = Channel()
        }
        runBlocking {
            launch(dispatcher) {
                for ((index, item) in items.withIndex()) {
                    authorWindow.output.text += map[index]?.receive()
                    taskTool.beforeNext(index, toText2.invoke(item))
                }
            }
            for ((index, line) in items.withIndex()) {
                launch(dispatcher) {
                    val result = if (indicator.isCanceled) {
                        "\n"
                    } else {
                        toLineStr.invoke(line)
                    }
                    map[index]?.send(result)
                    map[index]?.close()
                }
            }
        }
        return true
    }
}