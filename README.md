# DevOps Shared Library – `devops-shared-lib`

A reusable Jenkins Shared Library for CI/CD pipelines.

## Repository Layout

```
vars/
  notifySlack.groovy       # Email/Slack notification step
  buildAndPushImage.groovy # Docker build step
  runSonarScan.groovy      # SonarQube scanner step
src/org/myteam/
  NotificationService.groovy  # OOP class for notifications
  DockerHelper.groovy         # OOP class for Docker operations
README.md
```

---

## Registration in Jenkins

**Manage Jenkins → System → Global Pipeline Libraries → Add**

| Field            | Value                    |
|------------------|--------------------------|
| Name             | `devops-shared-lib`      |
| Default version  | `main`                   |
| Load implicitly  | ❌ (disabled)            |
| SCM              | GitHub                   |
| Repository URL   | `https://github.com/ramisali007/jenkins-shared-library` |

---

## `vars/` Global Steps

### `notifySlack(config)`
Sends an HTML email notification. Despite the name (kept for assignment compatibility), it uses Jenkins `emailext`.

**Parameters (all required):**
| Key        | Type   | Description                       |
|------------|--------|-----------------------------------|
| `status`   | String | `'SUCCESS'` or `'FAILURE'`        |
| `message`  | String | Human-readable summary            |
| `buildUrl` | String | `env.BUILD_URL`                   |
| `recipient`| String | Email address to notify           |

**Usage:**
```groovy
@Library('devops-shared-lib') _

notifySlack([
  status:    'SUCCESS',
  message:   'Build passed!',
  buildUrl:  env.BUILD_URL,
  recipient: 'team@example.com'
])
```

---

### `buildAndPushImage(config)`
Builds a Docker image with two tags (git SHA + branch name).

**Parameters (all required):**
| Key              | Type   | Description                          |
|------------------|--------|--------------------------------------|
| `imageName`      | String | Image name, e.g. `devops-app`        |
| `ecrRepo`        | String | Full ECR repo URI                    |
| `gitSha`         | String | Short git commit SHA                 |
| `branchName`     | String | Branch name (slashes replaced by `-`)|
| `dockerfilePath` | String | Path to Dockerfile                   |
| `context`        | String | Docker build context directory       |

**Usage:**
```groovy
buildAndPushImage([
  imageName:      'devops-app',
  ecrRepo:        '123.dkr.ecr.us-east-1.amazonaws.com/devops-app',
  gitSha:         'a1b2c3d',
  branchName:     'main',
  dockerfilePath: 'assignment-4/app/Dockerfile',
  context:        'assignment-4/app'
])
```

---

### `runSonarScan(config)`
Runs SonarQube Scanner inside `withSonarQubeEnv`.

**Parameters (all required):**
| Key           | Type   | Description                                    |
|---------------|--------|------------------------------------------------|
| `projectKey`  | String | SonarQube project key                          |
| `projectDir`  | String | Directory containing `sonar-project.properties`|
| `sonarServer` | String | Server name registered in Jenkins system config|

**Usage:**
```groovy
runSonarScan([
  projectKey:  'devops-app',
  projectDir:  'assignment-4/app',
  sonarServer: 'sonarqube-server'
])
```

---

## `src/org/myteam/` Classes

### `NotificationService`
```groovy
import org.myteam.NotificationService

def notifier = new NotificationService(this, 'default@example.com')
notifier.sendEmail('team@example.com', 'Subject', '<p>Body HTML</p>')
notifier.sendSlack('Pipeline completed successfully')
```

### `DockerHelper`
```groovy
import org.myteam.DockerHelper

def docker = new DockerHelper(this, 'us-east-1')
docker.ecrLogin('123.dkr.ecr.us-east-1.amazonaws.com')
docker.buildImage('devops-app', 'a1b2c3d', 'Dockerfile', '.')
docker.pushImage('devops-app:a1b2c3d', '123.dkr.ecr.us-east-1.amazonaws.com/devops-app', 'a1b2c3d')
```
