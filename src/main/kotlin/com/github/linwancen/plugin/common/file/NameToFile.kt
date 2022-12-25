package com.github.linwancen.plugin.common.file

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

object NameToFile {

    @JvmStatic
    fun files(
        fileName: String,
        fileEndList: List<String>,
        searchScope: GlobalSearchScope,
        project: Project
    ): Collection<VirtualFile> {
        var files: Collection<VirtualFile> = emptyList()
        DumbService.getInstance(project).runReadActionInSmartMode {
            for (fileEnd in fileEndList) {
                // 212.4746.92 delete project
                files = FilenameIndex.getVirtualFilesByName(project, "$fileName$fileEnd", searchScope)
                if (files.isNotEmpty()) {
                    return@runReadActionInSmartMode
                }
            }
        }
        return files
    }
}