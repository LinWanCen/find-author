package com.github.linwancen.plugin.author.module.export

import com.intellij.usages.UsageInfo2UsageAdapter
import com.intellij.usages.UsageView
import com.intellij.usages.impl.GroupNode
import com.intellij.usages.impl.UsageNode
import com.intellij.usages.impl.UsageViewImpl
import java.util.function.Consumer
import javax.swing.tree.TreeNode

object ExportService {

    private const val GROUPING_RULE = "GroupingRule"
    private const val DELETE_GROUPING_RULE_LEN = "GroupingRule".length - 1

    @JvmStatic
    val BLAME_MAP = mapOf(
        "gitName" to "%an",
        "gitTime" to "%ci",
        "gitHash" to "%h"
    )

    @JvmStatic
    fun scan(usageView: UsageView, consumer: Consumer<MutableMap<String, String>>) {
        if (usageView !is UsageViewImpl) {
            return
        }
        val lastChild = usageView.root.lastChild
        recursiveScan(emptyMap(), lastChild, usageView, consumer)
    }

    @JvmStatic
    private fun recursiveScan(
        map: Map<String, String>,
        treeNode: TreeNode,
        usageView: UsageView,
        consumer: Consumer<MutableMap<String, String>>,
    ) {
        val children = treeNode.children() ?: return
        for (child in children) {
            if (child is GroupNode) {
                val childMap = mutableMapOf<String, String>()
                childMap.putAll(map)
                var simpleName = child.group.javaClass.enclosingClass.simpleName
                if (simpleName.endsWith(GROUPING_RULE)) {
                    simpleName = simpleName.substring(0, simpleName.lastIndex - DELETE_GROUPING_RULE_LEN)
                }
                val text = child.group.getText(usageView)
                childMap[simpleName] = text
                childMap["${(childMap.size + 1) / 2}"] = text
                recursiveScan(childMap, child, usageView, consumer)
            } else if (child is UsageNode) {
                val info = child.userObject
                if (info is UsageInfo2UsageAdapter) {
                    val childMap = mutableMapOf<String, String>()
                    childMap.putAll(map)
                    childMap["filePath"] = info.file.path
                    childMap["fileName"] = info.file.name
                    childMap["lineNum"] = (info.line + 1).toString()
                    consumer.accept(childMap)
                }
            }
        }
    }
}