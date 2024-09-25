FROM wiremock/wiremock:3.3.1-1

COPY api-stubs/ /home/wiremock/mappings

ADD https://repo1.maven.org/maven2/org/wiremock/extensions/wiremock-faker-extension-standalone/0.1.1/wiremock-faker-extension-standalone-0.1.1.jar /var/wiremock/extensions/

CMD ["--global-response-templating", "--disable-gzip", "--verbose", "--extensions", "org.wiremock.RandomExtension"]
