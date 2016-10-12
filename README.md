# Tempo Utility App SDK

Welcome to Blue Maestro's Android SDK for the third generation of Tempo Disc™ Bluetooth Low Energy sensors and data loggers. Visit www.bluemaestro.com for the full range of Tempo Disc™ products. 

This SDK allows users to send commands to the device and graph data, if possible for the device.

### Classes

`MainActivity.java` - This contains the main functionality of the app. It provides implementation for buttons and views, connects and disconnects from devices, and contains the code for sending commands.

`DeviceListActivity.java` - This activity allows advertising devices to be displayed in a list whilst scanning.

`UartService.java` - This service allows the app to make UART connections to devices

`StyleOverride.java` - This class overrides certain styles, such as text font

#### BLE

`ScanRecordParser.java` - This parses Blue Maestro data into manufacturer data and scan response data

`Utility.java` - This class holds utility functions, such as converting Celsius to Fahrenheit and displaying bytes as hex

#### Devices

`BMDevice.java` - An abstract class to inherit for Blue Maestro devices

`BMDeviceMap.java` - This holds a map of IDs to custom device classes, and allows creation of these custom devices

`BMDefaultDevice.java` - A class which discovered Blue Maestro devices will default to if a custom class has not been defined for it

`BMTempoDiscT.java` - A device class for the Tempo Disc T

`BMTempHumi.java` - A device class for the Temp Humi

`BMShockLog.java` - A device class for the ShockLog

#### Views

`BMAlertDialog.java` - A simple wrapper for a Blue Maestro alert dialog

`BMButton.java` - A simple wrapper for a Blue Maestro button

`BMEditText.java` - A simple wrapper for a Blue Maestro editable textbox

`BMRelativeLayout.java` - A simple wrapper for a Blue Maestro relative layout

`BMTextView.java` - A simple wrapper for a Blue Maestro textview

`BMLineChart.java` - A simple wrapper for a Blue Maestro line chart

`LimitZone.java` - This class allows defining of limit 'zones', as opposed to limit lines, for a line chart


### Compile

The project can be compiled using Android Studio and Gradle

### Information

Version: 2.0.1

Android: Version 4.3 or above


DISCLAIMER

While this SDK has been tested and verified as working with Tempo Disc™ Bluetooth Low Energy products at the time of submission to this repository, Blue Maestro may not keep this SDK up to date nor does it accept any obligation to do so. Blue Maestro does not guarantee or warrant this SDK is fit for any intended purpose and accepts no liability as a result it is not.
