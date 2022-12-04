package com.github.linwancen.plugin.author.action

import com.github.linwancen.plugin.author.ui.ExportController
import com.github.linwancen.plugin.author.ui.FindAuthorBundle
import com.intellij.ide.actions.ExportToTextFileAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.usages.UsageView

class ExportToTsvFile : ExportToTextFileAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = FindAuthorBundle.message("export-tsv-with-git-author")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val usageView: UsageView = UsageView.USAGE_VIEW_KEY.getData(dataContext) ?: return
        ExportController.print(project, usageView)
    }
}