// vars/runSonarScan.groovy
// ──────────────────────────────────────────────────────────────────────────────
// Global variable step: run the SonarQube Scanner against a project directory.
// Usage:
//   runSonarScan([
//     projectKey:  'devops-app',
//     projectDir:  'assignment-4/app',
//     sonarServer: 'sonarqube-server'   // name registered in Jenkins system config
//   ])
// ──────────────────────────────────────────────────────────────────────────────

def call(Map config) {
    // ── Validate required keys ────────────────────────────────
    ['projectKey', 'projectDir', 'sonarServer'].each { key ->
        if (!config.containsKey(key) || !config[key]) {
            error "[runSonarScan] Missing required parameter: '${key}'"
        }
    }

    String projectKey  = config.projectKey
    String projectDir  = config.projectDir
    String sonarServer = config.sonarServer

    withSonarQubeEnv(sonarServer) {
        dir(projectDir) {
            sh """
                sonar-scanner \
                  -Dsonar.projectKey=${projectKey} \
                  -Dsonar.sources=src \
                  -Dsonar.tests=tests \
                  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \
                  -Dsonar.exclusions=node_modules/**,coverage/**
            """
        }
    }

    echo "[runSonarScan] SonarQube scan submitted for project: ${projectKey}"
}
