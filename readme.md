# QR Code GIF Reader

This project is an Android application that reads QR codes from a specific GIF file. This GIF file contains several frames, each of which is a QR code. The application processes the data embedded in these QR codes. The application is written in Kotlin and uses the ZXing library for QR code scanning.

You can use this [Java application](https://github.com/deletefromuser/qrcode-gif) to generate the QR codes GIF file.

## Key Components

- `MainActivity`: This is the entry point of the application. It sets up the main UI and starts the ScanActivity when the scan button is clicked.

- `ScanActivity`: This activity handles the QR code scanning process. It uses the device's camera to capture frames and processes them to extract QR code data.

- `AndroidManifest.xml`: This file declares the application's components and required permissions. The application requires camera and write external storage permissions.

- `build.gradle`: This file contains the build configuration for the application. The ZXing library is included as a dependency here.

- `gradle.properties`: This file contains project-wide Gradle settings.

- `settings.gradle`: This file includes the repositories for the project and the root project name.

## Data Structure

The QR code data is embedded in a JSON structure as follows:
```json
{
    "name": "input.txt",
    "count": "2",
    "index": "0",
    "data": "dGVzZXRzZXJ2ZXI6"
}
```

- `name`: The name of the file.
- `count`: The total number of QR codes that make up the complete data.
- `index`: The index of the current QR code in the sequence, start from 0.
- `data`: The data contained in the current QR code. This is a Base64 encoded string representing a portion of the file's bytes.

## How to Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Run the application on an emulator or a connected Android device.

## Note

The application is currently in development and some features may not be fully implemented. The QR code scanning process is initiated but the handling of the scanned data is commented out in the current version of the code.

The application uses the ZXing library for QR code scanning. The library is included as a dependency in the `build.gradle` file.

The application requires camera and write external storage permissions, which are declared in the `AndroidManifest.xml` file.

The application's UI is defined in XML layout files. The `activity_main.xml` file defines the main UI, which includes a button to start the scan activity. The `scan_activity.xml` file defines the UI for the scan activity, which includes a SurfaceView for the camera preview.
