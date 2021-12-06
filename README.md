# ExCamera

## English | [中文文档][Doc_Chinese.link]

Android SDK and Demo for products (MicroScope CAM, TeleScope CAM) designed by Convergence Ltd.

With ExCamera SDK, you are able to have an simple way to connect our products, and you can also develop more interesting and creative features by yourself.

## What we provide ?

First, we provide simple ways to connect our device, and encapsulates some popular functions, such as taking photos, taking videos, mirror flip, zoom, FPS calculation and so on.

Moreover, configurations (like focus, exposure, brightness, sharpness and so on) are controllable with our SDK，only if the hardware support them.

Finally, our demo will tell you how to use the SDK, almost all of the functions mentioned above are shown clearly (just having a look at [CamManager][CamManager.dir] will be enough).

Anyway, "***Talk is cheap, show me the code***".

## Usage

### 1. Setup

#### Ⅰ Import library moudle (Recommand)

- Step 1.

Add the following Gradle configuration to your Android project. In your root `build.gradle` file:

```
allprojects {
    repositories {
        maven {
            url 'https://gitee.com/wang_ziheng/libcommon/raw/master/repository/'
        }
    }
}
```

- Step 2.

Import the folder `libexcamera` as the module to your project.

***Note:*** To support **Android 10** devices using USB Connection, you need to keep your `targetSdkVersion` below `27` (27 is ok). For more information, you can see [UVCPermissionTest][UVCPermissionTest.link] and [this issue][UVCPermissionTest_Issue.link]

- Step 3.

Add the module `libexcamera` as a dependency to your app `build.gradle` file:

```
implementation project(path: ':libexcamera')
```

***Now, you are able to use ExCamera SDK in your project. :)***

------------

### 2. Connect and start preview

#### USB Connection

**USB Connection SDK** is developed based on the [saki4510t/UVCCamera][UVCCamera.link]. Before connecting to our device by USB, please make sure the OTG is open on your phone/pad device.

1. We need to register a broadcast to find OTG connection by `USBMonitor`, and we usually register on `onStart` and unregister on `onStop` in activity lifecycle.

2. After USB device is found by broadcast, we will request permission to exchange data with the USB device. It is only when we gain this permission that the USB device are actually connected.

3. After permission is granted, we try to open the UVC Camera and start preview.

4. Preview is need to be restored when you come back after leaving the preview page, so we start preview on `onResume` and stop preview on `onPause` in activity lifecycle.

5. When the USB device is disconnected or the activity is going to destroy, we need to stop preview and release the UVC Camera, so release on `onDestory` in the activity lifecycle is needed.

6. For more details, you can have a look at [UsbCameraCommand][UsbCameraCommand.java].

As mentioned above, the process of USB connection is complicated, but all you need to do is to initialize the [UsbCameraController][UsbCameraController.java] and bind it with [UsbCameraView][UsbCameraView.java]. Take the Demo codes in [UsbMicroCamManager][UsbMicroCamManager.java] as an example:

```
    private UsbCameraController usbCameraController;

    private void init() {
        usbCameraController = new UsbCameraController(context, usbCameraView);
        ...
    }

    public void onStart() {
        usbCameraController.registerUsb();
    }

    public void onResume() {
        usbCameraController.startPreview();
    }

    public void onPause() {
        usbCameraController.stopPreview();
    }

    public void onStop() {
        usbCameraController.unregisterUsb();
    }

    public void onDestroy() {
        usbCameraController.release();
    }
```

Well, you are able to preview on you own application by USB Connection now.

#### WiFi Connection

**WiFi Connection SDK** serves for our camera device that connected to **WiFi Box (V2 and above)** or **Planet**, and we develop the SDK with [Retrofit2][Retrofit.link] + [OkHttp3][OkHttp.link] + [RxAndroid][RxAndroid.link], so you need to **pay attention to dependencies conflicts**. Before connecting to our device by WiFi, please make sure your phone/pad device is connected to the wireless hotspot issued by WiFi Box, like `ScopeCAM_V2_XXXXXXXX`, `ScopeCAM_V3_XXXXXXXX` or `ScopeCAM_Planet_XXXXXXXX`

**ps.** The IP address to control the WiFi Box is `http://192.168.8.10:8080/`, and you can find the network API from [ApiService][ApiService.java]

