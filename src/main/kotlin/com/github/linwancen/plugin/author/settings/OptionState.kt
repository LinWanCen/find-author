package com.github.linwancen.plugin.author.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.usages.rules.UsageGroupingRule
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.linwancen.plugin.author.settings.IgnoreState",
    storages = [Storage("find-author/OptionState.xml")]
)
class OptionState : PersistentStateComponent<OptionState?> {
    var gitFormat = GIT_FORMAT
    var findFormat = FIND_FORMAT
    var fileEnds = "|.java"
        set(value) {
            field = value
            fileEndList = value.trim().split("|").map(String::trim)
            if (fileEndList.isEmpty()) {
                fileEndList = listOf("")
            }
        }
    var fileEndList = listOf<String>("", ".java")

    override fun getState(): OptionState {
        return this
    }

    override fun loadState(state: OptionState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        const val GIT_FORMAT = "\${fileNum}.(\${fileName}:\${lineNum})\t%an\t%ci\t%s"
        const val FIND_FORMAT = ".(\${fileName}:\${lineNum})\t\${gitName}\t\${gitTime}\t\${Module}"

        /**
         * @see UsageGroupingRule impl
         */
        @JvmStatic
        val params = """
            ${'$'}{filePath}    ${'$'}{fileName}     ${'$'}{lineNum}
            ${'$'}{gitName}     ${'$'}{gitTime}      ${'$'}{gitHash}
            ${'$'}{UsageScope}  ${'$'}{UsageType}    ${'$'}{Module}  ${'$'}{Directory} ${'$'}{File}
            ${'$'}{Package}     ${'$'}{Class}        ${'$'}{Method}
            ${'$'}{NonJavaFile} ${'$'}{NonCodeUsage}
            ${'$'}{SingleParentUsage}
        """.trimIndent()

        @JvmStatic
        fun of(project: Project? = null): OptionState {
            val manager = project ?: ApplicationManager.getApplication()
            return manager.getService(OptionState::class.java) ?: return OptionState()
        }
    }
}