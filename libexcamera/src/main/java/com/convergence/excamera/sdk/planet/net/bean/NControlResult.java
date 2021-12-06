package com.convergence.excamera.sdk.planet.net.bean;

/**
 * 云台操作网络请求结果
 *
 * @Author LiLei
 * @CreateDate 2021-06-22
 * @Organization Convergence Ltd.
 */
public class NControlResult {
    private int id;         //0-旋转 1-俯仰
    private double location;//电机0:0-360 电机1:10-170
    private int posLimit;   //0:不在正限位 1:在正限位
    private int negLimit;   //0:不在负限位 1:在负限位
    private int resetFlag;  //0:不在复位位置 1:在复位位置
    private int result;     //1000:执行ok 1001:电机未初始化  1002:电机正在运行  1003:电机已停止  1004:电机正在复位

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public int getPosLimit() {
        return posLimit;
    }

    public void setPosLimit(int posLimit) {
        this.posLimit = posLimit;
    }

    public int getNegLimit() {
        return negLimit;
    }

    public void setNegLimit(int negLimit) {
        this.negLimit = negLimit;
    }

    public int getResetFlag() {
        return resetFlag;
    }

    public void setResetFlag(int resetFlag) {
        this.resetFlag = resetFlag;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
