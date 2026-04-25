// src/org/myteam/DockerHelper.groovy
package org.myteam

/**
 * DockerHelper
 *
 * Encapsulates Docker build and push operations for use across pipelines.
 * Receives the Jenkins script context so it can execute shell steps.
 *
 * Usage inside a Jenkinsfile:
 *   @Library('devops-shared-lib') _
 *   import org.myteam.DockerHelper
 *   def docker = new DockerHelper(this, 'us-east-1')
 *   docker.buildImage('devops-app', 'a1b2c3d', 'assignment-4/app/Dockerfile', 'assignment-4/app')
 *   docker.pushImage('123.dkr.ecr.us-east-1.amazonaws.com/devops-app', 'a1b2c3d')
 */
class DockerHelper implements Serializable {

    private final def    script
    private final String awsRegion

    /**
     * @param script     The Jenkins script context (pass `this` from Jenkinsfile)
     * @param awsRegion  AWS region for ECR authentication
     */
    DockerHelper(def script, String awsRegion = 'us-east-1') {
        this.script    = script
        this.awsRegion = awsRegion
    }

    /**
     * Build a Docker image with multiple tags.
     *
     * @param name            Base name of the image (e.g. 'devops-app')
     * @param tag             Primary tag (e.g. git SHA)
     * @param dockerfilePath  Path to Dockerfile relative to workspace root
     * @param context         Docker build context directory
     */
    void buildImage(String name, String tag, String dockerfilePath = 'Dockerfile', String context = '.') {
        script.echo "[DockerHelper] Building image ${name}:${tag}"
        script.sh """
            docker build \
              -f ${dockerfilePath} \
              -t ${name}:${tag} \
              ${context}
        """
    }

    /**
     * Tag and push a local image to a remote registry (e.g. AWS ECR).
     *
     * @param localImage    Local image reference (name:tag)
     * @param remoteRepo    Full ECR repository URI
     * @param tag           Tag to push
     */
    void pushImage(String localImage, String remoteRepo, String tag) {
        script.echo "[DockerHelper] Pushing ${localImage} → ${remoteRepo}:${tag}"
        script.sh """
            docker tag  ${localImage} ${remoteRepo}:${tag}
            docker push ${remoteRepo}:${tag}
        """
    }

    /**
     * Authenticate the Docker daemon to AWS ECR.
     *
     * @param ecrRegistry  ECR registry host (e.g. 123.dkr.ecr.us-east-1.amazonaws.com)
     */
    void ecrLogin(String ecrRegistry) {
        script.sh """
            aws ecr get-login-password --region ${awsRegion} \
              | docker login --username AWS --password-stdin ${ecrRegistry}
        """
        script.echo "[DockerHelper] Logged in to ECR: ${ecrRegistry}"
    }
}
