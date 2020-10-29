## Tech stack

### UI/presentation related libraries
 - [Jetpack Compose + associated integration libraries](https://developer.android.com/jetpack/compose)
 - [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
 - [Accompanist: Coil by Chris Banes](https://github.com/chrisbanes/accompanist/tree/main/coil)

### Network related libraries
 - [Retrofit2](https://square.github.io/retrofit/)
 - [OkHTTP3](https://square.github.io/okhttp/)
 - [Moshi + codegen](https://github.com/square/moshi)
 - [EitherNet (Slack)](https://github.com/slackhq/EitherNet)
 
 ## Other
 - [Koin for dependency injection/service locator](https://insert-koin.io)

 
## Tools versions
Developed with Android Studio 4.2 Canary 11, most recently tested version is 4.2 Canary 15. **You will need 4.2 Canary 15 to build and run the project, you can find it [here](https://developer.android.com/studio/archive).**

 
## High level architecture
I wanted to implement the app using some form of unidirectional data flow model (as redux was mentioned in the spec). I looked into various third party libraries such as MvRx + Epoxy that seemed like a good fit for that, and also has some traction in the community. I'm largely unfamiliar with those libraries though, and after comparing them to Compose, I realized they were both going for a similar programming model. I eventually decided that Compose was a better fit as it allowed me to model almost the entire app state and navigation flow without having to bridge into traditional android components such as (multiple) activities and fragments. Having tried a Redux-like approach in the past within a traditional app (with fragments and androidX ViewModels), I learned that it was better suited to scoped parts of an application, i.e. each screen would have it's own state machine. This was a lot more manageable than having a single state machine for the entire application while trying to model navigation by bridging into traditional android navigation. The architecture is mostly inspired by MVI, though I did deviate slightly from some examples I found. 

Generally, the [AppStateProcessor](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppStateProcessor.kt) holds the current state of the app ([AppState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppState.kt)), including the view state ([ViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/ViewState.kt), a sealed class heirarchy). The [AppState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppState.kt) is expossed via a `StateFlow` that the UI subscribes to ([here](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/CodeChallengeActivity.kt)). The [ViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/ViewState.kt) is able to build itself via an abstract composable build function that is implemented by each subtype of [ViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/ViewState.kt) ([inspired by this article](https://medium.com/swlh/android-mvi-with-jetpack-compose-b0890f5156ac)). The Views/composbles recieve a [DispatchIntent](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/dispatchers.kt) function which they can use to send different [Action.Intent](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/Action.kt) to the [AppStateProcessor](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppStateProcessor.kt), depending on the user's action. The [AppStateProcessor](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppStateProcessor.kt) will reduce the [AppState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/AppState.kt) and [ViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/ViewState.kt) to it's new state via a call to [AppState.reduceWith](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/reducers.kt), it will then emit the new state via a `StateFlow` which is observed in the `onCreate` of the [CodeChallengeActivity](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/CodeChallengeActivity.kt). The [ScreenSurfaceWithViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/screens/ScreenSurface.kt) will propagate the state down to the [ScreensFromState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/ScreensFromState.kt) composable which is responsible for showing or hiding each of the screens ([EnterCredentialsScreen](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/screens/EnterCredentialsScreen.kt), [AuthenticatingScreen](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/screens/AuthenticatingScreen.kt) and [ProfileScreen](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/screens/ProfileScreen.kt)) depending on the current [ViewState](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/state/ViewState.kt).

Some composables maintain their own state, this was easier in certain cases than modelling the entire state via MVI. State such as deciding when and when not to enable the login button is managed by the [EnterCredentialsScreen](https://github.com/Aidanvii7/CodeChallenge1/blob/main/App/app/src/main/java/com/aidanvii/codechallenge/ui/screens/EnterCredentialsScreen.kt), plus a few more cases.


## The app itself
The app has 3 main screens: credentials, authenticating and user profile. Upon first run, the user is prompted for their credentials. When both the email and password are non-empty, they can login. They are then presented with the athenticating screen - a simple loading spinner if the phone is able to reach the API. It's worth mentioning here that there are 2 modes (actually app flavours) that the app can be run in, "normal" and "stubbed". Running the "normal" variant will hit an Apiary mock that I've drafted out. Runninng the app in "stubbed" mode will run when also when the phone does not have internet access, it merely uses some stubbed side effects for responding to certain actions and providing fake data back to the UI - this allowed me to draft out the flow of the app without worrying too much about the API layer. Once the user is logged in they will see their avatar image from Gravatar if they have an image associated withe their email address, but only if the apiary mock returns an empty avatar URL. The user is able to select a photo from their gallery, or take a photo with their camera. Once they have done so, they will be given an option to accept or decline the new avatar photo. Declining will simply revert the currently displaying image back to it's previous state. Confirming will attempt to upload the new photo - this part does not work properly unfortunately (it was almost there but ust ran out of time). Either way, because the apiary mock always returns an empty avatar URL, the URL will not e populated, and thus an error image is displayed instead. if the app is killed and restarted, it will attempt to log back in with the previously saved credentials (stored in encrypted shared preferences), if it fails (e.g. the apiary mock returns a 404), it will not login and instead put the user back to ther credentials screen. Currently there are no error messages displayed to the user other than a missing image, but the wiring is there to allow for it. The user is also able to logout, I added this for testing purposes so that I could verify the first run behaviour without wiping the app data. The app also fully supports dark mode if enabled by the user.

## Limitations, shortcomings and future improvements
 - Currently the image uploading mechanism does not work, this needs a bit if work to fix it.
 - The data layer of the app could be imporved upon by hiding the network calls behind data sources that the repository calls.
 - The app is a single module (for simplicity mainly), however in a real world app it may be better to split certain parts/layers out into their own modules to reduce the chances of coupling between layers. I have another repository that attempts to do exactly this, however it results in a lot more boilerplate to get it off the ground. See the diagrams [here](https://github.com/Aidanvii7/MercariApp) for an example.
 - Most asynchronous tasks are scheduled on the GlobalScope's dispatcher, this means that when the user navigates back from the profile screen to credentials, any ongoing work (say image upload) is not cancelled. This could be definitely be imporved upon, however the best way forward is not clear in compose. In traditional apps each screen would be it's own fragment, with it's own ViewModel that keeps work scoped to that screen. This may improve in the future, perhaps with future releases of the [navigation component for Compose](https://developer.android.com/jetpack/compose/navigation).
 
Please also see the screenshots and apks folder.
