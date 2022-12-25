package com.github.linwancen.plugin.author.ui

import com.github.linwancen.plugin.common.file.Dirs
import com.intellij.CommonBundle
import com.intellij.ide.ExporterToTextFile
import com.intellij.ide.util.ExportToFileUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.text.SimpleDateFormat
import java.util.*

object ExportOutputToTsvController {
    private val PROJECT = Project::class.java
    private val UTIL = ExportToFileUtil::class.java

    private val STRING = String::class.java

    @JvmStatic
    fun export(project: Project, exportName: String?, text: String?) {
        if (exportName == null || text == null) {
            return
        }
        val sdf = SimpleDateFormat("_yyyy-MM-dd_HH.mm.ss")
        val time = sdf.format(Date())
        val dir = project.basePath + "/find-author"
        val filePrefix = Dirs.deleteSymbol(exportName)
        val file = "$dir/$filePrefix$time.tsv"
        val exporter = ExportOutputToTsv(text, file)
        // 212.4746.92 ExportToFileUtil.chooseFileAndExport but can not use in old version
        try {
            // val dlg = ExportToFileUtil.ExportDialogBase(project, exporter)
            val clazz = Class.forName("com.intellij.ide.util.ExportToFileUtil\$ExportDialogBase")
            val construct = clazz.getDeclaredConstructor(PROJECT, ExporterToTextFile::class.java)
            construct.isAccessible = true
            val dlg = construct.newInstance(project, exporter)
            if (dlg !is DialogWrapper) {
                return
            }
            if (!dlg.showAndGet()) {
                return
            }
            val getFileName = clazz.getDeclaredMethod("getFileName")
            getFileName.isAccessible = true
            val fileName = getFileName.invoke(dlg).toString()

            Dirs.mkdirsAndIgnore(project, fileName)

            // ExportToFileUtil.exportTextToFile(project, dlg.fileName, dlg.text)
            val getText = clazz.getDeclaredMethod("getText")
            getText.isAccessible = true
            val dlgText = getText.invoke(dlg).toString()
            val exportTextToFile = UTIL.getDeclaredMethod("exportTextToFile", PROJECT, STRING, STRING)
            exportTextToFile.isAccessible = true
            exportTextToFile.invoke(dlg, project, fileName, dlgText)

            exporter.exportedTo(fileName)
        } catch (e: Exception) {
            Messages.showMessageDialog(
                project,
                "${e.javaClass.simpleName}: ${e.localizedMessage}",
                CommonBundle.getErrorTitle(),
                Messages.getErrorIcon()
            )
        }
    }
}