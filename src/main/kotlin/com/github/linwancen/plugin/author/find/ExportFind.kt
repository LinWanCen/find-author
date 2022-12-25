package com.github.linwancen.plugin.author.find

import com.intellij.navigation.NavigationItem
import com.intellij.usages.UsageInfo2UsageAdapter
import com.intellij.usages.UsageView
import com.intellij.usages.impl.GroupNode
import com.intellij.usages.impl.UsageNode
import com.intellij.usages.impl.UsageViewImpl
import java.util.function.Consumer
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

object ExportFind {

    private const val GROUPING_RULE = "GroupingRule"
    private const val DELETE_GROUPING_RULE_LEN = "GroupingRule".length - 1

    @JvmStatic
    fun key(usageView: UsageView): String {
        if (usageView is UsageViewImpl) {
            val firstChild = usageView.root.firstChild
            if (firstChild is DefaultMutableTreeNode) {
                val treeNode = firstChild.firstChild
                if (treeNode is DefaultMutableTreeNode) {
                    val userObject = treeNode.userObject
                    if (userObject is NavigationItem) {
                        userObject.name?.let { return it }
                    }
                }
            }
        }
        return "find-author"
    }

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
        consumer: Consumer<MutableMap<String, String>>
    ) {
        val children = treeNode.children() ?: return
        for (child in children) {
            if (child is GroupNode) {
                val childMap = mutableMapOf<String, String>()
                childMap.putAll(map)
                val groupClass = child.group.javaClass
                // PsiNamedElementUsageGroupBase
                var simpleName = groupClass.enclosingClass?.simpleName ?: groupClass.simpleName
                if (groupClass.enclosingClass == null) {
                    System.err.println(groupClass.name)
                }
                if (simpleName.endsWith(GROUPING_RULE)) {
                    simpleName = simpleName.substring(0, simpleName.lastIndex - DELETE_GROUPING_RULE_LEN)
                }
                // 212.4746.92 getPresentableGroupText()
                val text = child.group.getText(usageView)
                childMap[simpleName] = text
                childMap["${(childMap.size + 1) / 2}"] = text
                recursiveScan(childMap, child, usageView, consumer)
            } else if (child is UsageNode) {
                val info = child.userObject
                if (info is UsageInfo2UsageAdapter) {
                    val childMap = mutableMapOf<String, String>()
                    childMap.putAll(map)
                    childMap["Method"]?.let {
                        childMap["methodName"] = it.substring(0, it.indexOf("("))
                        childMap["."] = "."
                    }
                    childMap["filePath"] = info.file.path
                    childMap["fileNameS"] = info.file.nameWithoutExtension
                    childMap["fileName"] = info.file.name
                    val lineNum = (info.line + 1).toString()
                    childMap["lineNum"] = lineNum
                    childMap["text"] = info.text.joinToString("").substring(lineNum.length)
                    consumer.accept(childMap)
                }
            }
        }
    }
}