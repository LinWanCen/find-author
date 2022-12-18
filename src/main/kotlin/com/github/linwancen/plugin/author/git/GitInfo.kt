package com.github.linwancen.plugin.author.git

import com.github.linwancen.plugin.author.settings.State
import com.intellij.openapi.project.Project
import git4idea.commands.Git
import git4idea.commands.GitLineHandler
import git4idea.config.GitExecutableManager
import git4idea.config.GitVersion
import git4idea.repo.GitRepository

/**
 * git info cache and some fun
 */
data class GitInfo(val project: Project) {
    val state = State(project)
    val git = Git.getInstance()
    private val gitExecutableManager = GitExecutableManager.getInstance()
    private val version: GitVersion = gitExecutableManager.getVersion(project)
    val canIgnore: Boolean = version.isLaterOrEqual(GitBlame.SUPPORT_IGNORE_VERSION)

    fun outOrErr(repo: GitRepository, handler: GitLineHandler, format: String): String {
        val runCommand = git.runCommand(handler)
        var result = runCommand.outputAsJoinedString
        if (result.isBlank()) {
            return format.replace(GitLog.FORMAT_KEY, "") + runCommand.errorOutputAsJoinedString
        }
        result = result.replace("\${gitModule}", repo.root.path.substring(repo.root.path.indexOf('/') + 1))
        return GitLog.TIME_ZONE.replace(result, "")
    }

    fun ignore(name: String, rev: String): Boolean {
        return state.ignore.user.contains(name) || state.ignore.rev.contains(rev)
    }
}
