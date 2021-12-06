package com.convergence.excamera.activity.camera;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.convergence.excamera.R;
import com.convergence.excamera.activity.ExCameraActivity;
import com.convergence.excamera.manager.CamManager;
import com.convergence.excamera.manager.PlanetTeleCamManager;
import com.convergence.excamera.sdk.planet.net.PlanetNetWork;
import com.convergence.excamera.sdk.wifi.core.WifiCameraView;
import com.convergence.excamera.view.config.PlanetTeleConfigLayout;


import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 望远相机Planet连接页面
 *
 * @Author LiLei
 * @CreateDate 2021-06-02
 * @Organization Convergence Ltd.
 */
public class PlanetTeleCamActivity extends ExCameraActivity {

    @BindView(R.id.iv_close_activity_wifi_tele_camera)
    ImageView ivCloseActivityWifiTeleCamera;
    @BindView(R.id.iv_take_photo_activity_wifi_tele_camera)
    ImageView ivTakePhotoActivityWifiTeleCamera;
    @BindView(R.id.iv_record_activity_wifi_tele_camera)
    ImageView ivRecordActivityWifiTeleCamera;
    @BindView(R.id.iv_resolution_activity_wifi_tele_camera)
    ImageView ivResolutionActivityWifiTeleCamera;
    @BindView(R.id.view_wifi_camera_activity_wifi_tele_camera)
    WifiCameraView viewWifiCameraActivityWifiTeleCamera;
    @BindView(R.id.tv_fps_activity_wifi_tele_camera)
    TextView tvFpsActivityWifiTeleCamera;
    @BindView(R.id.tv_record_time_activity_wifi_tele_camera)
    TextView tvRecordTimeActivityWifiTeleCamera;

    @BindView(R.id.item_config_activity_planet_tele_camera)
    PlanetTeleConfigLayout itemConfigActivityWifiTeleCamera;
    @BindView(R.id.sv_activity_planet_tele_camera)
    ScrollView svActivityPlanetTeleCamera;

    @Override
    protected int onBindLayoutId() {
        return R.layout.activity_planet_tele_camera;
    }

    @Override
    protected CamManager onBindCamManager() {
        PlanetTeleCamManager planetTeleCamManager = new PlanetTeleCamManager.Builder(this, viewWifiCameraActivityWifiTeleCamera)
                .bindConfigLayout(itemConfigActivityWifiTeleCamera)
                .bindRecordTimeText(tvRecordTimeActivityWifiTeleCamera)
                .bindFpsText(tvFpsActivityWifiTeleCamera)
                .build();
        return planetTeleCamManager;
    }


    private static final String TAG = "PlanetTeleCamActivi";
    private final PlanetNetWork netWork = PlanetNetWork.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.iv_close_activity_wifi_tele_camera, R.id.iv_take_photo_activity_wifi_tele_camera, R.id.iv_record_activity_wifi_tele_camera, R.id.iv_resolution_activity_wifi_tele_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_activity_wifi_tele_camera:
                finish();
                break;
            case R.id.iv_take_photo_activity_wifi_tele_camera:
                camManager.takePhoto();
                break;
            case R.id.iv_record_activity_wifi_tele_camera:
                if (camManager.isRecording()) {
                    camManager.stopRecord();
                } else {
                    camManager.startRecord();
                }
                break;
            case R.id.iv_resolution_activity_wifi_tele_camera:
                camManager.showResolutionSelection();
                break;
            default:
                break;
        }
    }

    @OnLongClick({R.id.iv_take_photo_activity_wifi_tele_camera, R.id.iv_record_activity_wifi_tele_camera})
    public void onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_take_photo_activity_wifi_tele_camera:
                camManager.startStackAvg();
                break;
            case R.id.iv_record_activity_wifi_tele_camera:
                if (camManager.isTLRecording()) {
                    camManager.stopTLRecord();
                } else {
                    camManager.startTLRecord(15);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
