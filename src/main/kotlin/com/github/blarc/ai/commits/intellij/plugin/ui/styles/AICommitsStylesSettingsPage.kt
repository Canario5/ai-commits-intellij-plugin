package com.github.blarc.ai.commits.intellij.plugin.ui.styles

import com.github.blarc.ai.commits.intellij.plugin.AICommitsBundle.message
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage

/**
 * Configures color scheme settings for AI Commits UI elements.
 * Appears in Settings > Editor > Color Scheme > AI Commits Styles.
 * All methods bellow are required by the interface (but some can be null)
 */
class AICommitsStylesSettingsPage : ColorSettingsPage {
    // Purpose: method ColorDescriptors() define standalone UI colors (like borders, backgrounds of UI components)
    // Details: We customize only text attributes, not standalone UI colors, so we return just an empty array.
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    // Purpose: AttributeDescriptors() defines text styling (foreground color, background, bold, italic... etc.)
    // Details: Generates style descriptors for IDE color settings from AICommitsStyles.
    // !NOTE: It shows defaults Jetbrains fields, so some unwanted attributes are displayed.
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return AICommitsStyles.getAllDefinitions().map { styleDefinition ->
            AttributesDescriptor(
                message(styleDefinition.displayNameKey),
                styleDefinition.textAttributesKey
            )
        }.toTypedArray()
    }

    // Purpose: Sets the name shown in the settings in Editor -> Color Schemes -> "name of this settings page"
    override fun getDisplayName() = message("group.styles")

    // Purpose: Provides an icon for the settings page
    // Details: Returns null, meaning no icon will be shown
    override fun getIcon() = null

    // Uses a plain text highlighter, because content of the demo is a text and not a code
    override fun getHighlighter() = PlainSyntaxHighlighter()

    //? TODO: Potential improvement - Fetch users' real names of LLMs
    //! NOTE: Indentation at the start of the string in the demo is visible in the settings
    //? NOTE: Potential improvement - Display users' stored LLM names.
    override fun getDemoText() =
        """
${message("styles.demo.styling-limitations")}

Action Tooltip in commit window:
Generate commit message with <active-model-name>o1 (OpenAI)</active-model-name>
Generate commit message with <active-model-name>Gemini Flash 2.0 (Google)</active-model-name>
Generate commit message with <active-model-name>Gemini Flash 2.0 (OpenRouter)</active-model-name>

Error message example:
<error-message>${message("action.error")}</error-message>

Note: ${message("styles.name.llm-active.desc", "<active-model-name>LLM name</active-model-name>")}
        """.trimMargin()

    // Purpose: Maps XML-like tags in the demo text to text attribute keys
    // Details: Tells IntelliJ: "When is a text between <active-model-name> tags, apply the ACTIVE_MODEL_NAME text attributes"
    override fun getAdditionalHighlightingTagToDescriptorMap() = buildMap {
        AICommitsStyles.getAllDefinitions().forEach { styleDefinition ->
            put(styleDefinition.id, styleDefinition.textAttributesKey)
        }
    }.toMutableMap()
}
