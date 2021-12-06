# ExCamera

## [English][Doc_English.link] | 中文文档

Android SDK 和 Demo，适用于肯维捷斯（武汉）科技有限公司出品的产品（显微相机、望远相机）。

通过ExCamera SDK，你可以方便快捷地连接和控制我们的产品，另外你也可以在这基础上自由发挥，开发更多好玩有趣的功能。

## 我们提供什么？

首先，我们提供简单的方式来连接我们的设备，并且封装了一些普遍实用的功能，如拍照、录像、镜像翻转、缩放、FPS计算等等。

然后，一些设备图像参数（如对焦、曝光、亮度、锐度等）可以通过我们的SDK控制，只要是硬件设备支持的参数。

最后，我们的Demo会教你怎么使用SDK，上面提及的大多数功能使用方法都被清楚地写在Demo里了（主要看 [CamManager][CamManager.dir] ）。

总而言之，“~~屁话少说，放码过来~~”***“代码胜于雄辩”***。

## 使用方法

### 1. 安装

#### Ⅰ 导入Module（推荐）

- 步骤 1.

添加下列的Gradle配置到你的Android项目。在里你的根目录 `build.gradle`文件中：

```
allprojects {
    repositories {
        maven {
            url 'https://gitee.com/wang_ziheng/libcommon/raw/master/repository/'
        }
    }
}
```

- 步骤 2.

将文件夹`libexcamera`作为module导入你的项目。

***注意：*** 为了支持 **Android 10** 设备使用 USB 连接，你需要保持你的 `targetSdkVersion` 低于 `27`（27可以）。 更多相关信息，你可以看 [UVCPermissionTest][UVCPermissionTest.link] 和 [这一篇Issue][UVCPermissionTest_Issue.link]

- 步骤 3.

将 module `libexcamera` 作为依赖添加到你的 app `build.gradle` 文件：

```
implementation project(path: ':libexcamera')
```


***现在你就可以在你的项目中使用 ExCamera SDK 了。 :)***

------------

### 2. 连接并预览

#### USB 连接

**USB 连接 SDK** 是基于 [saki4510t/UVCCamera][UVCCamera.link] 开发的。在通过USB连接你的相机设备之前，请确保你的手机/平板已经打开了OTG功能。

1. 我们需要通过`USBMonitor`注册一个广播来监听OTG连接，并且我们通常在Activity生命周期的`onStart`中注册和在`onStop`中注销。

2. 在通过广播发现USB设备后，我们要请求权限和USB设备进行数据交换。只有在获取到这个权限后，我们才是真正和USB设备连接上。

3. 在权限被允许后，我们尝试打开UVC Camera和开始预览。

4. 在离开预览页面后，我们需要在重新回到页面是恢复预览，所以我们在Activity生命周期的`onResume`中开始预览和`onPause`中结束预览。

5. 当USB连接断开或者Activity要销毁的时候，我们需要停止预览并释放UVC Camera资源，所以需要在Activity生命周期的`onDestory`中释放资源。

6. 更多信息，你可以看一看 [UsbCameraCommand][UsbCameraCommand.java].

正如上述所说，USB连接的过程比较复杂，但是你需要做的仅仅是初始化 [UsbCameraController][UsbCameraController.java] 并将它和 [UsbCameraView][UsbCameraView.java] 进行绑定。以Demo中的 [UsbMicroCamManager][UsbMicroCamManager.java] 代码为例：

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

OK，现在你就可以在你自己的应用中进行USB连接的预览了。

#### WiFi 连接

**WiFi 连接 SDK** 是为连接到我们 **WiFi Box (V2版本及以上)**或**Planet** 的相机设备服务的，并且我们基于 [Retrofit2][Retrofit.link] + [OkHttp3][OkHttp.link] + [RxAndroid][RxAndroid.link] 开发的SDK，所以你需要**注意依赖冲突**。在通过WiFi连接到我们的相机设备之前，请确保你的手机/平板设备连接到WiFi Box创建的无线热点，如`ScopeCAM_V2_XXXXXXXX`、`ScopeCAM_V3_XXXXXXXX`或`ScopeCAM_Planet_XXXXXXXX`。

**ps.** 控制WiFi Box(Planet)的图传IP地址为`http://192.168.8.10:8080/`, 并且你可以在 [ApiService][ApiService.java] 中找到网络请求API。

