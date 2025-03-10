package com.github.blarc.ai.commits.intellij.plugin.settings.clients.openrouter

import com.github.blarc.ai.commits.intellij.plugin.settings.clients.LLMClientSharedState
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.XCollection

@Service(Service.Level.APP)
@State(name = "OpenrouterClientSharedState", storages = [Storage("AICommitsOpenrouter.xml")])
class OpenrouterClientSharedState :
    PersistentStateComponent<OpenrouterClientSharedState>,
    LLMClientSharedState {

    companion object {
        @JvmStatic
        fun getInstance(): OpenrouterClientSharedState = service()
    }

    @XCollection(style = XCollection.Style.v2)
    override val hosts = mutableSetOf("https://openrouter.ai/api/v1")

    @XCollection(style = XCollection.Style.v2)
    override val modelIds = mutableSetOf(
        "openai/chatgpt-4o-latest",
        "openai/o1",
        "anthropic/claude-3.7-sonnet",
        "anthropic/claude-3.7-sonnet:thinking",
        "deepseek/deepseek-r1",
        "meta-llama/llama-3.3-70b-instruct:free"
    )

    override fun getState(): OpenrouterClientSharedState = this

    override fun loadState(state: OpenrouterClientSharedState) {
        // Add all model IDs from the state in case they are not stored in xml
        modelIds += state.modelIds
        hosts += state.hosts
    }
}
