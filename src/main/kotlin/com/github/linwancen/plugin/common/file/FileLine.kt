package com.github.linwancen.plugin.common.file

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import java.util.regex.Pattern

data class FileLine(private val line: String, private var project: Project, private var scope: GlobalSearchScope) {
    companion object {
        /**
         * file:lineNum or (file:lineNum)
         */
        @JvmStatic
        private val LINE_NUM_PATTERN = Pattern.compile("\\(?([^(]*)[:\t](\\d++)\\)? *$")
    }

    val fileNum: String = ""
    val filePath: String
    val fileName: String
    val lineNum: String

    init {
        var filePath = line
        var lineNum = "0"
        val matcher = LINE_NUM_PATTERN.matcher(line)
        if (matcher.find()) {
            filePath = matcher.group(1)
            lineNum = matcher.group(2)
        }
        if (filePath.contains('\\')) {
            filePath = filePath.replace('\\', '/')
        }
        fileName = if (filePath.contains('/')) {
            filePath.substring(filePath.lastIndexOf('/') + 1)
        } else {
            val psiFile = NameToFile.file(filePath, scope, project)
            if (psiFile == null) {
                filePath.substring(filePath.lastIndexOf('/') + 1)
            } else {
                val file = psiFile.virtualFile
                filePath = file.path
                file.name
            }
        }
        this.filePath = filePath
        this.lineNum = lineNum
    }
}