1. 首先，我们通过网络请求`?action=stream`接口获取图像数据流InputStream，通过数据流我们可以解析成图像Bitmap。InputStream应写成单例模式，否则可能导致超大内存占用或者内存溢出。总之，在Activity生命周期`onStart`中请求图像数据流并在`onStop`中释放资源。

2. 在获取图像数据流成功后，下一步是请求`input.json`获取相机参数。这至关重要，因为我们也需要它们来更新相关参数。

3. 在离开预览页面后，我们需要在重新回到页面是恢复预览，所以我们在Activity生命周期的`onResume`中开始预览和`onPause`中结束预览。

4. 有时候，我们可能会断开无线连接或者遇到其他问题，所以有一个重试机制非常重要。好吧，我们已经在SDK中写好了重试机制所以你不需要担心这些问题。

5. 同样的，请在Activity生命周期`onDestroy`中释放资源。

6. 更多信息，你可以看一看 [WifiCameraCommand][WifiCameraCommand.java]。

同样的，我们也提供了简单的方式来完成这个复杂的过程，仅需要初始化 [WifiCameraController][WifiCameraController.java] 并将它和[WifiCameraView][WifiCameraView.java] 进行绑定。以Demo中的 [WifiMicroCamManager][WifiMicroCamManager.java] 代码为例：

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

OK，现在你就可以在你自己的应用中进行WiFi连接的预览了。

------------

### 3. 基础功能

|功能|USB 连接|WiFi 连接|
| :------------: | :------------: | :------------: |
|**更新分辨率**|UsbCameraController.updateResolution(int width, int height)|WifiCameraController.updateResolution(int width, int height)|
|**水平翻转**|UsbCameraSP.setIsFlipHorizontal(boolean value)|WifiCameraSP.setIsFlipHorizontal(boolean value)|
|**垂直翻转**|UsbCameraSP.setIsFlipVertical(boolean value)|WifiCameraSP.setIsFlipVertical(boolean value)|
|**拍照（默认路径）**|UsbCameraController.takePhoto()|WifiCameraController.takePhoto()|
|**拍照（自定义路径）**|UsbCameraController.takePhoto(String path)|WifiCameraController.takePhoto(String path)|
|**开始录像（默认路径）**|UsbCameraController.startRecord()|WifiCameraController.startRecord()|
|**开始录像（自定义路径）**|UsbCameraController.startRecord(String path)|WifiCameraController.startRecord(String path)|
|**停止录像**|UsbCameraController.stopRecord()|WifiCameraController.stopRecord()|
|**开始延时摄影（默认路径）**|UsbCameraController.startTLRecord()|WifiCameraController.startTLRecord()|
|**开始延时摄影（自定义路径）**|UsbCameraController.startTLRecord(String path)|WifiCameraController.startTLRecord(String path)|
|**停止延时摄影**|UsbCameraController.stopTLRecord()|WifiCameraController.stopTLRecord()|
|**开始去噪拍照（默认路径）**|UsbCameraController.startStackAvg()|WifiCameraController.startStackAvg()|
|**开始去噪拍照（自定义路径）**|UsbCameraController.startStackAvg(String path)|WifiCameraController.startStackAvg(String path)|
|**取消去噪拍照**|UsbCameraController.cancelStackAvg()|WifiCameraController.cancelStackAvg()|
|**开始自动对焦（望远相机）**|UsbCameraController.startTeleAF()|WifiCameraController.startTeleAF()|
|**停止自动对焦（望远相机）**|UsbCameraController.stopTeleAF()|WifiCameraController.stopTeleAF()|

------------

### 4. 参数

#### USB 连接

对应USB连接，所有的参数都会由 [UVCCamera][UVCCamera.java] 以 [UVCConfig][UVCConfig.java] 对象保存下来，并且被区分为两种类型： **Auto** 和 **Param**.

##### Auto 参数

|参数|标识（UVCConfig）|标识（UVCCamera）|显微相机|望远相机|
| :------------: | :------------: | :------------: | :------------: | :------------: |
|**FocusAuto**|TAG_AUTO_FOCUS_AUTO|CTRL_FOCUS_AUTO|✔|✔|
|Privacy|TAG_AUTO_PRIVACY|CTRL_PRIVACY|✖|✖|
|**WhiteBalanceAuto**|TAG_AUTO_WHITE_BALANCE_AUTO|PU_WB_TEMP_AUTO|✔|✔|
|WhiteBalanceComponentAuto|TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO|PU_WB_COMPO_AUTO|✖|✖|

##### Param 参数

