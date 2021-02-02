package com.convergence.excamera.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convergence.excamera.R;
import com.convergence.excamera.manager.CamManager;
import com.convergence.excamera.manager.WifiTeleCamManager;
import com.convergence.excamera.sdk.wifi.core.WifiCameraView;
import com.convergence.excamera.view.config.WifiTeleConfigLayout;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 望远相机WiFi连接页面
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-17
 * @Organization Convergence Ltd.
 */
public class WifiTeleCamActivity extends AppCompatActivity {

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
    @BindView(R.id.item_config_activity_wifi_tele_camera)
    WifiTeleConfigLayout itemConfigActivityWifiTeleCamera;

    private CamManager camManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_tele_camera);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
        camManager = new WifiTeleCamManager.Builder(this, viewWifiCameraActivityWifiTeleCamera)
                .bindConfigLayout(itemConfigActivityWifiTeleCamera)
                .bindRecordTimeText(tvRecordTimeActivityWifiTeleCamera)
                .bindFpsText(tvFpsActivityWifiTeleCamera)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        camManager.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        camManager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camManager.onDestroy();
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
}
