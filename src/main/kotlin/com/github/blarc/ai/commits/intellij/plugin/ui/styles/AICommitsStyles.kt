package com.github.blarc.ai.commits.intellij.plugin.ui.styles

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import java.awt.Font

/**
 * Defines and manages text styles for AI Commits UI elements.
 * These styles can be customized by users in Settings > Editor > Color Scheme > AI Commits Styles.
 */
object AICommitsStyles {
    val ACTIVE_MODEL_NAME = StyleDefinition(
        id = "active-model-name",
        displayNameKey = "styles.name.llm-active",
        defaultAttributes = TextAttributes().apply {
            foregroundColor = JBColor(0x2B7ABF, 0x6BA4D9)
            fontType = Font.BOLD
        }
    )

    // Currently not used anywhere
    private val ERROR_MESSAGE = StyleDefinition(
        id = "error-message",
        displayNameKey = "styles.name.error-message",
        defaultAttributes = TextAttributes().apply {
            foregroundColor = JBColor(0xC75450, 0xFF6B68)
            fontType = Font.BOLD
        }
    )

    // Add more Style definitions here as needed

    /**
     * Returns all Style definitions
     * Used by our settings page to display all items with defined styles.
     */
    fun getAllDefinitions(): List<StyleDefinition> = listOf(
        ACTIVE_MODEL_NAME,
        ERROR_MESSAGE
        // Add new definitions to this list when you create new one above
    )
}

/**
 * Represents a customizable color/style definition in the IDE.
 *
 * @param id Unique identifier for Style definition (used in settings storage)
 * @param displayNameKey Resource bundle key for the display name in settings
 * @param defaultAttributes Default text attributes (colors, font style)
 */
data class StyleDefinition(val id: String, val displayNameKey: String, val defaultAttributes: TextAttributes) {
    /**
     * The TextAttributesKey used by IntelliJ's color settings system.
     */
    val textAttributesKey: TextAttributesKey by lazy {
        TextAttributesKey.createTextAttributesKey("AICommits.$id").also { key ->
            EditorColorsManager.getInstance().globalScheme.setAttributes(key, defaultAttributes)
        }
    }

    /**
     * Gets the current attributes for this Style definition,
     * respecting any user customizations from the IDE settings.
     * Falls back to default attributes if not customized.
     */
    private fun getCurrentAttributes(): TextAttributes = EditorColorsManager.getInstance().globalScheme
        .getAttributes(textAttributesKey) ?: defaultAttributes

    /**
     * Generates HTML/CSS style string for Style definition.
     * Creates a clean style attribute by collecting only the applicable styles.
     */
    private fun getHtmlStyle(): String {
        val attributes = getCurrentAttributes()
        return buildList {
            attributes.foregroundColor?.let {
                add("color:${ColorUtil.toHtmlColor(it)}")
            }
            attributes.backgroundColor?.let {
                add("background-color:${ColorUtil.toHtmlColor(it)}")
            }
            if (attributes.fontType and Font.BOLD != 0) add("font-weight:bold")
            if (attributes.fontType and Font.ITALIC != 0) add("font-style:italic")
        }.joinToString(";")
    }

    fun wrapWithHtml(text: String): String {
        val style = getHtmlStyle()
        return "<span style=\"$style\">$text</span>"
    }
}