|参数|标识（UVCConfig）|标识（UVCCamera）|显微相机|望远相机|
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
|**Sharpness**|TAG_PARAM_SHARPNESS|PU_SHARPNESS|✔|✖|
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

##### 使用方法

使用 `UsbCameraController` 中的方法，和 `UVCConfig` 中的标识:

- **public UVCAutoConfig getAutoConfig(String tag)**

	通过标识获取自动类参数实体

- **public UVCParamConfig getParamConfig(String tag)**

	通过标识获取数值类参数实体

- **public boolean checkConfigEnable(String tag)**

	检测标识对应参数是否可用

- **public boolean getAuto(String tag)**

	获取标识对应的自动类参数当前是否自动

- **public void setAuto(String tag, boolean value)**

	设置标识对应的自动类参数为自动或手动

- **public void resetAuto(String tag)**

	重置标识对应的自动类参数

- **public int getParam(String tag)**

	获取标识对应的数值类参数的当前数值

- **public void setParam(String tag, int param)**

	设置标识对应的数值类参数的数值

- **public void resetParam(String tag)**

	重置标识对应的数值类参数

- **public void startTeleFocus(boolean isBack)**

	开始调整望远相机对焦，它通常在对焦按钮按下时调用

- **public void stopTeleFocus(boolean isBack)**

	结束调整望远相机对焦，它通常在对焦按钮抬起或移开时调用

**Note:**

对于**显微相机**的`Exposure`（曝光），它在`ExposureMode = 8`时是**自动**且在`ExposureMode = 1`时是**手动**。

#### WiFi 连接

同样的，对应WiFi连接，所有的参数都会由网络请求结果 json 以 [WifiConfig][WifiConfig.java] 对象保存下来，并且被区分为两种类型： **Auto** 和 **Param**。

##### Auto 参数

|参数|标识（WifiConfig）|ID|显微相机|望远相机|
| :------------: | :------------: | :------------: |:------------: |:------------: |
|**FocusAuto**|TAG_AUTO_FOCUS_AUTO|10094860|✔|✔|
|**WhiteBalanceAuto**|TAG_AUTO_WHITE_BALANCE_AUTO|9963788|✔|✔|
|**ExposureAuto**|TAG_AUTO_EXPOSURE_AUTO|10094849|✔|✔|

##### Param 参数

|参数|标识（WifiConfig）|ID|显微相机|望远相机|
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

使用 `WifiCameraController` 中的方法，和 `WifiConfig` 中的标识:

- **public WifiAutoConfig getAutoConfig(String tag)**

	通过标识获取自动类参数实体

- **public WifiParamConfig getParamConfig(String tag)**

	通过标识获取数值类参数实体

- **public boolean resetConfig(String tag)**

	重置标识对应的参数

- **public boolean getAuto(String tag)**

	获取标识对应的自动类参数当前是否自动

- **public void setAuto(String tag, boolean value)**

	设置标识对应的自动类参数为自动或手动

- **public int getParam(String tag)**

	获取标识对应的数值类参数的当前数值

- **public void setParam(String tag, int param)**

	设置标识对应的数值类参数的数值

- **public void startTeleFocus(boolean isBack)**

	开始调整望远相机对焦，它通常在对焦按钮按下时调用

- **public void stopTeleFocus(boolean isBack)**

	结束调整望远相机对焦，它通常在对焦按钮抬起或移开时调用

**Note:**

对于**显微相机**的`Focus`（对焦），它在`FocusAuto = 1`时是**自动**且在`FocusAuto = 0`时是**手动**。

对于**显微相机**的`WhiteBalance`（白平衡），它在`WhiteBalanceAuto = 1`时是**自动**且在`WhiteBalanceAuto = 0`时是**手动**。

对于**显微相机**的`Exposure`（曝光），它在`ExposureMode = 3`时是**自动**且在`ExposureMode = 1`时是**手动**。

------------

### 5. 回调

#### USB 连接

`UsbCameraController.OnControlListener`

- **void onUsbConnect()**

	当USB设备连接并成功获取权限时调用

- **void onUsbDisConnect()**

	当USB设备断开连接时调用

- **void onCameraOpen()**

	当UVC Camera打开时调用

- **void onCameraClose()**

	当UVC Camera关闭时调用

- **void onPreviewStart()**

	当预览开始时调用

- **void onPreviewStop()**

	当预览停止时调用

- **void onUsbStateUpdate(UsbCameraState state)**

	当USB相机状态 [UsbCameraState][UsbCameraState.java] 更新时调用

