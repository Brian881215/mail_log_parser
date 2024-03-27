mvn clean package -DskipTests
java -DPHISH_DB_PWD='TWMDat2023@' -DPHISHDB_JDBC_URL=jdbc:mysql://35.201.162.92:3306/phishdb -DGCP_SIGNURL_KEY_NAME=education-video-key2 -DGCP_SIGNURL_KEY_VALUE=9oo4MrDACCRwHzOtsqzE9w== -DSMTP_HOST=127.0.0.1 -DJWT_SECRET=taiwanmobilesocialengineerproduct2023taiwanmobilesocialengineerproduct2023 -jar ./target/smstest-0.0.1-SNAPSHOT.jar 
