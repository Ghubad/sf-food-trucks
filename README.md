# San Francisco Food Trucks App

Mobile food facility has become an increasingly significant feature of San Francisco. If you are a foodie who likes to explore street food, then wait no more, and start exploring !

This project is an Android app, for you to explore Mobile Food Trucks around you when in San Francisco. Locate trucks on your android phone and get the truck details including name of vendor, location, type of food sold. Data is provided by [data.sfgov.org](https://data.sfgov.org/). You can access this dataset via [the Socrata Open Data API](http://dev.socrata.com/).

## Screenshots
<img src="http://i.imgur.com/7mFoJL8m.png">          <img src="http://i.imgur.com/chr8LtPm.png">

## Features

##### With the app, you can:
- View the the food trucks on map around you or in any bylanes of San Francisco.
- Click on the marker to know more details: name of vendor, location.

######Features under development
- Locate a specific truck by using a text search. It will also assist you in autocompleting the name of the truck, for you!  

###### Application requirements:
This application requires a minimum sdk version of 15. By targeting API 15 and later, the application will run on approximately 94.1% of the devices that are active on the Google Play Store; IceCreamSandwich and later releases will be able to run it. The data was collected during a 7-day period ending on June 1, 2015 - [source](http://developer.android.com/intl/zh-CN/about/dashboards/index.html).

## Technologies used:
#### Build Tools:
|Name|Version|Description|
|---|---|---|
| [Gradle](http://gradle.org/docs/current/release-notes) | 2.4 | Gradle build system |
| [Android Gradle Build Tools](http://tools.android.com/tech-docs/new-build-system) | 1.2.3 | Official Gradle Plugin |
| [Android SDK](http://developer.android.com/tools/revisions/platforms.html#5.1) | 22 | Official SDK |
| [Android SDK Build Tools](http://developer.android.com/tools/revisions/build-tools.html) | 22.0.1 | Official Build Tools |
| [Android Studio](http://tools.android.com/recent) | 1.2.2 | Official IDE |

####Android Libraries:
|Name|Version|Description|
|---|---|---|
| [Android AppCompat-v7](http://developer.android.com/tools/support-library/features.html#v7-appcompat) | 22.2.0 | Support Library API 7+|
| [Android Google Play Services](https://developer.android.com/google/play-services/index.html) | 7.3.0 | Google Maps Android API v2 |
| [Volley](http://developer.android.com/training/volley/index.html) | 1.0.17 | HTTP library |

I have used [mcxiaoke/android-volley](https://github.com/mcxiaoke/android-volley). This is an unofficial mirror for android volley library, the source code will synchronizes periodically with the official volley repository. Did this to avoid, downloading official library source code and adding it as android library project into my workspace in order to use it -- Dependency management. 


####Testing Frameworks:
|Name|Version|Description|
|---|---|---|
| [JUnit](https://github.com/junit-team/junit) | 4.12 | Java Unit Testing Framework |
| [Mockito](https://github.com/mockito/mockito) | 1.10.19 | Mocking Framework |

####Crash Reporting Plugins:
|Name|Version|Description|
|---|---|---|
| [Fabric](https://dev.twitter.com/crashlytics/android) | 1.+ | Crashlytics Kit for Android |

####Continuous Integration:
|Name|Description|Status|
|---|---|---|
| [TravisCI](http://docs.travis-ci.com/user/languages/android/) | Build Server(Builds, Tests, Publishes reports) | In Progress |

###Detailed Description of the project

The code in the above repository leverages the functionality provided, primarily, by Android SDK and Google Maps Android API v2. Volley has been used to address networking requirements of the application. Volley is being used by Google Apps as their primary HTTP library. Initially, it was was used for Play Store before making it open source in 2013. Volley solves many of the problems related to networking.

Volley offers the following benefits, to name a few:
- Automatic scheduling of network requests.
- Multiple concurrent network connections.
- Transparent disk and memory response caching with standard HTTP cache coherence.
- Support for request prioritization.
- Cancellation request API. You can cancel a single request, or you can set blocks or scopes of requests to cancel.
- Ease of customization, for example, for retry and backoff.
- Strong ordering that makes it easy to correctly populate your UI with data fetched asynchronously from the network.
- Debugging and tracing tools.

For more information check out
[http://developer.android.com/intl/zh-cn/training/volley/index.html](http://developer.android.com/intl/zh-cn/training/volley/index.html)

I have used AppCompat support library for backward compatibility, to support material design in devices pre dating lollipop. Other technologies used are:
- Crashlytics for crash reporting.
- Junit and Mockito for unit testing.

To begin with, I am using AppController which is an application class, as a singleton, to create and hold volley request queue. There must be only one request queue. All the requests are then added to this queue. Volley makes the http call and returns the response in callback methods.

We need to check if the Play Services are available, for Maps to work. To do this, I implemented OnMapReadyCallback. Doing this makes SDK to take care of checking availability of Play services and asking user to install it, if otherwise. Finally when all the prerequisites are fulfilled, reference to map object is available in it’s callback method. Once we get this reference, we are ready to use Map Fragment view provided by APIs.

Next up, I initialize the google api client, once connected with this service, we can leverage the functionalities provided by Google. We get notified of the connection through a call back method. From here on, I go on to locate user to pinpoint him on Map and also fetch food trucks data by hitting SOCRATA apis.

To locate user, FusedLocationApi is being used. APIs getLastLocation method provides us with the location information. This info contains latitude and longitude information. Using this information, I zoom into the map to user’s location.

I have used a convenience button at the top of the Map view to take user to directly to San Francisco. This is for testing purpose. To do this, I have used Geocoder, getFromLocationName, return the location information similar to FusedLocationApi. This service can be used to fetch location information either by using name or pincode.

Google Maps view also provide user to go to his location. This implementation is completely provided by SDK. We don’t have any code to use this feature.

Finally, I used the Json response to store data (for now just storing it in memory, will implement local db soon) and add markers to the Maps which holds the information about the truck. Markers show the Name of the food truck and food it has to offer. 

Going forward, 
- I plan to integrate this project with Travis CI. 
- Provide a more detailed information view.
- Add database, since I realised this data is not realtime and often less frequently updated.
- Test edges cases pertaining to UI using Instrumention and Expresso framework provded by Android.
- Add contextual error messages by imeplenting custom exceptions to the project. 
- Add a search box with autocomplete feature to assist user to find a particular truck.
- Add a truck to favorites list.
- Leverage Geofencing to let user know if they are around their favorite truck.
- Share a truck info with with friends.

To end,this I take a lot of pride in uploading this project to public reposistory and in creating this read me file. All the information and coding help required to develop this project has come from a number of onine blogs and android api documentations. 

Due to time constraints, I was not to able to write many test cases. However I have written test cases for validating the data required for showing marker. I will be adding more test cases sooner. Along with above mentioned tasks.

You can download APK from [here](https://drive.google.com/file/d/0B9ErXsnOdkPAc1J3X1J2MmJWenM/view?usp=sharing)

License
=========

    Copyright (C) 2015 San Francisco Food Trucks by Suhas Mandrawadkar
   
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
