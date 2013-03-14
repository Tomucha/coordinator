#### How to build

1. Add `local.properties` containing the location of Android SDK.
2. This project uses the ActionBarShelock library project which should be added manually. Its location is specified in project.properties.
3. Create `SecretInfo.java` (see `coordinator-shared/src/main/java/cz/clovekvtisni/coordinator/SecretInfoTemplate.java` for more info).
4. Run `mvn clean install` in the parent directory.
5. Run `ant release`.