1. First, we request the `?action=stream` API to get InputStream by network, from which we can decode and generate frame bitmap. The InputStream should be single instance, or it may cause large memory footprint or OOM. Just request the stream on `onStart` and release it on `onStop` in activity lifecycle.

2. After load stream success, the next step is to request `input.json` for wifi camera configurations. It is important because we need them to update its configuration too.

3. Preview is need to be restored when you come back after leaving the preview page, so we start preview on `onResume` and stop preview on `onPause` in activity lifecycle.

4. Sometimes, we may lost the wireless connection or meet other troubles, so it is necessary to have a retry mechanism. Well, we already provided the retry mechanism for you don't have to consider about it.

5. Similarly, please release on `onDestroy` in the activity lifecycle.

6. For more details, you can have a look at [WifiCameraCommand][WifiCameraCommand.java].

Similarly, we also provide simple way for you to complete the complicated process, just initialize the [WifiCameraController][WifiCameraController.java] and bind it with [WifiCameraView][WifiCameraView.java]. Take the Demo codes in [WifiMicroCamManager][WifiMicroCamManager.java] as an example:

```
    private WifiCameraController wifiCameraController;

    private void init() {
        wifiCameraController = new WifiCameraController(context, wifiCameraView);
        ...
    }

    public void onStart() {
        wifiCameraController.startStream();
    }

    public void onResume() {
        wifiCameraController.startPreview();
    }

    public void onPause() {
        wifiCameraController.stopPreview();
    }

    public void onStop() {
        wifiCameraController.stopStream();
    }

    public void onDestroy() {
        wifiCameraController.release();
    }
```

Well, you are able to preview on you own application by WiFi Connection now.

------------

### 3. Basic action

|Action|USB Connection|WiFi Connection|
| :------------: | :------------: | :------------: |
|**Update Resolution**|UsbCameraController.updateResolution(int width, int height)|WifiCameraController.updateResolution(int width, int height)|
|**Flip Horizontal**|UsbCameraSP.setIsFlipHorizontal(boolean value)|WifiCameraSP.setIsFlipHorizontal(boolean value)|
|**Flip Vertical**|UsbCameraSP.setIsFlipVertical(boolean value)|WifiCameraSP.setIsFlipVertical(boolean value)|
|**Take Photo(Default Path)**|UsbCameraController.takePhoto()|WifiCameraController.takePhoto()|
|**Take Photo(Custom Path)**|UsbCameraController.takePhoto(String path)|WifiCameraController.takePhoto(String path)|
|**Start Record(Default Path)**|UsbCameraController.startRecord()|WifiCameraController.startRecord()|
|**Start Record(Custom Path)**|UsbCameraController.startRecord(String path)|WifiCameraController.startRecord(String path)|
|**Stop Record**|UsbCameraController.stopRecord()|WifiCameraController.stopRecord()|
|**Start TimeLapse Record(Default Path)**|UsbCameraController.startTLRecord()|WifiCameraController.startTLRecord()|
|**Start TimeLapse Record(Custom Path)**|UsbCameraController.startTLRecord(String path)|WifiCameraController.startTLRecord(String path)|
|**Stop TimeLapse Record**|UsbCameraController.stopTLRecord()|WifiCameraController.stopTLRecord()|
|**Start Taking Denoised Photo(Default Path)**|UsbCameraController.startStackAvg()|WifiCameraController.startStackAvg()|
|**Start Taking Denoised Photo(Custom Path)**|UsbCameraController.startStackAvg(String path)|WifiCameraController.startStackAvg(String path)|
|**Cancel Take Denoised Photo**|UsbCameraController.cancelStackAvg()|WifiCameraController.cancelStackAvg()|
|**Start Auto Focus(Telescope CAM)**|UsbCameraController.startTeleAF()|WifiCameraController.startTeleAF()|
|**Stop Auto Focus(Telescope CAM)**|UsbCameraController.stopTeleAF()|WifiCameraController.stopTeleAF()|

------------

### 4. Configuration

#### USB Connection

For USB Connection, all configurations are saved as [UVCConfig][UVCConfig.java] from [UVCCamera][UVCCamera.java], and are divided into two types: **Auto** and **Param**.

