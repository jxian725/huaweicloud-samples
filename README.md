# huaweicloud-samples

In order to try this demo, you will need to configure your Huawei Cloud credentials at the [application.properties](https://github.com/jxian725/huaweicloud-samples/blob/main/src/main/resources/application.properties).
<br>You may retrieve the credentials on Huawei Cloud console.
<br><br>
### Updating ECS
1. Import 3rd Party JAR into local repository
```
//place the java-sdk-core-3.1.2.jar at the project root
mvn install:install-file -Dfile="java-sdk-core-3.1.2.jar" -DgroupId="com.cloud.apigateway" -DartifactId="java-sdk-core" -Dversion="3.1.2" -Dpackaging="java-sdk-core-3.1.2.jar"
```
2. Build Package and rename the package to app.jar
```
mvn clean package
```
3. Using FTP/SFTP to upload the app.jar to ECS
```
dir /home/springboot/root/
```
4. Restart springboot service
```
systemctl restart springboot
```
