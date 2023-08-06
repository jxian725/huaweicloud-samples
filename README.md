# huaweicloud-samples

In order to try this demo, you will need to configure your Huawei Cloud credentials at the [application.properties](https://github.com/jxian725/huaweicloud-samples/blob/main/src/main/resources/application.properties).
<br>You may retrieve the credentials on Huawei Cloud console.
<br><br>
### Updating ECS
1. Build Package and rename the package to app.jar
```
mvn clean package
```
2. Using FTP/SFTP to upload the app.jar to ECS
```
dir /home/springboot/root/
```
4. Restart springboot service
```
systemctl restart springboot
```