- **void onActionStateUpdate(ActionState state)**

	当操作状态 [ActionState][ActionState.java] 更新时调用

- **void onLoadFrame(Bitmap bitmap)**

	你可以通过这个回调获取每一帧的图像Bitmap

- **void onLoadFPS(int instantFPS, float averageFPS)**

	你可以通过这个回调获取图像FPS

#### WiFi 连接

`WifiCameraController.OnControlListener`

- **void onStreamStart(boolean isRetry)**

	当成功获取图像数据并正确预览时调用，如果从重试状态中恢复，isRetry将会是true

- **void onStreamStop(boolean isRetry)**

	当停止获取结束获取图像数据时调用，如果当前状态变成重试状态，isRetry将会是true

- **void onWifiStateUpdate(WifiCameraState state)**

	当WiFi相机状态 [WifiCameraState][WifiCameraState.java] 更新时调用

- **void onActionStateUpdate(ActionState state)**

	当操作状态 [ActionState][ActionState.java] 更新时调用

- **void onParamUpdate(WifiCameraParam param, boolean isReset)**

	当网络请求相机参数成功时调用，如果是刚开始获取图像数据时请求的相机参数，isReset将会是true

- **void onLoadFrame(Bitmap bitmap)**

	你可以通过这个回调获取每一帧的图像Bitmap

- **void onLoadFPS(int instantFPS, float averageFPS)**

	你可以通过这个回调获取图像FPS

------------

### 6. Planet 运动控制

云台运动通过以下接口控制：

通信协议:HTTP
接口地址:http://192.168.8.10:8092 
请求方式:GET
请求参数:
|字段|说明|值类型|值|
| :------------: | :------------ | :------------: |:------------ |
|**id**|电机id|int|0-旋转<br>1-俯仰|
|**controlType**|控制方式|int|0-停止<br>1-正转<br>2-反转<br>3-复位|
|**time**|运行时间|int|>=0(单位:毫秒)|
|**mode**|电机转动模式|int|0-指定速度<br>1-指定时间和速度|
|**speed**|电机速度，不是角速度①|int|>=400<br><=2000|
|**subDivision**|电机细分|int|2<br>4<br>8<br>16|
|**returnTrip**|是否消除回程差|int|0-不消回程<br>1-消回程|
|**returnTripTime**|消回程时间|int|>=0(单位:毫秒)|

①电机的旋转角速度由两个参数决定：‘speed’和”subDivision”。 电机0的理论角速度等于3,662.109375/(speed\*subDivision)，电机1的理论角速度等于1,831.0546875/(speed\*subDivision)。

返回结果:
|字段|说明|值类型|值|
| :------------: | :------------ | :------------: |:------------ |
|**id**|电机id|int|0-旋转 1-俯仰|
|**location**|电机位置|double|电机0:0-360<br>电机1:30-150|
|**posLimit**|电机正限位|int|0-不在正限位<br>1-在正限位|
|**negLimit**|电机负限位|int|0-不在负限位<br>1-在负限位|
|**resetFlag**|电机是否在复位位置|int|0-不在复位位置<br>1-在复位位置|
|**result**|执行结果|int|1000-执行ok<br>1001-电机未初始化<br>1002-电机正在运行<br>1003-电机已停止<br>1004-电机正在复位|

使用示例 [PlanetCommand][PlanetCommand.java]。

------------

### 7. 默认配置

USB 连接默认配置 : [UsbCameraConstant][UsbCameraConstant.java]

WiFi 连接默认配置 : [WifiCameraConstant][WifiCameraConstant.java]

默认输出路径 : [OutputUtil][OutputUtil.java]

------------

## 感谢

[UVCCamera][UVCCamera.link] , [AndroidUSBCamera][AndroidUSBCamera.link] , [mjpg-streamer][mjpg-streamer.link]

在ExCamera SDK 和 Demo 中使用的第三方库:

[Retrofit][Retrofit.link] , [OkHttp][OkHttp.link] , [RxAndroid][RxAndroid.link] , [ButterKnife][ButterKnife.link] , [PermissionX][PermissionX.link] , [ImmersionBar][ImmersionBar.link]

## 联系我们

**邮箱：**

software@cvgc.cn

**官方网站**

[www.cvgc.cn][cvgc.link]

[www.tipscope.com][tipscope.link]

[www.tinyscope.com][tinyscope.link]

## 开源许可

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