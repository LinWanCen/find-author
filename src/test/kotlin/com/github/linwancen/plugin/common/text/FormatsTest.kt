package com.github.linwancen.plugin.common.text

import org.junit.Test

import org.junit.Assert.*

/**
 * @see Formats
 */
class FormatsTest {

    @Test
    fun text() {
        val map = mapOf(
            "k1" to "v1",
            "k2" to "v2"
        )
        assertEquals("v1", Formats.text("\${k1}", map))
        assertEquals("<v1", Formats.text("<\${k1}", map))
        assertEquals("v1>", Formats.text("\${k1}>", map))
        assertEquals("<v1>", Formats.text("<\${k1}>", map))
        assertEquals("v1v2", Formats.text("\${k1}\${k2}", map))
        assertEquals("<v1v2", Formats.text("<\${k1}\${k2}", map))
        assertEquals("v1v2>", Formats.text("\${k1}\${k2}>", map))
        assertEquals("<v1v2>", Formats.text("<\${k1}\${k2}>", map))
        assertEquals("v1|v2", Formats.text("\${k1}|\${k2}", map))
        assertEquals("<v1|v2", Formats.text("<\${k1}|\${k2}", map))
        assertEquals("v1|v2>", Formats.text("\${k1}|\${k2}>", map))
        assertEquals("<v1|v2>", Formats.text("<\${k1}|\${k2}>", map))
        assertEquals("<v1|>", Formats.text("<\${k1}|\${k3}>", map))
    }
}