##### Auto Config

|Configuration|TAG(UVCConfig)|TAG(UVCCamera)|MicroScope CAM|TeleScope CAM|
| :------------: | :------------: | :------------: | :------------: | :------------: |
|**FocusAuto**|TAG_AUTO_FOCUS_AUTO|CTRL_FOCUS_AUTO|✔|✔|
|Privacy|TAG_AUTO_PRIVACY|CTRL_PRIVACY|✖|✖|
|**WhiteBalanceAuto**|TAG_AUTO_WHITE_BALANCE_AUTO|PU_WB_TEMP_AUTO|✔|✔|
|WhiteBalanceComponentAuto|TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO|PU_WB_COMPO_AUTO|✖|✖|

##### Param Config

|Configuration|TAG(UVCConfig)|TAG(UVCCamera)|MicroScope CAM|TeleScope CAM|
| :------------: | :------------: | :------------: | :------------: | :------------: |
|ScanningMode|TAG_PARAM_SCANNING_MODE|CTRL_SCANNING|✖|✖|
|**ExposureMode**|TAG_PARAM_EXPOSURE_MODE|CTRL_AE|✔|✔|
|ExposurePriority|TAG_PARAM_EXPOSURE_PRIORITY|CTRL_AE_PRIORITY|✖|✖|
|**Exposure**|TAG_PARAM_EXPOSURE|CTRL_AE_ABS|✔|✔|
|**Focus**|TAG_PARAM_FOCUS|CTRL_FOCUS_ABS|✔|✔|
|FocusRel|TAG_PARAM_FOCUS_REL|CTRL_FOCUS_REL|✖|✖|
|Iris|TAG_PARAM_IRIS|CTRL_IRIS_ABS|✖|✖|
|IrisRel|TAG_PARAM_IRIS_REL|CTRL_IRIS_REL|✖|✖|
|**Zoom**|TAG_PARAM_ZOOM|CTRL_ZOOM_ABS|✖|✖|
|ZoomRel|TAG_PARAM_ZOOM_REL|CTRL_ZOOM_REL|✖|✖|
|Pan|TAG_PARAM_PAN|CTRL_PANTILT_ABS|✖|✖|
|PanRel|TAG_PARAM_PAN_REL|CTRL_PANTILT_REL|✖|✖|
|Tilt|TAG_PARAM_TILT|CTRL_PANTILT_ABS|✖|✖|
|TiltRel|TAG_PARAM_TILT_REL|CTRL_PANTILT_REL|✖|✖|
|**Roll**|TAG_PARAM_ROLL|CTRL_ROLL_ABS|✖|✖|
|RollRel|TAG_PARAM_ROLL_REL|CTRL_ROLL_REL|✖|✖|
|**Brightness**|TAG_PARAM_BRIGHTNESS|PU_BRIGHTNESS|✔|✔|
|**Contrast**|TAG_PARAM_CONTRAST|PU_CONTRAST|✔|✔|
|**Hue**|TAG_PARAM_HUE|PU_HUE|✔|✔|
|**Saturation**|TAG_PARAM_SATURATION|PU_SATURATION|✔|✔|
|**Sharpness**|TAG_PARAM_SHARPNESS|PU_SHARPNESS|✔|✔|
|**Gamma**|TAG_PARAM_GAMMA|PU_GAMMA|✔|✔|
|**Gain**|TAG_PARAM_GAIN|PU_GAIN|✖|✔|
|**WhiteBalance**|TAG_PARAM_WHITE_BALANCE|PU_WB_TEMP|✔|✔|
|WhiteBalanceComponent|TAG_PARAM_WHITE_BALANCE_COMPONENT|PU_WB_COMPO|✖|✖|
|BacklightCompensation|TAG_PARAM_BACKLIGHT_COMPENSATION|PU_BACKLIGHT|✔|✔|
|PowerLineFrequency|TAG_PARAM_POWER_LINE_FREQUENCY|PU_POWER_LF|✔|✔|
|DigitalMultiplier|TAG_PARAM_DIGITAL_MULTIPLIER|PU_DIGITAL_MULT|✖|✖|
|DigitalMultiplierLimit|TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT|PU_DIGITAL_LIMIT|✖|✖|
|AnalogVideoStandard|TAG_PARAM_ANALOG_VIDEO_STANDARD|PU_AVIDEO_STD|✖|✖|
|AnalogVideoLockStatus|TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS|PU_AVIDEO_LOCK|✖|✖|

