package com.github.linwancen.plugin.author.git

import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.config.GitVersion
import java.util.regex.Pattern

object GitBlame {

    val SUPPORT_IGNORE_VERSION = GitVersion(2, 23, 0, 0)

    /**
     * hash filePath (author yyyy-MM-dd HH:mm:ss
     */
    private val BLAME_PATTERN = Pattern.compile(
        "(\\w++) .*\\(" +
                "(.*?) ++" +
                "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})"
    )

    @JvmStatic
    val BLAME_MAP = mapOf(
        "gitName" to "%an",
        "gitTime" to "%ci",
        "gitHash" to "%h"
    )

    /**
     * git blame --ignore-rev=xxx -w -Ln,n filePath
     */
    @JvmStatic
    fun info(
        gitInfo: GitInfo,
        format: String,
        filePath: String?,
        lineNum: String?,
        ignoreRevs: MutableSet<String> = mutableSetOf()
    ): String {
        if (filePath == null || lineNum == null) {
            return format.replace(GitLog.FORMAT_KEY, "")
        }
        val repo = GitRepos.repo(filePath, gitInfo.project) ?: return GitRepos.notInGit(format)
        val handler = GitLineHandler(gitInfo.project, repo.root, GitCommand.BLAME)
        if (gitInfo.canIgnore && ignoreRevs.isNotEmpty()) {
            for (ignoreRev in ignoreRevs) {
                handler.addParameters("--ignore-rev=$ignoreRev")
            }
        }
        handler.addParameters("-w", "-L$lineNum,$lineNum", filePath)
        val runCommand = gitInfo.git.runCommand(handler)
        val blameText: String = runCommand.outputAsJoinedString
        if (blameText.isBlank()) {
            return format.replace(GitLog.FORMAT_KEY, "") + runCommand.errorOutputAsJoinedString
        }
        val matcher = BLAME_PATTERN.matcher(blameText)
        if (!matcher.find()) {
            return format.replace(GitLog.FORMAT_KEY, "") + blameText
        }
        val hash = matcher.group(1) ?: ""
        val name = matcher.group(2) ?: ""
        val time = matcher.group(3) ?: ""
        if (gitInfo.canIgnore && gitInfo.ignore(name, hash) && !ignoreRevs.contains(hash)) {
            ignoreRevs.add(hash)
            return info(gitInfo, format, filePath, lineNum, ignoreRevs)
        }
        var result = format.replace("%h", hash)
        result = result.replace("%an", name)
        result = result.replace("%ci", time)
        result = result.replace("\${gitModule}", repo.root.path.substring(repo.root.path.indexOf('/') + 1))
        result = result.replace(GitLog.FORMAT_KEY, "")
        return result
    }
}