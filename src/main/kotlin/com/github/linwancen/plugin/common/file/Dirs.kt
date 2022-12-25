package com.github.linwancen.plugin.common.file

import com.intellij.CommonBundle
import com.intellij.ide.IdeBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.regex.Pattern

object Dirs {
    @JvmStatic
    private val FILE_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]")

    /**
     * delete \/:*?"<>|
     */
    @JvmStatic
    fun deleteSymbol(path: String): String {
        return FILE_PATTERN.matcher(path).replaceAll("")
    }

    /**
     * mkdirs and new .gitignore to ignore all file
     */
    @JvmStatic
    fun mkdirsAndIgnore(project: Project?, fileName: String) {
        val dirFile = File(fileName).parentFile
        if (dirFile.exists()) {
            return
        }
        dirFile.mkdirs()
        val basePath = project?.basePath ?: return
        val newDir = dirFile.path.replace('\\', '/')
        if (!newDir.startsWith(basePath)) {
            return
        }
        val ignoreFile = "$newDir/.gitignore"
        try {
            FileWriter(ignoreFile).use { writer -> writer.write("**/**") }
        } catch (e: IOException) {
            Messages.showMessageDialog(
                project,
                IdeBundle.message("error.writing.to.file", ignoreFile),
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon()
            )
        }
    }
}