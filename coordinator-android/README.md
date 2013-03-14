#### How to build

1. Add local.properties containing the location of Android SDK.
2. This project uses the ActionBarShelock library project which should be added manually. Its location is specified in project.properties.
3. Create SecretInfo.java (see coordinator-shared/src/main/java/cz/clovekvtisni/coordinator/SecretInfoTemplate.java for more info).
4. In the parent directory, run <pre>mvn clean install</pre>
5. Run <pre>ant release</pre>
