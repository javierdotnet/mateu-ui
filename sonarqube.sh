mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.organization=miguelperezcolom-github \
    -Dsonar.login=106bb1a67de77467bec4f535e4750a8633d3b64c