##### Usage

Use methods in `UsbCameraController`, and tag from `UVCConfig`:

- **public UVCAutoConfig getAutoConfig(String tag)**

	Get the auto config entity with the tag

- **public UVCParamConfig getParamConfig(String tag)**

	Get the param config entity with the tag

- **public boolean checkConfigEnable(String tag)**

	Check is config with the tag available

- **public boolean getAuto(String tag)**

	get is auto config with tag auto now

- **public void setAuto(String tag, boolean value)**

	set auto config with tag to be auto or not

- **public void resetAuto(String tag)**

	reset auto config with tag

- **public int getParam(String tag)**

	get value of param config with tag

- **public void setParam(String tag, int param)**

	set value of param config with tag

- **public void resetParam(String tag)**

	reset param config with tag

- **public void startTeleFocus(boolean isBack)**

	start adjusting the telescope CAM focus and it is usually called when button is pressed down

- **public void stopTeleFocus(boolean isBack)**

	stop adjusting the telescope CAM focus and it is usually called when button is pressed up or cancel

**Note:**

For `Exposure` of **MicroScope CAM**, it will be **Auto** when `ExposureMode = 8` and be **Manual** when `ExposureMode = 1`

#### WiFi Connection

Similarly, for WiFi Connection, all configurations are saved as [WifiConfig][WifiConfig.java] from result json, and are divided into two types: **Auto** and **Param**.

##### Auto Config

|Configuration|TAG(WifiConfig)|ID|MicroScope CAM|TeleScope CAM|
| :------------: | :------------: | :------------: |:------------: |:------------: |
|**FocusAuto**|TAG_AUTO_FOCUS_AUTO|10094860|✔|✔|
|**WhiteBalanceAuto**|TAG_AUTO_WHITE_BALANCE_AUTO|9963788|✔|✔|
|**ExposureAuto**|TAG_AUTO_EXPOSURE_AUTO|10094849|✔|✔|

##### Param Config

|Configuration|TAG(WifiConfig)|ID|MicroScope CAM|TeleScope CAM|
| :------------: | :------------: | :------------: |:------------: |:------------: |
|**Focus**|TAG_PARAM_FOCUS|10094858|✔|✔|
|**WhiteBalance**|TAG_PARAM_WHITE_BALANCE|9963802|✔|✔|
|**Exposure**|TAG_PARAM_EXPOSURE|10094850|✔|✔|
|**Brightness**|TAG_PARAM_BRIGHTNESS|9963776|✔|✔|
|**Contrast**|TAG_PARAM_CONTRAST|9963777|✔|✔|
|**Saturation**|TAG_PARAM_SATURATION|9963778|✔|✔|
|**Hue**|TAG_PARAM_HUE|9963779|✔|✔|
|**Gamma**|TAG_PARAM_GAMMA|9963792|✔|✔|
|**Gain**|TAG_PARAM_GAIN|9963795|✖|✔|
|**Sharpness**|TAG_PARAM_SHARPNESS|9963803|✔|✖|
|BacklightCompensation|TAG_PARAM_BACKLIGHT_COMPENSATION|9963804|✔|✔|
|PowerLineFrequency|TAG_PARAM_POWER_LINE_Frequency|9963800|✔|✔|
|JpegQuality|TAG_PARAM_JPEG_QUALITY|1|✖|✖|

##### Usage

Use methods in `WifiCameraController`, and tag from `WifiConfig`:

- **public WifiAutoConfig getAutoConfig(String tag)**

	Get the auto config entity with the tag

- **public WifiParamConfig getParamConfig(String tag)**

	Get the param config entity with the tag

- **public boolean resetConfig(String tag)**

	reset the config with the tag

- **public boolean getAuto(String tag)**

	get is auto config with tag auto now

- **public void setAuto(String tag, boolean value)**

	set auto config with tag to be auto or not

- **public int getParam(String tag)**

	get value of param config with tag

- **public void setParam(String tag, int param)**

	set value of param config with tag

