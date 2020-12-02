package com.convergence.excamera.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convergence.excamera.R;
import com.convergence.excamera.manager.CamManager;
import com.convergence.excamera.manager.UsbMicroCamManager;
import com.convergence.excamera.sdk.usb.core.UsbCameraView;
import com.convergence.excamera.view.config.UsbMicroConfigLayout;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 显微相机USB连接页面
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbMicroCamActivity extends AppCompatActivity {

    @BindView(R.id.iv_close_activity_usb_micro_camera)
    ImageView ivCloseActivityUsbMicroCamera;
    @BindView(R.id.iv_take_photo_activity_usb_micro_camera)
    ImageView ivTakePhotoActivityUsbMicroCamera;
    @BindView(R.id.iv_record_activity_usb_micro_camera)
    ImageView ivRecordActivityUsbMicroCamera;
    @BindView(R.id.iv_resolution_activity_usb_micro_camera)
    ImageView ivResolutionActivityUsbMicroCamera;
    @BindView(R.id.view_usb_camera_activity_usb_micro_camera)
    UsbCameraView viewUsbCameraActivityUsbMicroCamera;
    @BindView(R.id.tv_fps_activity_usb_micro_camera)
    TextView tvFpsActivityUsbMicroCamera;
    @BindView(R.id.tv_record_time_activity_usb_micro_camera)
    TextView tvRecordTimeActivityUsbMicroCamera;
    @BindView(R.id.item_config_activity_usb_micro_camera)
    UsbMicroConfigLayout itemConfigActivityUsbMicroCamera;

    private CamManager camManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_micro_camera);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
        camManager = new UsbMicroCamManager.Builder(this, viewUsbCameraActivityUsbMicroCamera)
                .bindConfigLayout(itemConfigActivityUsbMicroCamera)
                .bindRecordTimeText(tvRecordTimeActivityUsbMicroCamera)
                .bindFpsText(tvFpsActivityUsbMicroCamera)
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

    @OnClick({R.id.iv_close_activity_usb_micro_camera, R.id.iv_take_photo_activity_usb_micro_camera, R.id.iv_record_activity_usb_micro_camera, R.id.iv_resolution_activity_usb_micro_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_activity_usb_micro_camera:
                finish();
                break;
            case R.id.iv_take_photo_activity_usb_micro_camera:
                camManager.takePhoto();
                break;
            case R.id.iv_record_activity_usb_micro_camera:
                if (camManager.isRecording()) {
                    camManager.stopRecord();
                } else {
                    camManager.startRecord();
                }
                break;
            case R.id.iv_resolution_activity_usb_micro_camera:
                camManager.showResolutionSelection();
                break;
            default:
                break;
        }
    }
}
