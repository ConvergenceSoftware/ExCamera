package com.convergence.excamera.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convergence.excamera.R;
import com.convergence.excamera.manager.CamManager;
import com.convergence.excamera.manager.WifiMicroCamManager;
import com.convergence.excamera.sdk.wifi.core.WifiCameraView;
import com.convergence.excamera.view.config.WifiMicroConfigLayout;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 显微相机WiFi连接页面
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-17
 * @Organization Convergence Ltd.
 */
public class WifiMicroCamActivity extends AppCompatActivity {

    @BindView(R.id.iv_close_activity_wifi_micro_camera)
    ImageView ivCloseActivityWifiMicroCamera;
    @BindView(R.id.iv_take_photo_activity_wifi_micro_camera)
    ImageView ivTakePhotoActivityWifiMicroCamera;
    @BindView(R.id.iv_record_activity_wifi_micro_camera)
    ImageView ivRecordActivityWifiMicroCamera;
    @BindView(R.id.iv_resolution_activity_wifi_micro_camera)
    ImageView ivResolutionActivityWifiMicroCamera;
    @BindView(R.id.view_wifi_camera_activity_wifi_micro_camera)
    WifiCameraView viewWifiCameraActivityWifiMicroCamera;
    @BindView(R.id.tv_fps_activity_wifi_micro_camera)
    TextView tvFpsActivityWifiMicroCamera;
    @BindView(R.id.tv_record_time_activity_wifi_micro_camera)
    TextView tvRecordTimeActivityWifiMicroCamera;
    @BindView(R.id.item_config_activity_wifi_micro_camera)
    WifiMicroConfigLayout itemConfigActivityWifiMicroCamera;

    private CamManager camManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_micro_camera);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
        camManager = new WifiMicroCamManager.Builder(this, viewWifiCameraActivityWifiMicroCamera)
                .bindConfigLayout(itemConfigActivityWifiMicroCamera)
                .bindRecordTimeText(tvRecordTimeActivityWifiMicroCamera)
                .bindFpsText(tvFpsActivityWifiMicroCamera)
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

    @OnClick({R.id.iv_close_activity_wifi_micro_camera, R.id.iv_take_photo_activity_wifi_micro_camera, R.id.iv_record_activity_wifi_micro_camera, R.id.iv_resolution_activity_wifi_micro_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_activity_wifi_micro_camera:
                finish();
                break;
            case R.id.iv_take_photo_activity_wifi_micro_camera:
                camManager.takePhoto();
                break;
            case R.id.iv_record_activity_wifi_micro_camera:
                if (camManager.isRecording()) {
                    camManager.stopRecord();
                } else {
                    camManager.startRecord();
                }
                break;
            case R.id.iv_resolution_activity_wifi_micro_camera:
                camManager.showResolutionSelection();
                break;
            default:
                break;
        }
    }
}
