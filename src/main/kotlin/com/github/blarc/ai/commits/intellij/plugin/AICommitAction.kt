package com.github.blarc.ai.commits.intellij.plugin

import com.github.blarc.ai.commits.intellij.plugin.AICommitsBundle.message
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
import com.intellij.ui.JBColor
import com.intellij.openapi.util.text.StringUtil
import java.awt.Color

class AICommitAction : AnAction(), DumbAware {


    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        e.project?.service<ProjectSettings>()?.getActiveLLMClientConfiguration()?.let { config ->
            if (config.getGenerateCommitMessageJob()?.isActive == true) {
                e.presentation.icon = Icons.Process.STOP.getThemeBasedIcon()
            } else {
                e.presentation.icon = config.getClientIcon()
                val sanitizedName = HtmlSanitizer.escapeHtml(config.name)
                val color = HtmlSanitizer.toHex(getModelColor())
                e.presentation.text = HtmlSanitizer.wrapHtml(message("action.tooltip", sanitizedName, color))
            }
        }
    }

    // Helper object to sanitize and format HTML content enduring proper format.
    object HtmlSanitizer {
        fun escapeHtml(str: String) = StringUtil.escapeXmlEntities(str)
        fun toHex(color: Color) = "#${"%06x".format(color.rgb and 0xFFFFFF)}"
        fun wrapHtml(content: String) = "<html>$content</html>"
    }

    private fun getModelColor(): Color {
        return JBColor.namedColor(
            "AICommits.ActiveModelNameHighlight", // takes values from plugin.xml which allows IntelliJ theme support with configurable color overrides, otherwise fallback to values below
            JBColor(Color(0x2B, 0x7A, 0xBF), Color(0x6B, 0xA4, 0xD9))
        )
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
