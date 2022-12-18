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
        const val GIT_FORMAT = ".(\${fileName}:\${lineNum})" +
                "\t%an" +
                "\t%ci" +
                "\t\${fileNum}" +
                "\t\${fileNameS}\${.}\${methodName}" +
                "\t\${@author}" +
                "\t\${@1} \${@2}" +
                "\t%s"
        const val FIND_FORMAT = ".(\${fileName}:\${lineNum})" +
                "\t\${gitName}" +
                "\t\${gitTime}" +
                "\t\${Module}" +
                "\t\${fileNameS}\${.}\${methodName}" +
                "\t\${@author}" +
                "\t\${@1} \${@2}" +
                "\t\${text}"

        /**
         * @see UsageGroupingRule impl
         */
        @JvmStatic
        val params = """
            ${'$'}{filePath}    ${'$'}{fileName}      ${'$'}{lineNum}  ${'$'}{fileNameS}
            ${'$'}{gitName}     ${'$'}{gitTime}       ${'$'}{gitHash}  ${'$'}{gitModule}
            ${'$'}{@author}     ${'$'}{@1} ${'$'}{@2} ${'$'}{@0}
            ${'$'}{UsageScope}  ${'$'}{UsageType}     ${'$'}{Module}   ${'$'}{Directory}  ${'$'}{File}
            ${'$'}{Package}     ${'$'}{Class}         ${'$'}{Method}   ${'$'}{methodName} ${'$'}{text} ${'$'}{.}
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