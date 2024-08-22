# HACKSense widget

**UNDER MAINTAINANCE, USE THE WEBSITE INSTEAD**
Website: [hsbp.org](https://hsbp.org)

Designed for [Hackserspace Budapest](https://hsbp.org), If you want to use this widget first you have to set it up based on the [HackSense](https://hsbp.org/HackSense) article article.

### Roadmap
- [x] Working with HSBP's API
- [ ] Add colors

### Sample 
![Sample](media/sample.png)

### Edit
You have to edit 2 things.  
1. The HackSense's url is located at `app/src/main/java/com/hacksense/app/RequestSender.java#L34`
```java
URL url = new URL("https://hacksense-url/status.json");
```
2. Hackerspace's name is located at `app/src/main/res/values/strings.xml#L4`
```xml
<string name="hackerspace_name">hackerspace-name</string>
```

### Build
```sh
./gradlew assembleRelease
```

### Keygen
```sh
keytool -genkey -v \
    -keystore hacksense.keystore \
    -keyalg RSA \
    -keysize 2048 \
    -sigalg SHA256withRSA \
    -validity 10000 \
    -alias hacksense
```

### Sign
```sh
apksigner sign \
    --ks hacksense.keystore \
    --ks-key-alias hacksense \
    --in ./app/build/outputs/apk/release/app-release-unsigned.apk \
    --out hacksense.apk
```

### Check sign
```sh
apksigner verify --verbose --print-certs hacksense.apk
```
