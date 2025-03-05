## RouterOS Monitoring Project

This is an Android project I developed with the help of Copilot. The code quality might not be the best, so please bear with me. The app is designed for landscape mode only and has been tested on Android 7.0.

### Features:
1. Uptime
2. System version
3. CPU usage
4. Memory usage
5. Firewall information
6. Upload and download traffic information

### Usage Instructions:
1. Create a new group in RouterOS user management and grant `read` and `api` permissions.
2. Create a new user and assign the newly created group to the user.
3. Tap the bottom left corner of the app to open the settings interface.
   ![Settings Interface](https://github.com/user-attachments/assets/635718ab-756c-4027-85cb-17fb185f4c30)
4. Configure the router IP, API port, username, and password.
   ![Configuration](https://github.com/user-attachments/assets/29d90b0c-f4f0-448d-a53d-743838a8af76)
5. Enjoy the app!
   ![App Interface](https://github.com/user-attachments/assets/b883a995-2006-4def-98fb-cd837a7047cf)

### Acknowledgements:
Thanks to the following open-source projects:
- [mikrotik_java](https://github.com/GideonLeGrange/mikrotik-java)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [CircularProgressIndicator](https://github.com/antonKozyriatskyi/CircularProgressIndicator)