- **public void startTeleFocus(boolean isBack)**

	start adjusting the telescope CAM focus and it is usually called when button is pressed down

- **public void stopTeleFocus(boolean isBack)**

	stop adjusting the telescope CAM focus and it is usually called when button is pressed up or cancel

**Note:**

For `Focus` of **MicroScope CAM**, it will be **Auto** when `FocusAuto = 1` and be **Manual** when `FocusAuto = 0`

For `WhiteBalance` of **MicroScope CAM**, it will be **Auto** when `WhiteBalanceAuto = 1` and be **Manual** when `WhiteBalanceAuto = 0`

For `Exposure` of **MicroScope CAM**, it will be **Auto** when `ExposureAuto = 3` and be **Manual** when `ExposureAuto = 1`

------------

### 5. Callback

#### USB Connection

`UsbCameraController.OnControlListener`

- **void onUsbConnect()**

	It will be called when the USB device is connected and permission is granted

- **void onUsbDisConnect()**

	It will be called when the USB device is disconnected

- **void onCameraOpen()**

	It will be called when the UVC Camera is opened

- **void onCameraClose()**

	It will be called when the UVC Camera is closed

- **void onPreviewStart()**

	It will be called when the preview start

- **void onPreviewStop()**

	It will be called when the preview stop

- **void onUsbStateUpdate(UsbCameraState state)**

	It will be called when [UsbCameraState][UsbCameraState.java] update

- **void onActionStateUpdate(ActionState state)**

	It will be called when [ActionState][ActionState.java] update

- **void onLoadFrame(Bitmap bitmap)**

	You can get every frame from this callback

- **void onLoadFPS(int instantFPS, float averageFPS)**

	You can get current FPS from this callback

#### WiFi Connection

`WifiCameraController.OnControlListener`

- **void onStreamStart(boolean isRetry)**

	It will be call when the stream start and preview correctly, and isRetry will be ture if restored from Retrying state

- **void onStreamStop(boolean isRetry)**

	It will be call when the stream stop, and isRetry will be ture if current state become Retrying

- **void onWifiStateUpdate(WifiCameraState state)**

	It will be called when [WifiCameraState][WifiCameraState.java] update

- **void onActionStateUpdate(ActionState state)**

	It will be called when [ActionState][ActionState.java] update

- **void onParamUpdate(WifiCameraParam param, boolean isReset)**

	It will be called when configuration is loaded success from network, and isReset will be true if the stream is just started

- **void onLoadFrame(Bitmap bitmap)**

	You can get every frame from this callback

- **void onLoadFPS(int instantFPS, float averageFPS)**

	You can get every frame from this callback

------------

### 6. Planet Rotatement Control

Planet rotatement is controlled through the following interface:

Communication protocol:HTTP

Request URL:http://192.168.8.10:8092 

Request Method:GET

Request Parameter:
|Parameter|Explanation|Value Type|Value|
| :------------: | :------------ | :------------: |:------------ |
|**id**|Motor id|int|0-Spin<br>1-Pitch|
|**controlType**|Control type|int|0-Stop<br>1-Clockwise<br>2-Counterclockwise<br>3-Reset the position|
|**time**|Time|int|>=0 unit(milliseconds)|
|**mode**|Motor rotate mode|int|0-Specified speed<br>1-Specified time and speed|
|**speed**|Motor speed, not the angular velocity①|int|>=400<br><=2000|
|**subDivision**|Motor subdivision|int|2<br>4<br>8<br>16|
|**returnTrip**|Whether to eliminate the return error|int|0-Not eliminate the return error<br>1-Eliminate the return error|
|**returnTripTime**|Eliminate return error time|int|>=0 unit(milliseconds)|

①The rotate angular velocity of the motor is determined by two parameter: “speed” and “subDivision”. The theoretical angular velocity of motor 0 is equal to 3,662.109375/(speed\*subDivision), and the theoretical angular velocity of motor 1 is equal to 1,831.0546875/(speed\*subDivision).

