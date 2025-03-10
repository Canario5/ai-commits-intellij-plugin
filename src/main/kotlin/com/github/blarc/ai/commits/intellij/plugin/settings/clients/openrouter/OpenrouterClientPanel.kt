package com.github.blarc.ai.commits.intellij.plugin.settings.clients.openrouter

import com.github.blarc.ai.commits.intellij.plugin.AICommitsBundle.message
import com.github.blarc.ai.commits.intellij.plugin.emptyText
import com.github.blarc.ai.commits.intellij.plugin.settings.clients.LLMClientPanel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*

class OpenrouterClientPanel(private val clientConfiguration: OpenrouterClientConfiguration) :
    LLMClientPanel(clientConfiguration) {
    private val proxyTextField = JBTextField()
    private val tokenPasswordField = JBPasswordField()
    private val topPTextField = JBTextField()

    override fun create() = panel {
        nameRow()
        hostRow(clientConfiguration::host.toNullableProperty())
        proxyRow()
        timeoutRow(clientConfiguration::timeout)
        tokenRow()
        modelIdRow()
        temperatureRow()
        topPDoubleRow(topPTextField, clientConfiguration::topP.toNullableProperty())
        verifyRow()
    }

    override fun verifyConfiguration() {
        clientConfiguration.host = hostComboBox.item
        clientConfiguration.proxyUrl = proxyTextField.text
        clientConfiguration.timeout = socketTimeoutTextField.text.toInt()
        clientConfiguration.modelId = modelComboBox.item
        clientConfiguration.temperature = temperatureTextField.text
        clientConfiguration.token = String(tokenPasswordField.password)
        clientConfiguration.topP = topPTextField.text.toDoubleOrNull()

        OpenrouterClientService.getInstance().verifyConfiguration(clientConfiguration, verifyLabel)
    }

    private fun Panel.proxyRow() {
        row {
            label(message("settings.llmClient.proxy"))
                .widthGroup("label")
            cell(proxyTextField)
                .bindText(clientConfiguration::proxyUrl.toNonNullableProperty(""))
                .resizableColumn()
                .align(Align.FILL)
                .comment(message("settings.llmClient.proxy.comment"))
        }
    }

    private fun Panel.tokenRow() {
        row {
            label(message("settings.llmClient.token"))
                .widthGroup("label")
            cell(tokenPasswordField)
                .bindText(getter = { "" }, setter = {
                    OpenrouterClientService.getInstance().saveToken(clientConfiguration, it)
                })
                .emptyText(
                    if (clientConfiguration.tokenIsStored) {
                        message("settings.llmClient.token.stored")
                    } else {
                        message("settings.openRouter.token.example")
                    }
                )
                .resizableColumn()
                .align(Align.FILL)
                .comment(message("settings.openRouter.token.comment"), 50)
        }
    }
}
