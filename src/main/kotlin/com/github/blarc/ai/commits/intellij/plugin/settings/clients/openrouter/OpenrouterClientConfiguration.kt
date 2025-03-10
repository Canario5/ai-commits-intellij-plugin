package com.github.blarc.ai.commits.intellij.plugin.settings.clients.openrouter

import com.github.blarc.ai.commits.intellij.plugin.Icons
import com.github.blarc.ai.commits.intellij.plugin.settings.clients.LLMClientConfiguration
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Transient
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler
import kotlinx.coroutines.Job
import javax.swing.Icon

class OpenrouterClientConfiguration :
    LLMClientConfiguration(
        "Openrouter",
        "openai/chatgpt-4o-latest",
        "0.7"
    ) {

    @Attribute
    var host: String = "https://openrouter.ai/api/v1"

    @Attribute
    var timeout: Int = 30

    @Attribute
    var proxyUrl: String? = null

    @Attribute
    var tokenIsStored: Boolean = false

    @Transient
    var token: String? = null

    @Attribute
    var topP: Double? = null

    companion object {
        const val CLIENT_NAME = "Openrouter"
    }

    override fun getClientName(): String = CLIENT_NAME

    override fun getClientIcon(): Icon = Icons.OPENROUTER.getThemeBasedIcon()

    override fun getSharedState(): OpenrouterClientSharedState = OpenrouterClientSharedState.getInstance()

    override fun generateCommitMessage(
        commitWorkflowHandler: AbstractCommitWorkflowHandler<*, *>,
        commitMessage: CommitMessage,
        project: Project
    ) = OpenrouterClientService.getInstance().generateCommitMessage(
        this,
        commitWorkflowHandler,
        commitMessage,
        project
    )

    override fun getGenerateCommitMessageJob(): Job? = OpenrouterClientService.getInstance().generateCommitMessageJob

    override fun clone(): LLMClientConfiguration {
        val copy = OpenrouterClientConfiguration()
        copy.id = id
        copy.name = name
        copy.host = host
        copy.proxyUrl = proxyUrl
        copy.timeout = timeout
        copy.modelId = modelId
        copy.temperature = temperature
        copy.tokenIsStored = tokenIsStored
        copy.topP = topP
        return copy
    }

    override fun panel() = OpenrouterClientPanel(this)
}
