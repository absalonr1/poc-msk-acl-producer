# poc-msk-acl-producer

MSK_ZOOKEEPERS=z-1.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:2181,z-3.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:2181,z-2.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:2181

MSK_BROKERS=b-1.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:9096,b-2.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:9096


######################################################

[AmazonMSK_fullAccesss; full/access]

echo -n "security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="full" password="access";" >  /tmp/client.properties_full

	./kafka-acls.sh \
	--bootstrap-server $MSK_BROKERS \
	--add \
	--allow-principal User:full \
	--operation All \
	--allow-host '*' \
	--topic '*' \
	--command-config /tmp/client.properties_full \
	--group '*'

######################################################

[AmazonMSK_readOnly; read/only]

echo -n "security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="read" password="only";" >  /tmp/client.properties_read

	./kafka-acls.sh \
	--bootstrap-server $MSK_BROKERS \
	--add \
	--allow-principal User:read \
	--operation Read \
	--operation Describe \
	--allow-host '*' \
	--topic '*' \
	--command-config /tmp/client.properties_read \
	--group '*'
	
######################################################

./kafka-acls.sh \
--bootstrap-server $MSK_BROKERS \
--add \
--allow-principal User:user \
--operation Read \
--topic test_topic \
--group * \
--command-config /tmp/client.properties_user

./kafka-acls.sh \
--bootstrap-server $MSK_BROKERS \
--remove \
--allow-principal User:user \
--operation Read \
--topic test_topic \
--group * \
--command-config /tmp/client.properties_user

./kafka-acls.sh \
--bootstrap-server $MSK_BROKERS \
--add \
--allow-principal User:user \
--operation Write \
--topic test_topic \
--command-config /tmp/client.properties_user


./kafka-acls.sh \
--bootstrap-server $MSK_BROKERS \
--add \
--allow-principal User:user \
--operation Write \
--topic excargonet_ \
--resource-pattern-type prefixed \
--command-config /tmp/client.properties_user




(ResourceType GROUP only supports operations READ,DESCRIBE,DELETE,ALL)

######################################################

./kafka-acls.sh \
--authorizer-properties zookeeper.connect=$MSK_ZOOKEEPERS \
--add \
--allow-principal "User:CN=*.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com" \
--operation All \
--group '*'

./kafka-acls.sh \
--authorizer-properties zookeeper.connect=$MSK_ZOOKEEPERS \
--add \
--allow-principal "User:CN=*.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com" \
--operation All \
--topic '*'

######################################################

./kafka-topics.sh \
--list \
--zookeeper "$MSK_ZOOKEEPERS"

./kafka-acls.sh \
--authorizer kafka.security.auth.SimpleAclAuthorizer \
--authorizer-properties zookeeper.connect=$MSK_ZOOKEEPERS \
--list \
--topic test_topic



echo -n "security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \\
  username="user" \\
  password="pass";
" > /tmp/client.properties_user



./kafka-acls.sh \
--bootstrap-server $MSK_BROKERS \
--list \
--command-config /tmp/client.properties_user \
--topic test_topic


./kafka-acls.sh \
--authorizer kafka.security.auth.SimpleAclAuthorizer \
--authorizer-properties zookeeper.connect=$MSK_ZOOKEEPERS \
--add \
--allow-principal User:bx.user.msk.dev \
--allow-hosts * \
--operation Read \
--operation Read \
--operation Read \
--topic test_topic


--resource-pattern-type prefixed


./kafka-topics.sh \
--create \
--zookeeper "$MSK_ZOOKEEPERS" \
--replication-factor 2 \
--partitions 2 \
--topic excargonet_topic1

./kafka-topics.sh \
--create \
--zookeeper "$MSK_ZOOKEEPERS" \
--replication-factor 2 \
--partitions 2 \
--topic excargonet_topic2


./kafka-console-consumer.sh \
--bootstrap-server "$MSK_BROKERS"  \
--consumer.config ./dev/test1.properties \
--topic excargonet_carga_tmp_cubicado \
--from-beginning

------------------------------------

security.protocol=SASL_SSL
ssl.protocol=TLSv1.2
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \
    username="bx.user.msk.dev" \
    password="cqGVch0Zmh3ou0UoWJRbZyq01qYcUR4I";

------------------------------------


