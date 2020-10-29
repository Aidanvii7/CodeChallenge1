# CodeChallenge App

## Tech stack

## Tools versions
Deveoped with Android Studio 4.2 Canary 11, most recently tested version is 4.2 Canary 15. **You will need 4.2 Canary 15 to build and run the project, you can find it [here](https://developer.android.com/studio/archive).**

## UI/presentation related libraries
 - Jetpack Compose + associated integration libraries
 - AndroidX Lifecycle

### Network related libraries
 - Retrofit2
 - OkHTTP3
 - Moshi + codegen
 - EitherNet (Slack)
 
### High level architecture
I wanted to implement the app using some form of unidirectional data flow model (as redux was mentioned in the spec). I looked into various third party libraries such as MvRx + Epoxy that seemed like a good fit for that, and also has some traction in the community. I'm largely unfamiliar with those libraries though, and after comparing them to Compose, I realized they were both going for a similar programming model. I eventually decided that Compose was a better fit as it allowed me to model almost the entire app state and navigation flow without having to bridge into traditional android components such as (multiple) activities and fragments. Having tried a Redux-like approach in the past within a traditional app (with fragments and androidX ViewModels), I learned that it was better suited to scoped parts of an application, i.e. each screen would have it's own state machine. This was a lot more manageable than having a single state machine for the entire application while trying to model navigation by bridging into traditional android navigation.

## The app itself
The app has 3 main screens: credentials, authenticating and user profile. Upon first run, the user is prompted for their credentials. When both the email and password are non-empty, they can login. They are then presented with the athenticating screen - a simple loading spinner if the phone is able to reach the API. It's worth mentioning here that there are 2 modes (actually app flavours) that the app can be run in, "normal" and "stubbed". Running the "normal" variant will hit an Apiary mock that I've drafted out. Runninng the app in "stubbed" mode will run when also when the phone does not have internet access, it merely uses some stubbed side effects for responding to certain actions and providing fake data back to the UI - this allowed me to draft out the flow of the app without worrying too much about the API layer. Once the user is logged in they will see their avatar image from Gravatar if they have an image associated withe their email address, but only if the apiary mock returns an empty avatar URL. The user is able to select a photo from their gallery, or take a photo with their camera. Once they have done so, they will be given an option to accept or decline the new avatar photo. Declining will simply revert the currently displaying image back to it's previous state. Confirming will attempt to upload the new photo - this part does not work properly unfortunately (it was almost there but ust ran out of time). Either way, because the apiary mock always returns an empty avatar URL, the URL will not e populated, and thus an error image is displayed instead. if the app is killed and restarted, it will attempt to log back in with the previously saved credentials (stored in encrypted shared preferences), if it fails (e.g. the apiary mock returns a 404), it will not login and instead put the user back to ther credentials screen. Currently there are no error messages displayed to the user other than a missing image, but the wiring is there to allow for it. The user is also able to logout, I added this for testing purposes so that I could verify the first run behaviour without wiping the app data. The app also fully supports dark mode if enabled by the user.

Please see the screenshots and apks folder.
