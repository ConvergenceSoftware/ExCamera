package com.convergence.excamera.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.convergence.excamera.R;
import com.convergence.excamera.manager.CamManager;
import com.convergence.excamera.manager.UsbTeleCamManager;
import com.convergence.excamera.sdk.usb.core.UsbCameraView;
import com.convergence.excamera.view.config.UsbTeleConfigLayout;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 望远相机USB连接页面
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-10
 * @Organization Convergence Ltd.
 */
public class UsbTeleCamActivity extends AppCompatActivity {

    @BindView(R.id.iv_close_activity_usb_tele_camera)
    ImageView ivCloseActivityUsbTeleCamera;
    @BindView(R.id.iv_take_photo_activity_usb_tele_camera)
    ImageView ivTakePhotoActivityUsbTeleCamera;
    @BindView(R.id.iv_record_activity_usb_tele_camera)
    ImageView ivRecordActivityUsbTeleCamera;
    @BindView(R.id.iv_resolution_activity_usb_tele_camera)
    ImageView ivResolutionActivityUsbTeleCamera;
    @BindView(R.id.view_usb_camera_activity_usb_tele_camera)
    UsbCameraView viewUsbCameraActivityUsbTeleCamera;
    @BindView(R.id.tv_fps_activity_usb_tele_camera)
    TextView tvFpsActivityUsbTeleCamera;
    @BindView(R.id.tv_record_time_activity_usb_tele_camera)
    TextView tvRecordTimeActivityUsbTeleCamera;
    @BindView(R.id.item_config_activity_usb_tele_camera)
    UsbTeleConfigLayout itemConfigActivityUsbTeleCamera;

    private CamManager camManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_tele_camera);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
        camManager = new UsbTeleCamManager.Builder(this, viewUsbCameraActivityUsbTeleCamera)
                .bindConfigLayout(itemConfigActivityUsbTeleCamera)
                .bindRecordTimeText(tvRecordTimeActivityUsbTeleCamera)
                .bindFpsText(tvFpsActivityUsbTeleCamera)
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

    @OnClick({R.id.iv_close_activity_usb_tele_camera, R.id.iv_take_photo_activity_usb_tele_camera, R.id.iv_record_activity_usb_tele_camera, R.id.iv_resolution_activity_usb_tele_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_activity_usb_tele_camera:
                finish();
                break;
            case R.id.iv_take_photo_activity_usb_tele_camera:
                camManager.takePhoto();
                break;
            case R.id.iv_record_activity_usb_tele_camera:
                if (camManager.isRecording()) {
                    camManager.stopRecord();
                } else {
                    camManager.startRecord();
                }
                break;
            case R.id.iv_resolution_activity_usb_tele_camera:
                camManager.showResolutionSelection();
                break;
        }
    }
}
