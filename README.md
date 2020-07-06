# Sync Monkey Android App

[![Build Status](https://travis-ci.com/chesapeaketechnology/syncmonkey.svg?branch=develop)](https://travis-ci.com/github/chesapeaketechnology/syncmonkey)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat)](https://github.com/chesapeaketechnology/syncmonkey/blob/master/LICENSE)

The Sync Monkey Android App enables files on the Android device to be synced with a Microsoft Azure Blob storage.

![App Screenshot](screenshots/Main_Screenshot_0.0.1.png "The Sync Monkey App Main Screen")

## Getting Started

To build and install the project follow the steps below:

    1) Clone the repo.
    2) Open Android Studio, and then open the root directory of the cloned repo.
    3) Connect an Android Phone (make sure debugging is enabled on the device).
    4) Install and run the app by clicking the "Play" button in Android Studio.

### Prerequisites

Install Android Studio to work on this code.

## Google Play Listing

[The Google Play Listing for this app](https://play.google.com/store/apps/details?id=com.chesapeaketechnology.syncmonkey)

## Changelog

##### [0.1.4](https://github.com/chesapeaketechnology/syncmonkey/releases/tag/v0.1.4) - 2020-07-05
 * Fixed a bug where a few of the MDM configured values were not being shown in the Settings UI.

##### [0.1.3](https://github.com/chesapeaketechnology/syncmonkey/releases/tag/v0.1.3) - 2020-07-02
 * The settings now reflect if the app is configured via MDM.

##### [0.1.2](https://github.com/chesapeaketechnology/syncmonkey/releases/tag/v0.1.2) - 2020-06-04
 * The SAS URL expiration count down is now displayed at the bottom of the screen.

##### [0.1.1](https://github.com/chesapeaketechnology/syncmonkey/releases/tag/v0.1.1) - 2020-04-21
 * Added support for other apps to kick off a sync.
 * The Android Advertisement ID is written to a txt file and synced to the Azure Blob store.

##### [0.1.0](https://github.com/chesapeaketechnology/syncmonkey/releases/tag/v0.1.0) - 2020-03-30
 * Added support for sending shared content to Sync Monkey without presenting the share UI.

## Authors

* **Christian Rowlands** - *Initial work* - [christianrowlands](https://github.com/christianrowlands)
