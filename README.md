# teliver-fooddelivery-driver-android
Food Delivery Driver App.

Teliver
    Teliver is a Realtime Location Tracking Android and IOS Sdk. The Teliver Sdk can be easily Integerated in a application  with few steps. The SDK supports tracking of a operator with a marker in map, that updates for every location change of the opertor. The operator can also send push notification to the consumer regarding the traffic updates and the time taken for delivery.  
         
Steps to Integrate Teliver:

1.Open your build.gradle file of Module:app.
Add compile 'com.teliver.sdk:TeliverSdk:1.0.18'as dependency.

2.Obtain the map key from Google maps.

Open your AndroidManifest.xml file and paste the following code under application tag after embedding your map key obtained from Google.

<meta-data
android:name="com.google.android.geo.API_KEY"
android:value="API_KEY_FOR_MAP"/>


Steps to initialize Teliver:

1.Fire up the SDK in application by the adding the following code snippet in your Application class
      Teliver.init(this,"TELIVER_KEY"); 
      
Note: Obtain the Teliver key from the dashboard, Use TLog.setVisible(true);to enable logging for development.


2.Now to Transmit a location update from a driver application.  

Teliver.startTrip(new TripBuilder("Tracking_Id").build());        

Note: The Tracking_Id here is your unique identifier for the trip; basically it’s just the order id or driver id in your system


3.To Track the Transmitter location in a separate Application
      Teliver.startTracking(new TrackingBuilder(new MarkerOption("Tracking_Id")).build());


         
