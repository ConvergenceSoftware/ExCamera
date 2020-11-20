package com.convergence.excamera.sdk.wifi.net.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * 获取所有参数请求结果
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class NConfigList {

    /**
     * {
     * "controls": [
     * {
     * "name": "Brightness",
     * "id": "9963776",
     * "type": "1",
     * "min": "-64",
     * "max": "64",
     * "step": "1",
     * "default": "0",
     * "value": "0",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Contrast",
     * "id": "9963777",
     * "type": "1",
     * "min": "0",
     * "max": "100",
     * "step": "1",
     * "default": "40",
     * "value": "40",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Saturation",
     * "id": "9963778",
     * "type": "1",
     * "min": "0",
     * "max": "100",
     * "step": "1",
     * "default": "64",
     * "value": "24",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Hue",
     * "id": "9963779",
     * "type": "1",
     * "min": "-180",
     * "max": "180",
     * "step": "1",
     * "default": "0",
     * "value": "0",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "White Balance Temperature, Auto",
     * "id": "9963788",
     * "type": "2",
     * "min": "0",
     * "max": "1",
     * "step": "1",
     * "default": "1",
     * "value": "1",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Gamma",
     * "id": "9963792",
     * "type": "1",
     * "min": "100",
     * "max": "500",
     * "step": "1",
     * "default": "300",
     * "value": "100",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Gain",
     * "id": "9963795",
     * "type": "1",
     * "min": "1",
     * "max": "128",
     * "step": "1",
     * "default": "64",
     * "value": "64",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Power Line Frequency",
     * "id": "9963800",
     * "type": "3",
     * "min": "0",
     * "max": "2",
     * "step": "1",
     * "default": "1",
     * "value": "1",
     * "dest": "0",
     * "flags": "0",
     * "group": "1",
     * "menu": {
     * "0": "Disabled",
     * "1": "50 Hz",
     * "2": "60 Hz"
     * }
     * },
     * {
     * "name": "White Balance Temperature",
     * "id": "9963802",
     * "type": "1",
     * "min": "2800",
     * "max": "6500",
     * "step": "10",
     * "default": "4600",
     * "value": "4550",
     * "dest": "0",
     * "flags": "16",
     * "group": "1"
     * },
     * {
     * "name": "Sharpness",
     * "id": "9963803",
     * "type": "1",
     * "min": "0",
     * "max": "100",
     * "step": "1",
     * "default": "50",
     * "value": "18",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Backlight Compensation",
     * "id": "9963804",
     * "type": "1",
     * "min": "0",
     * "max": "2",
     * "step": "1",
     * "default": "0",
     * "value": "0",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Exposure, Auto",
     * "id": "10094849",
     * "type": "3",
     * "min": "0",
     * "max": "3",
     * "step": "1",
     * "default": "3",
     * "value": "3",
     * "dest": "0",
     * "flags": "0",
     * "group": "1",
     * "menu": {
     * "0": " rn A ",
     * "1": "Manual Mode",
     * "2": "",
     * "3": "Aperture Priority Mode"
     * }
     * },
     * {
     * "name": "Exposure (Absolute)",
     * "id": "10094850",
     * "type": "1",
     * "min": "50",
     * "max": "10000",
     * "step": "1",
     * "default": "166",
     * "value": "50",
     * "dest": "0",
     * "flags": "16",
     * "group": "1"
     * },
     * {
     * "name": "Exposure, Auto Priority",
     * "id": "10094851",
     * "type": "2",
     * "min": "0",
     * "max": "1",
     * "step": "1",
     * "default": "0",
     * "value": "1",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "Focus (absolute)",
     * "id": "10094858",
     * "type": "1",
     * "min": "0",
     * "max": "1023",
     * "step": "1",
     * "default": "68",
     * "value": "160",
     * "dest": "0",
     * "flags": "16",
     * "group": "1"
     * },
     * {
     * "name": "Focus, Auto",
     * "id": "10094860",
     * "type": "2",
     * "min": "0",
     * "max": "1",
     * "step": "1",
     * "default": "1",
     * "value": "1",
     * "dest": "0",
     * "flags": "0",
     * "group": "1"
     * },
     * {
     * "name": "JPEG quality",
     * "id": "1",
     * "type": "1",
     * "min": "0",
     * "max": "100",
     * "step": "1",
     * "default": "50",
     * "value": "0",
     * "dest": "0",
     * "flags": "0",
     * "group": "3"
     * }
     * ],
     * "formats": [
     * {
     * "id": "0",
     * "name": "Motion-JPEG",
     * "compressed": "true",
     * "emulated": "false",
     * "current": "true",
     * "resolutions": {
     * "0": "2592x1944",
     * "1": "320x180",
     * "2": "320x240",
     * "3": "424x240",
     * "4": "640x360",
     * "5": "848x480",
     * "6": "960x540",
     * "7": "1280x720",
     * "8": "1920x1080",
     * "9": "2048x1536",
     * "10": "640x480"
     * },
     * "currentResolution": "10"
     * },
     * {
     * "id": "1",
     * "name": "YUYV 4:2:2",
     * "compressed": "false",
     * "emulated": "false",
     * "current": "false",
     * "resolutions": {
     * "0": "2592x1944",
     * "1": "320x180",
     * "2": "320x240",
     * "3": "424x240",
     * "4": "640x360",
     * "5": "848x480",
     * "6": "960x540",
     * "7": "1280x720",
     * "8": "1920x1080",
     * "9": "2048x1536",
     * "10": "640x480"
     * }
     * }
     * ]
     * }
     */

    private List<ControlsBean> controls;
    private List<FormatsBean> formats;

    public List<ControlsBean> getControls() {
        return controls;
    }

    public void setControls(List<ControlsBean> controls) {
        this.controls = controls;
    }

    public List<FormatsBean> getFormats() {
        return formats;
    }

    public void setFormats(List<FormatsBean> formats) {
        this.formats = formats;
    }

    public static class ControlsBean {
        /**
         * name : Brightness
         * id : 9963776
         * type : 1
         * min : -64
         * max : 64
         * step : 1
         * default : 0
         * value : 0
         * dest : 0
         * flags : 0
         * group : 1
         * menu : {"0":"Disabled","1":"50 Hz","2":"60 Hz"}
         */

        private String name;
        private String id;
        private int type;
        private int min;
        private int max;
        private int step;
        @SerializedName("default")
        private int defaultX;
        private int value;
        private int dest;
        private int flags;
        private int group;
//        private Map<String, String> menu;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getDefaultX() {
            return defaultX;
        }

        public void setDefaultX(int defaultX) {
            this.defaultX = defaultX;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getDest() {
            return dest;
        }

        public void setDest(int dest) {
            this.dest = dest;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

//        public Map<String, String> getMenu() {
//            return menu;
//        }
//
//        public void setMenu(Map<String, String> menu) {
//            this.menu = menu;
//        }
    }

    public static class FormatsBean {
        /**
         * id : 0
         * name : Motion-JPEG
         * compressed : true
         * emulated : false
         * current : true
         * resolutions : {"0":"2592x1944","1":"320x180","2":"320x240","3":"424x240","4":"640x360","5":"848x480","6":"960x540","7":"1280x720","8":"1920x1080","9":"2048x1536","10":"640x480"}
         * currentResolution : 10
         */

        private String id;
        private String name;
        private boolean compressed;
        private boolean emulated;
        private boolean current;
        private Map<Integer, String> resolutions;
        private int currentResolution;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getCompressed() {
            return compressed;
        }

        public void setCompressed(boolean compressed) {
            this.compressed = compressed;
        }

        public boolean getEmulated() {
            return emulated;
        }

        public void setEmulated(boolean emulated) {
            this.emulated = emulated;
        }

        public boolean getCurrent() {
            return current;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public int getCurrentResolution() {
            return currentResolution;
        }

        public void setCurrentResolution(int currentResolution) {
            this.currentResolution = currentResolution;
        }

        public Map<Integer, String> getResolutions() {
            return resolutions;
        }

        public void setResolutions(Map<Integer, String> resolutions) {
            this.resolutions = resolutions;
        }
    }
}