Return Result:
|Parameter|Explanation|Value Type|Value|
| :------------: | :------------ | :------------: |:------------ |
|**id**|Motor id|int|0-Spin<br>1-Pitch|
location**|The location of the motor|double|Motor 0:0-360<br>Motor 1:30-150|
|**posLimit**|Motor position limit|int|0-Not in the positivie limit<br>1-In the positive limit|
|**negLimit**|Motor negetive limit|int|0-Not in the negetive limit<br>1-In the negetive limit|
|**resetFlag**|Is the motor in the reset position|int|0-In the reset position<br>1-Not in the reset position|
|**result**|Return result|int|1000-ok<br>1001-The motor isn't initialized<br>1002-The motor is running<br>1003-The motor has already stopped<br>1004-The motor is resetting the position|

Usage example [PlanetCommand][PlanetCommand.java]。

------------

### 7. Default Setting

Default setting for USB Connection : [UsbCameraConstant][UsbCameraConstant.java]

Default setting for WiFi Connection : [WifiCameraConstant][WifiCameraConstant.java]

Default output path : [OutputUtil][OutputUtil.java]

------------

## Thanks

[UVCCamera][UVCCamera.link] , [AndroidUSBCamera][AndroidUSBCamera.link] , [
mjpg-streamer][mjpg-streamer.link]

Dependencies used in ExCamera SDK and Demo :

[Retrofit][Retrofit.link] , [OkHttp][OkHttp.link] , [RxAndroid][RxAndroid.link] , [ButterKnife][ButterKnife.link] , [PermissionX][PermissionX.link] , [ImmersionBar][ImmersionBar.link]

## Contact us

**Email :**

software@cvgc.cn

**Official Website :**

[www.cvgc.cn][cvgc.link]

[www.tipscope.com][tipscope.link]

[www.tinyscope.com][tinyscope.link]

## License

```
Copyright © 2020 Convergence Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[Doc_English.link]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/README.md
[Doc_Chinese.link]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/README_CN.md

[CamManager.dir]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/app/src/main/java/com/convergence/excamera/manager
[ApiService.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/net/ApiService.java

[UsbCameraCommand.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/usb/core/UsbCameraCommand.java
[UsbCameraController.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/usb/core/UsbCameraController.java
[UsbCameraView.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/usb/core/UsbCameraView.java
[UsbMicroCamManager.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/app/src/main/java/com/convergence/excamera/manager/UsbMicroCamManager.java
[UVCConfig.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/serenegiant/usb/config/base/UVCConfig.java
[UVCCamera.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/serenegiant/usb/UVCCamera.java

[WifiCameraCommand.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/core/WifiCameraCommand.java
[WifiCameraController.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/core/WifiCameraController.java
[WifiCameraView.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/core/WifiCameraView.java
[WifiMicroCamManager.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/app/src/main/java/com/convergence/excamera/manager/WifiMicroCamManager.java
[WifiConfig.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/config/base/WifiConfig.java

[UsbCameraState.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/usb/UsbCameraState.java
[WifiCameraState.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/WifiCameraState.java
[ActionState.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/common/ActionState.java

[PlanetCommand.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/planet/core/PlanetCommand.java

[UsbCameraConstant.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/usb/UsbCameraConstant.java
[WifiCameraConstant.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/wifi/WifiCameraConstant.java
[OutputUtil.java]: https://github.com/ConvergenceSoftware/ExCamera/blob/master/libexcamera/src/main/java/com/convergence/excamera/sdk/common/OutputUtil.java

[cvgc.link]: http://www.cvgc.cn
[tipscope.link]: http://www.tipscope.com
[tinyscope.link]: https://www.tinyscope.com

[UVCCamera.link]: https://github.com/saki4510t/UVCCamera
[UVCPermissionTest.link]: https://github.com/saki4510t/UVCPermissionTest
[UVCPermissionTest_Issue.link]: https://github.com/saki4510t/UVCPermissionTest/issues/1
[AndroidUSBCamera.link]: https://github.com/jiangdongguo/AndroidUSBCamera
[mjpg-streamer.link]: https://github.com/jacksonliam/mjpg-streamer

[Retrofit.link]: https://github.com/square/retrofit
[OkHttp.link]: https://github.com/square/okhttp
[RxAndroid.link]: https://github.com/ReactiveX/RxAndroid
[ButterKnife.link]: https://github.com/JakeWharton/butterknife
[PermissionX.link]: https://github.com/guolindev/PermissionX
[ImmersionBar.link]: https://github.com/gyf-dev/ImmersionBar