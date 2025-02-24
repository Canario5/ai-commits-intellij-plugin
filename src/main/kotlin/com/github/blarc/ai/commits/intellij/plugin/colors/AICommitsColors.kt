package com.github.blarc.ai.commits.intellij.plugin.colors

import com.github.blarc.ai.commits.intellij.plugin.AICommitsBundle.message
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.ui.JBColor
import com.intellij.ui.ColorUtil

object AICommitsColors {
    val LLM_ACTIVE = ColorDefinition(
        id = "llm-active",
        messageKey = "colors.name.llm-active",
        lightColor = 0x2B7ABF,
        darkColor = 0x6BA4D9
    )

    data class ColorDefinition(
        val id: String,
        val messageKey: String, 
        val lightColor: Int,
        val darkColor: Int
    ) {
        val default = JBColor(lightColor, darkColor)
        val textAttributesKey = TextAttributesKey.createTextAttributesKey("AICommits.$id")
        
        fun getCurrentColor() = EditorColorsManager.getInstance()
            .globalScheme
            .getAttributes(textAttributesKey)
            ?.foregroundColor
            ?: default

        fun toHtml() = ColorUtil.toHtmlColor(getCurrentColor())
    }
}

class AICommitsColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors() = arrayOf(
        AttributesDescriptor(
            message("colors.name.llm-active"),
            AICommitsColors.LLM_ACTIVE.textAttributesKey
        )
    )

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getHighlighter() = PlainSyntaxHighlighter()
    override fun getDemoText() = """
                                Action Tooltip in commit window
                                Generate commit message with ${highlightLlmName("o1 (OpenAI)")}
                                Generate commit message with ${highlightLlmName("Gemini Flash 2.0 (Google)")}
                                Generate commit message with ${highlightLlmName("Gemini Flash 2.0 (OpenRouter)")}

                                Note: The color highlights the exact ${highlightLlmName("LLM name")}
                                you saved in your AI Commits plugin settings,
                                helping you quickly identify which AI service 
                                is generating your commit messages.
                                """.trimIndent()

    private fun highlightLlmName(text: String) = "<llm-active>$text</llm-active>"

    override fun getDisplayName() = message("group.colors")
    override fun getIcon() = null
    override fun getAdditionalHighlightingTagToDescriptorMap() = mapOf(
        "llm-active" to AICommitsColors.LLM_ACTIVE.textAttributesKey
    )
}
