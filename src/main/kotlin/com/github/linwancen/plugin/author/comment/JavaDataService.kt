package com.github.linwancen.plugin.author.comment

import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.common.text.DocText
import com.intellij.openapi.project.DumbService
import com.intellij.psi.*
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.javadoc.PsiDocToken
import com.intellij.psi.javadoc.PsiInlineDocTag
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.util.PsiTreeUtil

object JavaDataService : DataService() {

    init {
        SERVICES[this::class.java.simpleName] = this
    }

    override fun needPut(format: String): Boolean {
        return format.contains("\${@")
    }

    override fun putData(gitInfo: GitInfo, format: String, map: MutableMap<String, String>): String? {
        val fileName = map["fileName"] ?: return null
        val fileNameS = map["fileNameS"] ?: return null
        if (!fileName.endsWith(".java")) {
            return null
        }
        val namesCache = PsiShortNamesCache.getInstance(gitInfo.project) ?: return null
        DumbService.getInstance(gitInfo.project).runReadActionInSmartMode {
            val psiClasses = namesCache.getClassesByName(fileNameS, gitInfo.state.projectScope)
            val methodName = map["methodName"]
            for (psiClass in psiClasses) {
                addDocParam(gitInfo, map, psiClass.docComment)
                // override when exist
                if (methodName == null) {
                    lineMethod(gitInfo, psiClass, map)
                } else {
                    for (psiMethod in psiClass.findMethodsByName(methodName, false)) {
                        addDocParam(gitInfo, map, psiMethod.docComment)
                    }
                }
            }
        }
        return null
    }

    private fun lineMethod(gitInfo: GitInfo, psiClass: PsiClass, map: MutableMap<String, String>) {
        val lineNum = map["lineNum"] ?: return
        if (lineNum == "0") return
        val lineNumber = try {
            lineNum.toInt()
        } catch (e: Exception) {
            return
        }
        val file = psiClass.containingFile.virtualFile
        val viewProvider = PsiManager.getInstance(gitInfo.project).findViewProvider(file) ?: return
        val document = viewProvider.document ?: return
        val offset = try {
            document.getLineEndOffset(lineNumber - 1)
        } catch (e: Exception) {
            return
        }
        val element = viewProvider.findElementAt(offset - 1)
        val psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java) ?: return
        map["methodName"] = psiMethod.name
        map["."] = "."
        addDocParam(gitInfo, map, psiMethod.docComment)
    }

    private fun addDocParam(gitInfo: GitInfo, map: MutableMap<String, String>, docComment: PsiDocComment?) {
        if (docComment == null) {
            return
        }
        addDescription(docComment, map)
        addTag(gitInfo, docComment, map)
    }

    private fun addDescription(docComment: PsiDocComment, map: MutableMap<String, String>) {
        val elements: Array<PsiElement> = docComment.descriptionElements
        val all = StringBuilder()
        val currLine = StringBuilder()
        var lineCount = 1
        for (element in elements) {
            if (appendElementText(element, all, currLine)) {
                map["@$lineCount"] = currLine.toString()
                lineCount++
                currLine.clear()
            }
        }
        map["@0"] = all.toString()
    }

    private fun addTag(gitInfo: GitInfo, docComment: PsiDocComment, map: MutableMap<String, String>) {
        for (tag in docComment.tags) {
            // @see @param should use getDataElements()
            val name = tag.name
            val valueElement = tag.valueElement ?: continue
            val value = DocText.addHtmlText(valueElement.text) ?: continue
            if (name == "author" && gitInfo.state.ignore.user.contains(value)) {
                continue
            }
            map["@$name"] = value
        }
    }

    private fun appendElementText(element: PsiElement, all: StringBuilder, currLine: StringBuilder): Boolean {
        if (element is PsiDocToken) {
            DocText.addHtmlText(element.text, all, currLine)
        }
        if (element is PsiInlineDocTag) {
            val children = element.children
            if (children.size >= 3) {
                DocText.addHtmlText(children[children.size - 2].text, all, currLine)
            }
        }
        return element is PsiWhiteSpace && currLine.isNotEmpty()
    }
}