package com.github.linwancen.plugin.author.ui

import com.intellij.ide.ExporterToTextFile

class ExportOutputToTsv(val text: String, val path: String) : ExporterToTextFile {

    override fun getReportText(): String {
        return text
    }

    override fun getDefaultFilePath(): String {
        return path
    }

    override fun canExport(): Boolean {
        return true
    }
}