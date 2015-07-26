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
