/etc/init.d/tor start
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -Duser.timezone=UTC -jar -Xmx256m miner.jar --dir /app/work