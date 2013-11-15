##R1 Connect Library for Android

##Overview##


Downloading LibR1Connect.jar allows you to begin the integration process of adding R1 Connect services to your Android app. All mobile and tablet devices running Android 2.2. and above are supported.
This integration doc assumes you have already set up Google Play Services in your application project. This is needed to use Google Cloud Messaging (GCM), the notification gateway R1 Connect will use for your Android app. Also you will need to have created the app you will be using in R1 Connect.
In order to use R1 Connect with your application you will need an project number (sender ID) and API key from Google. Please visit “GCM Getting Started” [here](http://developer.android.com/intl/ru/google/gcm/gs.html) and create google project and API key.

##Setup

The following steps will explain how to integrate with R1 Connect to enable both event tracking and push notifications. You have the option to use the R1 Connect Demo as a sample app project to begin with or you can use your own app project. 

Once you have downloaded the R1 Connect Library from this repo you can add it to your project in the libs directory. 

##Configuring your App

![Configure image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image1.png)


Next, to configure how the library will be used in your project you will need to create a file called **r1connect.properties** in the folder called “assets”.

![Parameters image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image2.png)
￼
As you can see in the example above, it will contain the following:

	sender_id – You will need to enter the project number you received when creating the Google API project

	app_id – You will need to enter the App ID you received when creating your app on R1 Connect (it’s found under Dev Tools -> Keys & Secrets)

	client_key – You will need to enter the App Key you received when creating your app on R1 Connect (it ‘s found under Dev Tools -> Keys & Secrets)

	enable_push – this defaults to “true” and it will enable push notifications or disable push notifications after you start your application. You change these settings later in your code.

	disable_sdk_location - when set to “true” it disables the use of sdk tracking location. It is useful if you want to use your own tracking location. You can pass a location object like so: R1Emitter.getInstance().trackLocation(location);
	
	enable_ gps – when set to “true” it enables the use of device GPS to get location (only if GPS is enabled in device settings), when “false” only the network is used

	location_update_time – this is the timeout between location updates

	location_update_distance – you can set the change in distance for location updates

	location_in_background – you can set whether or not the location is allowed to be sent while the app is in the background

###Enabling Push Notifications and Handling Push Events

To make sure push notifications work correctly, please follow these steps:

1. Create a class that inherits from the class Application (or you can use an existing one in the project)

2. To enable an action such as opening the app when a notification is clicked, create a class that inherits from BroadcastReceiver and add the necessary logic to it. If you are okay with the default, which closes the notification upon pressing it, then no further coding is required.

	![Broadcast Receiver image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image3.png)
		
3. The class referred to in the first item is used in the following way:

		R1Emitter.getInstance().setNotificationIconResourceId(this, R.drawable.ic_launcher);
		//The above line is necessary for creating an icon in the notification bar when the device receives the notification

		R1Emitter.getInstance().setIntentReceiver(this, TestPushReceiver.class);
		//This line tells the library that the class created in step 2 will be processing the push notification
		//In TestPushReceiver (see step 2) we want to open ShowNotificationActivity when notification is clicked
		
		R1Emitter.getInstance().connect(this); //To make sure the library works correctly it is necessary this line in onCreate() method

	![Application image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image4.png)
	
	If you want to create your own notifications you have to create a class that implements R1NotificationBuilder interface and write your notification builder like in the example below:
	
	![Custom notification image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image7.png)
	
	After that add this line just before R1Emitter.getInstance().connect(this) in your application class:
	
		R1Emitter.getInstance().setNotificationBuilder( new CustomNotificationBuilder()); 
	

4. To make sure the library works correctly it is also necessary to include the following in onStart and onStop methods in all your application Activities:

	![OnStart image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image5.png)
	![OnStop image](https://raw.github.com/radiumone/r1-connect-demo-Android/master/readme-images/image6.png)
￼

In the manifest you need to create the following:

	<!-- android:name of application tag must be full application name that was created in first step.-->

	<application
        android:name="com.example.r1connecttestapplication.TestApplication"
        android:allowBackup="true"

        android:icon="@drawable/ic_launcher"

        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
        
	<!—the next lines are your project activities-->

	//Then there are necessary fields for the library to work correctly (cont…):

	<receiver android:name="com.radiumone.emitter.gcm.R1GCMPushReceiver" android:exported="true"
                  android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <!-- name must be your applicationPackage -->
                <category android:name="com.radiumone.sdk" />
            </intent-filter>
        </receiver>

        <receiver android:name="com.radiumone.emitter.push.R1PushBroadcastReceiver"
                  android:exported="false">
            <intent-filter>
                <action android:name="com.radiumone.r1push.OPENED_INTERNAL"/>
            </intent-filter>
        </receiver>

	<service android:name="com.radiumone.emitter.push.R1ConnectService"/>
        <service android:name="com.radiumone.emitter.location.LocationService"/>

	//Using the class created in Step 2 (cont…):

	<receiver android:name=".testpush.TestPushReceiver"
                  android:exported="false">
            <intent-filter>
                <action android:name="com.radiumone.r1push.OPENED"/>
            </intent-filter>
        </receiver>

    </application>


	//Permissions that are necessary for the library to work:

	<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />


    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />

	//Permission to get location when using the network

	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

	//Permission to get location when using GPS

	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

	<uses-permission android:name="android.permission.WAKE_LOCK"></uses-permission>
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.VIBRATE"/>

	<!—(your_application_package).permission.C2D_MESSAGE -->

    <permission android:name="com.radiumone.sdk.permission.C2D_MESSAGE" android:protectionLevel="signature"/>
    <uses-permission android:name="com.radiumone.sdk.permission.C2D_MESSAGE"></uses-permission>


##Emitter & Push Parameters


The following is a list of configuration parameters for the R1 Connect SDK, most of these contain values that are sent to the tracking server to help identify your app on our platform and to provide analytics on sessions and location.

###Configuration Parameters

*appName*

The application name associated with emitter. By default, this property is populated with the package name of the application. If you wish to override this property, you must do so before making any tracking calls.
 
	R1Emitter.getInstance().setApplicationName("customApplicationName");

*appId*


The application identifier associated with this emitter. By default, this property is null. If you wish to set this property, you must do so before making any tracking calls.
Note that this is not your app's bundle id (e.g. com.example.appname).

	R1Emitter.getInstance().setApplicationUserId("12345");

*appVersion*


The application version associated with this emitter. By default, this property is populated with the android:versionName= string from the application AndroidManifest.xml. If you wish to override this property, you must do so before making any tracking calls.
 
	R1Emitter.getInstance().setAppVersion("1.0");


*appScreen*


The current application screen set for this emitter.

	R1Emitter.getInstance().emitAppScreen("appScreen", "contentDescription", "documentLocationUrl", "documentHostName", "documentPath", parameters);

	where parameters is a HashMap. Example:

	private HashMap<String, Object> parameters = new HashMap<String, Object>();
	parameters.put("key","value");


*sessionStart*


If true, indicates the start of a new session. Note that when a emitter is first instantiated, this is initialized to true. To prevent this default behavior, set sessionTimeout to negative value.
 By itself, setting this does not send any data. If this is true, when the next emitter call is made, a parameter will be added to the resulting emitter information indicating that it is the start of a session, and this flag will be cleared.
		
	R1Emitter.getInstance().setSessionStarted(true);
	// Your code here
	R1Emitter.getInstance().setSessionStarted(false);

*sessionTimeout*


Indicates how long, in seconds, the application must transition to the inactive or background state before the tracker automatically indicates the start of a new session. When this happens and the app becomes active again it will set sessionStart to true. For example, if this is set to 30 seconds, and the user receives a phone call that lasts for 45 seconds while using the app, upon returning to the app, the sessionStart parameter will be set to true. If the phone call instead lasted 10 seconds, sessionStart will not be modified.
By default, this is 30 seconds.

	R1Emitter.getInstance().setSessionTimeout(30);

*applicationUserID*


Optional current user identifier.
 
	R1Emitter.getInstance().setApplicationUserId("12345");
	
###Push Tags

You can specify Tags for *R1 Connect SDK* to send *Push Notifications* for certain groups of users.

The maximum length of a Tag is 128 characters.

*R1 Connect SDK* saves Tags. You do not have to add Tags every time the application is launched.

***Add a new Tag***

	R1Push.getInstance(context).addTag("tag");
	
	
***Remove existing Tag***

	R1Push.getInstance(context).removeTag("tag");	
	
***Get all Tags***
	
	String[] allTags = R1Push.getInstance(context).getTags(context);	


###Emitter Events

R1 Connect SDK will automatically capture some generic events, but in order to get the most meaningful data on how users interact with your app the SDK also offers pre-built user-defined events for popular user actions as well as the ability to create your own custom events.

**State Events**

Some events are emitted automatically when the OS changes the state of the application, therefore, they do not require any additional code to be written in the app in order to work out of the box:

***Launch***- emitted when the app starts

***First Launch***- emitted when the app starts for the first time

***First Launch After Update*** - emitted when the app starts for the first time after a version upgrade
 

###Pre-Defined Events


Pre-Defined Events are also helpful in measuring certain metrics within the apps and do not require further developer input to function. These particular events below are used to help measure app open events and track Sessions.

**Application Opened** - This event is very useful for push notifications and can measure when your app is opened after a message is sent.


**Session End**- As the name implies the Session End event is used to end a session and passes with it a Session Length attribute that calculates the session length in seconds.

**User-Defined Events**


User-Defined Events are not sent automatically so it is up to you if you want to use them or not. They can provide some great insights on popular user actions if you decide to track them.  In order to set this up the application code needs to include the emitter callbacks in order to emit these events.

Note: The last argument in all of the following emitter callbacks, otherInfo, is a hashmap with parameters or null

**Action**


A generic user action, such as a button click.

	long value = 12345;
	R1Emitter.getInstance().emitAction("action", "label", value, parameters);

**Login**


Tracks a user login within the app

	R1Emitter.getInstance().emitLogin( sha1("userId"), "userName", parameters); 


**Registration**


Records a user registration within the app

	UserItem userItem = new UserItem();
	userItem.userId = sha1("userId");
	userItem.userName = "userName";
	userItem.email = "user@email.net"
	userItem.streetAddress = "address";
	userItem.phone = "123456";
	userItem.zip = "111111";
	userItem.city = "City";
	userItem.state = "State"
	R1Emitter.getInstance().emitRegistration(userItem, parameters);


**Facebook Connect**


Allows access to Facebook services

	R1Emitter.getInstance().emitFBConnect( sha1( "userId" ), "userName", permissions, parameters);

where permissions is a List of R1SocialPermissions:

	ArrayList<R1SocialPermission> socialPermissions = new ArrayList<R1SocialPermission>();
	socialPermissions.add( new R1SocialPermission("permission", true));


**Twitter Connect**


Allows access to Twitter services

	R1Emitter.getInstance().emitTConnect( sha1( "userId" ),
	“username”, permissions, parameters);
				permissions:permissions
				otherInfo:@[“custom_key”:”value”}];


**Trial Upgrade**


Tracks an application upgrade from a trial version to a full version

	R1Emitter.getInstance().emitTrialUpgrade(parameters);


**Screen View**

Basically, a page view, it provides info about that screen

	R1Emitter.getInstance().emitAppScreen("title","description","http://	www.example.com/path”,”example.com”,”path”,parameters);


###E-Commerce Events


**Transaction**

	EmitItem purchaseItem = new EmitItem();
	purchaseItem.storeId = "storeId";
	purchaseItem.storeName = "name";
	purchaseItem.transactionId = "AE3237DAA"
	purchaseItem.cartId = "ABBCCD"
	purchaseItem.orderId = "ABCDEF";
	purchaseItem.totalSale = 3.2f;
	purchaseItem.currency = "EUR";
	purchaseItem.shippingCosts = 1.8f;
	purchaseItem.transactionTax = 2.5f;

	R1Emitter.getInstance().emitTransaction(emitItem, parameters);


**Transaction Item**

	R1EmitterLineItem lineItem = new R1EmitterLineItem();

	lineItem.productId = "productId";
	lineItem.productName = "productName";
	lineItem.quantity = 5;
	lineItem.unitOfMeasure = "parts";
	lineItem.msrPrice = 1.3f;
	lineItem.pricePaid = 3.4f;
	lineItem.currency = "EUR;
	lineItem.itemCategory = "items";
	R1Emitter.getInstance().emitTransactionItem("transactionItemId", lineItem, 	parameters) {

**Create Cart**

	R1Emitter.getInstance().emitCartCreate("cartId", parameters);

**Delete Cart**

	R1Emitter.getInstance().emitCartDelete("cartId", parameters); 


**Add To Cart**


	R1EmitterLineItem lineItem = new R1EmitterLineItem();
	lineItem.productId = "productId";
	lineItem.productName = "productName";
	lineItem.quantity = 5;
	lineItem.unitOfMeasure = "parts";
	lineItem.msrPrice = 1.3f;
	lineItem.pricePaid = 3.4f;
	lineItem.currency = "EUR;
	lineItem.itemCategory = "items";
	R1Emitter.getInstance().emitAddToCart("cartId", lineItem, parameters);

**Delete From Cart**


	R1EmitterLineItem lineItem = new R1EmitterLineItem();
	lineItem.productId = "productId";
	lineItem.productName = "productName";
	lineItem.quantity = 5;
	lineItem.unitOfMeasure = "parts";
	lineItem.msrPrice = 1.3f;
	lineItem.pricePaid = 3.4f;
	lineItem.currency = "EUR;
	lineItem.itemCategory = "items";
	R1Emitter.getInstance().emitRemoveFromCart("cartId", lineItem, parameters);

**Custom Events**

With custom events you have the ability to create and track specific events that are more closely aligned with your app. If planned and structured correctly, custom events can be strong indicators of user intent and behavior. Some examples include pressing the “like” button, playing a song, changing the screen mode from portrait to landscape, and zooming in or out of an image. These are all actions by the user that could be tracked with events.

To include tracking of custom events for the mobile app, the following callbacks need to be included in the application code.

	// Emits a custom event without parameters
	R1Emitter.getInstance().emitEvent("Your custom event name");
	// Emits a custom event with parameters
	private HashMap<String, Object> parameters = new HashMap<String, Object>();

	parameters.put("key","value");
	R1Emitter.getInstance().emitEvent("Your custom event name", parameters);
 



