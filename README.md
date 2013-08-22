Cloud Adventure
===============

Copyright
---------

Copyright 2013 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Disclaimer
----------

This sample application is not an official Google product.


Summary
-------
Cloud Adventure is a mobile game application built entirely upon Google’s Cloud Platform. This package includes the App Engine backend code, which exposes endpoints that the Android client will call. The setup instructions in this README are the same as the ones you will find in the corresponding Android client package. You will need both packages for this sample to work.

Supported Components
--------------------

Languages:
Java

Google Components:
App Engine
Cloud Endpoints
Android

Downloads
---------

* Make sure you have Java installed.
* Download and set up [Eclipse] (http://www.eclipse.org/downloads/). This sample application was built with Eclipse 3.8, but later versions are fine.
* Download the [App Engine Java SDK] (http://googleappengine.googlecode.com/files/appengine-java-sdk-1.8.0.zip)
* Download the [Google Plugin for Eclipse] (https://developers.google.com/eclipse/docs/getting_started) for your IDE version. Set it up using the instructions on that page.
* Download the [ADT Plugin] (http://developer.android.com/tools/sdk/eclipse-adt.html). Set it up using the instructions on that page. Note: upon Eclipse restart, you may see a dialog saying that there is no Android SDK installed. If you cancel that dialog, you should see another one just below it that will prompt you to install the Android SDK.

Download the following sample code:
* [Android client zip file] (https://github.com/GoogleCloudPlatform/solutions-cloud-adventure-sample-android-client)
* [App Engine backend zip file] (https://github.com/GoogleCloudPlatform/solutions-cloud-adventure-sample-backend-java)


Creating and Setting up Projects
--------------------------------

#### App Engine

Create a new application from the [App Engine] (https://appengine.google.com/) dashboard. If you have not developed applications using App Engine before, use the [Getting Started Guide] (https://developers.google.com/appengine/docs/java/gettingstarted/) as reference.

#### API Project

Create a new project from [the API console] (https://code.google.com/apis/console/). You may also use an existing project if you have one.

Navigate to Services using the left-hand menu. Enable the following Services in your project:

* Google Cloud Messaging for Android

Navigate to API Access using the left-hand menu. Under "Simple API Access", create a new Server Key. This will generate an API key, which you will need for your application to use Google Cloud Messaging from this project. To reiterate: you need the Server Key, not the Android one. You can read more about API keys [here] (https://developers.google.com/console/help/#generatingdevkeys).


Setting up Projects in Eclipse
------------------------------

You should have downloaded both the client and server sample code. Extract those files.

1) You should have set up the ADT plugin (see Downloads section) prior to this point. Select Window → Android SDK Manager in Eclipse. The Android SDK Manager window will pop up.

Select this version of Android:
* Android 4.2.2 (API 17)
Under Tools, select:
* Android SDK Tools
* Android SDK Platform-tools
* Android SDK Build-tools (revision 17)
Under Extras, select:
* Android Support Library
* Google Cloud Messaging for Android

Install these packages. Note: you may use the latest versions beyond API 17, but be aware that this code sample was created using API 17. Also, Google Cloud Messaging for Android may show as deprecated if you use the newest version, since a new version was recently introduced.

2) Create the Android client project in Eclipse: select File → New → Project... and then select Android → Android Application Project. Input "CloudAdventure" as the Application Name. Input "com.google.cloud.solutions.cloudadventure" as the Package Name. Click through the rest of the setup, leaving the defaults as-is.

3) Create the App Engine backend project in Eclipse: right-click on the CloudAdventure project, and select Google → Generate App Engine Backend. You will need to enter in your API key and Project Number from the API project you created in the Creating and Setting up Projects section. Select App Engine version 1.8.1 or higher. This project was created using App Engine version 1.8.1.

4) In the newly-created Android client project:
* Remove any pre-generated code under the src/ folder. Copy into the src/ folder the sample code from the downloaded CloudAdventure src/ folder.
* Remove any pre-generated code under the res/ folder. Copy into the res/ folder the sample code from the downloaded CloudAdventure res/ folder.
* Replace the root-level files with these 5 root-level files from the downloaded CloudAdventure code:
> AndroidManifest.xml
> ic_launcher-web.png
> proguard-google-api-client.txt
> proguard-project.txt
> project.properties
* Replacing the AndroidManifest.xml may cause Eclipse to prompt whether or not you want to accept the target changes. Select the "accept" or "OK" option.

5) In the newly-created App Engine project:
* Remove any pre-generated code under the src/ folder. Copy into the src/ folder the sample code from the downloaded CloudAdventure-AppEngine src/ folder.
* Leave the war/WEB-INF/ folder as it is, but remove the other pre-generated files under war/. Copy into the war/ folder the sample code from the downloaded CloudAdventure-Appengine war/ folder.

6) Generate your API library: right-click the App Engine project and select Google → Generate Cloud Endpoint Client Library. This will create an endpoint-libs/ folder in your Android client and add files there. If you run into any problems, check the Error Log View in Eclipse. Select Window → Show View → Error Log.

Configuring the Sample
----------------------

#### App Engine

* com.google.cloud.solutions.cloudadventure.CloudMessenger.java: update the API_KEY string. This is your Server Key from the Creating and Setting up Projects section.
* war/WEB-INF/appengine-web.xml: change the text in the <application> tag to the Application Identifier of the App Engine application created in the Creating and Setting up Projects section.
* Make sure your Eclipse is signed in with the same Google account that your App Engine application is created under. You can see the Google account in the lower right-hand corner of your Eclipse window. Right-click on the top-level of the project and select Google → Deploy to App Engine.

#### API Project

* com.google.cloud.solutions.cloudadventure.GCMIntentService.java: update the SENDER_ID string. This is the Project Number of your API project.
* AndroidManifest.xml: if you used an Android API version that is higher than 17, change the text in android:targetSdkVersion to your API version.
* If you want to do local testing on an emulator, you need to open CloudEndpointUtils.java and update the variable: LOCAL_ANDROID_RUN = true. Be sure to set it back to false when attempting to test on an Android device. This is not applicable to this particular code sample because it uses Google Accounts on your Android.

Running the Android Application
-------------------------------

This mobile game uses Google Accounts from your phone to sign in. Make sure you have at least one Google Account on your phone by going to Settings → Accounts.

Turn on Developer Options for your phone. This option should be in Settings, though toggling the option is a procedure that varies from phone to phone. Do a Google search for your phone on how to do this.

Connect your Android phone to your computer via USB. Right-click on the top-level of the project and select Run As → Android Application. A window will pop up asking which Android device you would like to use. Select your phone from the list and run.

Modifying the Code
------------------

#### App Engine

* Start by adding a couple fields to the data  model. When you build the project (or if Build Automatically is turned on), you will find fresh .api and .discovery files in your WEB-INF directory. These files specify how your API should be generated.
* You can also try adding a new API method. These are annotated with @ApiMethod and can be found the classes ending with “Endpoint.java”.
* When you are done with modifying the sample data model, right-click on the top-level of this project and select Google → Generate Cloud Endpoint Library. This will take your api files and generate API Java files in your Android client under endpoint-libs/.

#### API Project

* You have new fields in your model, and perhaps new API methods! Use them in your Android client.


