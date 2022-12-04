package com.github.linwancen.plugin.common.ui

import java.util.function.BiConsumer
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

object UiUtils {
    @JvmStatic
    fun onChange(jTextComponent: JTextComponent, initValue: String, onChange: BiConsumer<DocumentEvent, String>) {
        jTextComponent.text = initValue
        jTextComponent.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                onChange.accept(e, jTextComponent.text)
            }

            override fun removeUpdate(e: DocumentEvent) {
                onChange.accept(e, jTextComponent.text)
            }

            override fun changedUpdate(e: DocumentEvent) {
                onChange.accept(e, jTextComponent.text)
            }
        })
    }
}