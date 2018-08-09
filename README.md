# MLKitBarcodeAndroid
Android barcode scanning app using [Google ML Kit](https://developers.google.com/ml-kit/)



## Key Features

* Show camera preview of facing back camera
* Detect ISBN barcode (EAN-13 format)
* Display detected region and barcode value on preview screen



## Requirements

* Android version 6.0 or higher
* Google PlayStore is available on the device
* Support facing back camera



## Set-up

* Change package name to your own naming
  * change package name in ```AndroidManifext.xml```
  * change package name in ```build.gradle``` for App level
  * change directory structure and package name of java source files
* Enroll the package to firebase console
  * Log in [Firebase Console](https://console.firebase.google.com/?hl=ko)
  * Add a project for this app
  * Go to project preference
  * Enroll application package name you named before
  * Download ```google-services.json``` file and copy into ```app``` directory
* Build and Run
  * Build and install to device
  * Application is launched, allow permissions



## Further more

* This project use ```android.hardware.camera``` APIs and it is deprecated now.
  If you want to enhance and to extend camera features, use ```android.hardware.camera2```.
* The preview screen is locked up portrait mode.
  All of orientation modes will be supported if considering preview image measure and camera resources management.

