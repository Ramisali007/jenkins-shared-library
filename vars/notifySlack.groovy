// vars/notifySlack.groovy
// ──────────────────────────────────────────────────────────────────────────────
// Global variable step: send an email notification from any pipeline.
// Usage:
//   notifySlack([
//     status:    'SUCCESS' | 'FAILURE',
//     message:   'Human-readable summary',
//     buildUrl:  env.BUILD_URL,
//     recipient: 'team@example.com'
//   ])
// ──────────────────────────────────────────────────────────────────────────────

def call(Map config) {
    // ── Validate required keys ────────────────────────────────
    ['status', 'message', 'buildUrl', 'recipient'].each { key ->
        if (!config.containsKey(key) || !config[key]) {
            error "[notifySlack] Missing required parameter: '${key}'"
        }
    }

    String status    = config.status
    String message   = config.message
    String buildUrl  = config.buildUrl
    String recipient = config.recipient
    String subject   = "[Jenkins] Build ${status}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
    String body      = """
<html>
  <body style="font-family: Arial, sans-serif; color: #333;">
    <h2 style="color: ${status == 'SUCCESS' ? '#27ae60' : '#e74c3c'};">
      ${status == 'SUCCESS' ? '✅' : '❌'} Pipeline ${status}
    </h2>
    <p><b>Job:</b> ${env.JOB_NAME}</p>
    <p><b>Build:</b> #${env.BUILD_NUMBER}</p>
    <p><b>Branch:</b> ${env.BRANCH_NAME ?: 'N/A'}</p>
    <p><b>Message:</b> ${message}</p>
    <p><a href="${buildUrl}">View Build in Jenkins</a></p>
  </body>
</html>
"""

    emailext(
        subject:     subject,
        body:        body,
        mimeType:    'text/html',
        to:          recipient,
        replyTo:     recipient,
        attachLog:   (status == 'FAILURE')
    )

    echo "[notifySlack] Email sent to ${recipient} with status ${status}"
}
