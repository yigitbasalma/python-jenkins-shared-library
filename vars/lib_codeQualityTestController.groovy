def call(Map config) {
    withSonarQubeEnv(config.sonarqube_env_name) {
        sh """
        export CONFIG=\$(find . -name "sonar-project.properties")

        if [ -z \$CONFIG ]
        then
            ${config.sonarqube_home}/bin/sonar-scanner \
            -Dsonar.projectKey=${config.sonar_qube_project_key} \
            -Dsonar.coverage.exclusions=**/__init__.py \
            -Dsonar.sources=.
        else
            ${config.sonarqube_home}/bin/sonar-scanner \
            -Dproject.settings=\$CONFIG 
            
        fi
        """
    }

    timeout(time: 1, unit: 'HOURS') {
        def qg = waitForQualityGate()
        if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
        }
    }
}
