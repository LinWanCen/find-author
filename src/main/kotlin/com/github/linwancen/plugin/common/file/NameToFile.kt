package com.github.linwancen.plugin.common.file

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

object NameToFile {

    @JvmStatic
    fun file(
        fileName: String,
        searchScope: GlobalSearchScope,
        project: Project
    ): PsiFile? {
        var result: PsiFile? = null
        DumbService.getInstance(project).runReadActionInSmartMode {
            if (!fileName.startsWith("package-info")) {
                for (psiFile in FilenameIndex.getFilesByName(project, fileName, searchScope)) {
                    result = psiFile
                    return@runReadActionInSmartMode
                }
                for (psiFile in FilenameIndex.getFilesByName(project, "$fileName.java", searchScope)) {
                    result = psiFile
                    return@runReadActionInSmartMode
                }
            }
        }
        return result
    }
}