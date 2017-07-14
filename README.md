# json-log-formater
Simple json log formater

# Payara micro and docker usage
` 
mvn clean package 
`

Dockerfile:

```

COPY lib/json-log-formater.jar  $PAYARA_PATH/json-logging.jar

CMD java $JAVA_OPTS \
	 	-Djava.util.logging.config.file=/opt/payara/logging.properties \
	 	-Dcontainer.hostname=$HOSTNAME \
	 	-cp "$PAYARA_PATH/payara-micro.jar:$PAYARA_PATH/json-log-formater.jar" \
	 	fish.payara.micro.PayaraMicro \
		--deploy /opt/payara/deployments/<some-war> \
		--logproperties /opt/payara/logging.properties
```

