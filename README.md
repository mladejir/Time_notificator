# Time notificator (for Android)
### Author
- Jiří Mládek

### Development

Application was developed in Android Studio, with OpenJDK 8. Gradle was used for building. In terms of application environment, minimum SDK is set on Android 7.0 (Nougat).

### Goals of app
Application can be used when user needs to get notification periodically. To me great usage of the app would be as a workout helper. User would set e.g. 30 seconds interval and then can work out for 30 seconds, rest for 30 seconds and all over again as many times as he wants. Once he is exhausted, he just sets the notifications off. So he doesn't have to set the timer each time, just once. 

### How application works
User clicks on a blue timer icon of app. After opening app, user is told to set the interval of seconds/minutes/hours, in which notifications should appear. Number in range from 1 to 10000 needs to be inserted, otherwise an error occurs. Then user needs to specify the unit, he has 3 options: seconds/minutes/hours. To set the interval click the button **SET INTERVAL**. 
Then apperars a message, which shows success or an error. If setting interval was successfull, the time of next notification is shown. If user wants to stop the interval, then he should click **STOP** button. If user wants to set a new interval, just insert data again and do the same steps, old interval will be stopped. User is notified even when the application is run in background.
