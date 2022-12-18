package com.github.linwancen.plugin.common.file

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import java.util.regex.Pattern

data class FileLine(
    private val line: String,
    val fileEndList: List<String>,
    private var project: Project,
    private var scope: GlobalSearchScope
) {
    companion object {
        /**
         * file:lineNum or (file:lineNum)
         */
        @JvmStatic
        private val LINE_NUM_PATTERN = Pattern.compile("\\(?([^(]*)[:\t](\\d++)\\)? *$")
    }

    val fileNum: String
    val filePath: String
    val fileName: String
    val fileNameS: String
    val lineNum: String

    val map: MutableMap<String, String>

    init {
        var fileNum = ""
        var filePath = line
        var fileName = ""
        var fileNameS = ""
        var lineNum = "0"
        val matcher = LINE_NUM_PATTERN.matcher(line)
        if (matcher.find()) {
            filePath = matcher.group(1)
            lineNum = matcher.group(2)
        }
        if (filePath.contains('\\')) {
            filePath = filePath.replace('\\', '/')
        }
        if (filePath.contains('/')) {
            fileName = filePath.substring(filePath.lastIndexOf('/') + 1)
            fileNameS = fileName.substring(0, fileName.lastIndexOf('.') - 1)
        } else {
            val files = NameToFile.files(filePath, fileEndList, scope, project)
            if (files.isEmpty()) {
                fileName = filePath.substring(filePath.lastIndexOf('/') + 1)
            } else {
                fileNum = "${files.size}"
                for (file in files) {
                    filePath = file.path
                    fileName = file.name
                    fileNameS = file.nameWithoutExtension
                    break
                }
            }
        }
        this.fileNum = fileNum
        this.filePath = filePath
        this.fileName = fileName
        this.fileNameS = fileNameS
        this.lineNum = lineNum
        map = mutableMapOf(
            "fileNum" to fileNum,
            "filePath" to filePath,
            "fileName" to fileName,
            "fileNameS" to fileNameS,
            "lineNum" to lineNum
        )
    }
}
