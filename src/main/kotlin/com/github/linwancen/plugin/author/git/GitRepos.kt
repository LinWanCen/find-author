package com.github.linwancen.plugin.author.git

import com.github.linwancen.plugin.author.ui.FindAuthorBundle
import com.google.common.cache.CacheBuilder
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object GitRepos {
    private val gitRootPathCache: ConcurrentMap<String, GitRepository> = ConcurrentHashMap()

    private val filePathsCache = CacheBuilder.newBuilder()
        .maximumSize(20) // 2^14
        // .weakKeys()
        .expireAfterAccess(Duration.ofMinutes(15))
        .recordStats()
        .build<String, GitRepository>()

    @JvmStatic
    fun notInGit(format: String) = format.replace(GitLog.FORMAT_KEY, "") + FindAuthorBundle.message("not-in-git")

    @JvmStatic
    fun repo(filePath: String, project: Project? = null): GitRepository? {
        // find in filePathsCache
        filePathsCache.getIfPresent(filePath)?.let {
            // println("filePathsCache$path")
            return it
        }
        // find in gitRootPathCache
        for (entry in gitRootPathCache) {
            if (filePath.startsWith(entry.key)) {
                // println("gitRootPathCache$path")
                filePathsCache.put(filePath, entry.value)
                return entry.value
            }
        }
        // reload gitRootPathCache and find
        project ?: return null
        val repos = GitUtil.getRepositories(project)
        var result: GitRepository? = null
        for (repo in repos) {
            if (repo.submodules.isEmpty()) {
                val gitRootPath = repo.root.path
                gitRootPathCache[gitRootPath] = repo
                if (filePath.startsWith(gitRootPath)) {
                    filePathsCache.put(filePath, repo)
                    result = repo
                }
            }
        }
        return result
    }

    @JvmStatic
    fun run(project: Project, gitCommand: GitCommand, param: String) {
        object : Task.Backgroundable(project, "$gitCommand $param") {
            override fun run(indicator: ProgressIndicator) {
                val repo = repo(project) ?: return
                val handler = GitLineHandler(project, repo.root, gitCommand)
                handler.addParameters(param)
                Git.getInstance().runCommandWithoutCollectingOutput(handler)
            }
        }.queue()
    }

    @JvmStatic
    fun repo(project: Project): GitRepository? {
        for (repo in repos(project)) {
            return repo
        }
        return null
    }

    @JvmStatic
    fun repos(project: Project): MutableCollection<GitRepository> {
        if (gitRootPathCache.isNotEmpty()) {
            return gitRootPathCache.values
        }
        val repos = GitUtil.getRepositories(project)
        for (repo in repos) {
            if (repo.submodules.isEmpty()) {
                val gitRootPath = repo.root.path
                gitRootPathCache[gitRootPath] = repo
            }
        }
        return repos
    }
}