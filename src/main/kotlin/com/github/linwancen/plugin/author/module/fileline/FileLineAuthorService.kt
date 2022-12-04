package com.github.linwancen.plugin.author.module.fileline

import com.github.linwancen.plugin.author.git.GitBlame
import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.git.GitLog
import com.github.linwancen.plugin.common.file.FileLine
import com.github.linwancen.plugin.common.text.Formats

object FileLineAuthorService {
    @JvmStatic
    fun outLine(
        gitInfo: GitInfo,
        lineStr: String,
        format: String
    ): String {
        val project = gitInfo.project
        val scope = gitInfo.state.projectScope
        val fileLine = FileLine(lineStr, gitInfo.state.option.fileEndList, project, scope)
        val result = if (fileLine.lineNum != "0") {
            GitBlame.info(gitInfo, format, fileLine.filePath, fileLine.lineNum)
        } else {
            GitLog.info(gitInfo, format, fileLine.filePath)
        }
        val paramMap = mapOf(
            "fileNum" to fileLine.fileNum,
            "filePath" to fileLine.filePath,
            "fileName" to fileLine.fileName,
            "lineNum" to fileLine.lineNum
        )
        return Formats.text(result, paramMap).trim()
    }
}