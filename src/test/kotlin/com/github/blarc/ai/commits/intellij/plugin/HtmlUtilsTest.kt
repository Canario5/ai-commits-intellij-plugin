package com.github.blarc.ai.commits.intellij.plugin

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.awt.Color

class HtmlSanitizerTest : BasePlatformTestCase() {

    fun testHtmlEscaping() {
        val input = """Model "A&B" <script>alert('xss')</script>"""
        val expected = "Model &quot;A&amp;B&quot; &lt;script&gt;alert(&apos;xss&apos;)&lt;/script&gt;"
        assertEquals(expected, AICommitAction.HtmlSanitizer.escapeHtml(input))
    }

    fun testColorConversion() {
        val color = Color(0x2B, 0x7A, 0xBF)
        assertEquals("#2b7abf", AICommitAction.HtmlSanitizer.toHex(color))
    }

    fun testHtmlWrapping() {
        val input = "Simple text"
        assertEquals("<html>Simple text</html>", AICommitAction.HtmlSanitizer.wrapHtml(input))
    }
}
