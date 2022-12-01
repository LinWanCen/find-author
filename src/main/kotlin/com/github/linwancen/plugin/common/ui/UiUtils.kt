package com.github.linwancen.plugin.common.ui

import java.util.function.Consumer
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

object UiUtils {
    @JvmStatic
    fun onChange(jTextComponent: JTextComponent, initValue: String, onChange: Consumer<DocumentEvent>) {
        jTextComponent.text = initValue
        jTextComponent.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                onChange.accept(e)
            }

            override fun removeUpdate(e: DocumentEvent) {
                onChange.accept(e)
            }

            override fun changedUpdate(e: DocumentEvent) {
                onChange.accept(e)
            }
        })
    }
}