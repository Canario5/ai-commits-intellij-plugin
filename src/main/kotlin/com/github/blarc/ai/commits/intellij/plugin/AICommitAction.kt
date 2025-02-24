package com.github.blarc.ai.commits.intellij.plugin

import com.github.blarc.ai.commits.intellij.plugin.AICommitsBundle.message
import com.github.blarc.ai.commits.intellij.plugin.colors.AICommitsColors
import com.github.blarc.ai.commits.intellij.plugin.notifications.Notification
import com.github.blarc.ai.commits.intellij.plugin.notifications.sendNotification
import com.github.blarc.ai.commits.intellij.plugin.settings.ProjectSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.vcs.commit.AbstractCommitWorkflowHandler

/**
 * Action for generating AI-powered commit messages.
 * Provides visual feedback for generation states with theme-aware color highlighting for currently active model.
 */
class AICommitAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        e.project?.service<ProjectSettings>()?.getActiveLLMClientConfiguration()?.let { config ->
            e.presentation.apply {
                if (config.getGenerateCommitMessageJob()?.isActive == true) {
                    icon = Icons.Process.STOP.getThemeBasedIcon()
                    text = message("action.tooltip.is-active")
                } else {
                    icon = config.getClientIcon()
                    text = createTooltipWithHighlightedModel(config.name)
                }
            }
        }
    }

    private fun createTooltipWithHighlightedModel(modelName: String): String {
        val colorHtml = AICommitsColors.LLM_ACTIVE.toHtml()
        return message("action.tooltip", modelName, colorHtml)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val llmClient = project.service<ProjectSettings>().getActiveLLMClientConfiguration()
        if (llmClient == null) {
            sendNotification(Notification.clientNotSet())
            return
        }

        val generateCommitMessageJob = llmClient.getGenerateCommitMessageJob()
        if (generateCommitMessageJob?.isActive == true) {
            generateCommitMessageJob.cancel()
            return
        }

        val commitWorkflowHandler = e.getData(VcsDataKeys.COMMIT_WORKFLOW_HANDLER) as AbstractCommitWorkflowHandler<*, *>?
        if (commitWorkflowHandler == null) {
            sendNotification(Notification.noCommitMessage())
            return
        }

        val commitMessage = VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.dataContext) as CommitMessage?
        if (commitMessage == null) {
            sendNotification(Notification.noCommitMessage())
            return
        }

        llmClient.generateCommitMessage(commitWorkflowHandler, commitMessage, project)
    }
}
