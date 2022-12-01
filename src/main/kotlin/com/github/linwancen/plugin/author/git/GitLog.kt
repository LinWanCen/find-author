package com.github.linwancen.plugin.author.git

import com.github.linwancen.plugin.author.ui.FindAuthorBundle
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler

object GitLog {

    const val DEFAULT_FORMAT = "\$fileNum.(\$fileName:\$lineNum)\t%an\t%ci\t%h\t%s"
    val FORMAT_KEY = Regex("%\\w+")

    /**
     * ```
     * git log -1 --pretty=format:"%an %ci %h %s" --invert-grep --extended-regexp filePath
     * git log -1 --pretty=format:"%an %ci %h %s" --invert-grep --extended-regexp --author=name1|name2 filePath
     * git log -1 --pretty=format:"%an %ci %h %s" --invert-grep --extended-regexp --grep=msg1|msg2 filePath
     * git log -1 --pretty=format:"%an %ci %h %s" --invert-grep --extended-regexp --walk-reflogs --grep-reflog=rev1|rev2 filePath
     * ```
     */
    @JvmStatic
    fun info(gitInfo: GitInfo, format: String, filePath: String): String {
        val repo = GitRepos.repo(filePath, gitInfo.project) ?: return GitRepos.notInGit(format)
        val handler = GitLineHandler(gitInfo.project, repo.root, GitCommand.LOG)
        handler.addParameters("-1", "--pretty=format:$format")

        val ignore = gitInfo.state.ignore
        handler.addParameters("--invert-grep", "--extended-regexp")
        if (ignore.userPattern.isNotBlank()) {
            handler.addParameters("--author=${ignore.userPattern}")
        } else if (ignore.msgPattern.isNotBlank()) {
            handler.addParameters("--grep=${ignore.msgPattern}")
        } else if (ignore.revPattern.isNotBlank()) {
            handler.addParameters("--walk-reflogs", "--grep-reflog=${ignore.revPattern}")
        }

        handler.addParameters(filePath)
        return gitInfo.outOrErr(handler, format)
    }

    @JvmStatic
    fun revs(project: Project): String {
        val gitInfo = GitInfo(project)
        val repos = GitRepos.repos(project)
        var result = ""
        for (repo in repos) {
            val handler = GitLineHandler(project, repo.root, GitCommand.LOG)
            handler.addParameters("--pretty=format:%h", "--grep=${gitInfo.state.ignore.msgPattern}")
            val runCommand = gitInfo.git.runCommand(handler)
            result += runCommand.outputAsJoinedString + '\n'
        }
        return result
    }

    @JvmStatic
    fun demo(project: Project, format: String): String {
        val title = "${FindAuthorBundle.message("git-log-format-demo-task")} $format"
        val value = object : Task.WithResult<String, Exception>(project, title, false) {
            override fun compute(indicator: ProgressIndicator): String {
                val gitInfo = GitInfo(project)
                val repo = GitRepos.repo(project) ?: return FindAuthorBundle.message("not-in-git")
                val handler = GitLineHandler(project, repo.root, GitCommand.LOG)
                handler.addParameters("-1", "--pretty=format:$format")
                return gitInfo.outOrErr(handler, format)
            }
        }
        value.queue()
        return value.result
    }
}