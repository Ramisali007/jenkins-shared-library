// src/org/myteam/NotificationService.groovy
package org.myteam

/**
 * NotificationService
 *
 * Provides sendEmail() and sendSlack() notification methods.
 * Receives the Jenkins script context (this) in its constructor
 * so it can call pipeline steps such as emailext.
 *
 * Usage inside a Jenkinsfile:
 *   @Library('devops-shared-lib') _
 *   import org.myteam.NotificationService
 *   def notifier = new NotificationService(this)
 *   notifier.sendEmail('team@example.com', 'Build Failed', 'Check Jenkins.')
 */
class NotificationService implements Serializable {

    private final def script
    private final String defaultRecipient

    /**
     * @param script          The Jenkins script context (pass `this` from Jenkinsfile)
     * @param defaultRecipient  Fallback email when none is provided
     */
    NotificationService(def script, String defaultRecipient = '') {
        this.script           = script
        this.defaultRecipient = defaultRecipient
    }

    /**
     * Send an HTML email notification via Jenkins emailext.
     *
     * @param to      Recipient email address
     * @param subject Email subject line
     * @param body    HTML body content
     */
    void sendEmail(String to, String subject, String body) {
        String recipient = to ?: defaultRecipient
        if (!recipient) {
            script.error '[NotificationService] sendEmail: no recipient specified'
        }
        script.emailext(
            subject:   subject,
            body:      body,
            mimeType:  'text/html',
            to:        recipient,
            replyTo:   recipient,
            attachLog: false
        )
        script.echo "[NotificationService] Email sent to ${recipient}: ${subject}"
    }

    /**
     * Send a Slack-style notification.
     * In this implementation, delivers via email (Slack webhooks can be
     * added here if a workspace is configured later).
     *
     * @param message  The notification message text
     */
    void sendSlack(String message) {
        sendEmail(
            defaultRecipient,
            "[Jenkins] Pipeline Notification",
            "<p>${message}</p>"
        )
    }
}
