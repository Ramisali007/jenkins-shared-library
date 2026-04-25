// vars/buildAndPushImage.groovy
// ──────────────────────────────────────────────────────────────────────────────
// Global variable step: build a Docker image and push two tags to AWS ECR.
// Usage:
//   buildAndPushImage([
//     imageName:      'devops-app',
//     ecrRepo:        '123456789.dkr.ecr.us-east-1.amazonaws.com/devops-app',
//     gitSha:         'a1b2c3d',
//     branchName:     'main',
//     dockerfilePath: 'assignment-4/app/Dockerfile',
//     context:        'assignment-4/app'
//   ])
// ──────────────────────────────────────────────────────────────────────────────

def call(Map config) {
    // ── Validate required keys ────────────────────────────────
    ['imageName', 'ecrRepo', 'gitSha', 'branchName', 'dockerfilePath', 'context'].each { key ->
        if (!config.containsKey(key) || !config[key]) {
            error "[buildAndPushImage] Missing required parameter: '${key}'"
        }
    }

    String imageName      = config.imageName
    String ecrRepo        = config.ecrRepo
    String gitSha         = config.gitSha
    String branchName     = config.branchName.replaceAll('/', '-')
    String dockerfilePath = config.dockerfilePath
    String context        = config.context

    String tagSha    = "${ecrRepo}:${gitSha}"
    String tagBranch = "${ecrRepo}:${branchName}"

    echo "[buildAndPushImage] Building ${imageName}  tags: ${gitSha}, ${branchName}"

    sh """
        docker build \
          -f ${dockerfilePath} \
          -t ${tagSha} \
          -t ${tagBranch} \
          ${context}
    """

    echo "[buildAndPushImage] Image built successfully: ${tagSha}"
}
