package com.convergence.excamera.sdk.planet.net.bean;

/**
 * 云台操作网络请求参数
 *
 * @Author LiLei
 * @CreateDate 2021-06-22
 * @Organization Convergence Ltd.
 */
public class NControlBean {

    private int id; //电机id 0-旋转 1-俯仰
    private int controlType;//控制方式 0-停止 1-正转 2-反转 3-复位
    private int time; //运行时间 单位; 毫秒
    private int mode;//0-指定速度 1-指定时间和速度
//    private double angle; //角度
    private int speed; //速度
    private int subDivision; //电机细分
    private int returnTrip; //是否处理回程
    private int returnTripTime; //回程处理时间


    public NControlBean(int id, int controlType, int time, int mode, int speed, int subDivision, int returnTrip, int returnTripTime) {
        this.id = id;
        this.controlType = controlType;
        this.time = time;
        this.mode = mode;
//        this.angle = angle;
        this.speed = speed;
        this.subDivision = subDivision;
        this.returnTrip = returnTrip;
        this.returnTripTime = returnTripTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

//    public double getAngle() {
//        return angle;
//    }
//
//    public void setAngle(double angle) {
//        this.angle = angle;
//    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(int subDivision) {
        this.subDivision = subDivision;
    }

    public int getReturnTrip() {
        return returnTrip;
    }

    public void setReturnTrip(int returnTrip) {
        this.returnTrip = returnTrip;
    }

    public int getReturnTripTime() {
        return returnTripTime;
    }

    public void setReturnTripTime(int returnTripTime) {
        this.returnTripTime = returnTripTime;
    }
}
