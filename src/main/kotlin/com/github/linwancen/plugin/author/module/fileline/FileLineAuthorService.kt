package com.github.linwancen.plugin.author.module.fileline

import com.github.linwancen.plugin.author.git.GitBlame
import com.github.linwancen.plugin.author.git.GitInfo
import com.github.linwancen.plugin.author.git.GitLog
import com.github.linwancen.plugin.common.file.FileLine

object FileLineAuthorService {
    @JvmStatic
    fun outLine(
        gitInfo: GitInfo,
        line: String,
        format: String
    ): String {
        val project = gitInfo.project
        val scope = gitInfo.state.allScope
        val fileLine = FileLine(line, project, scope)
        var result = if (fileLine.lineNum != "0") {
            GitBlame.info(gitInfo, format, fileLine.filePath, fileLine.lineNum)
        } else {
            GitLog.info(gitInfo, format, fileLine.filePath)
        }
        result = result.replace("\$fileNum", fileLine.fileNum)
        result = result.replace("\$filePath", fileLine.filePath)
        result = result.replace("\$fileName", fileLine.fileName)
        result = result.replace("\$lineNum", fileLine.lineNum)
        result = result.replace(GitLog.FORMAT_KEY, "")
        return result.trim()
    }
}