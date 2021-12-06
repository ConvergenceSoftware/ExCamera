package com.convergence.excamera.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.convergence.excamera.R;
import com.convergence.excamera.activity.camera.UsbMicroCamActivity;
import com.convergence.excamera.activity.camera.UsbTeleCamActivity;
import com.convergence.excamera.activity.camera.WifiMicroCamActivity;
import com.convergence.excamera.activity.camera.WifiTeleCamActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.permissionx.guolindev.PermissionX;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_micro_usb_connect_activity_main)
    TextView tvMicroUsbConnectActivityMain;
    @BindView(R.id.tv_micro_wifi_connect_activity_main)
    TextView tvMicroWifiConnectActivityMain;
    @BindView(R.id.tv_tele_usb_connect_activity_main)
    TextView tvTeleUsbConnectActivityMain;
    @BindView(R.id.tv_tele_wifi_connect_activity_main)
    TextView tvTeleWifiConnectActivityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                .transparentBar()
                //状态栏字体是深色，不写默认为亮色
                .statusBarDarkFont(true)
                //导航栏图标是深色，不写默认为亮色
                .navigationBarDarkIcon(true)
                .init();
    }

    private void startActivity(Class<?> cls) {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        Intent intent = new Intent(this, cls);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick({R.id.tv_micro_usb_connect_activity_main, R.id.tv_micro_wifi_connect_activity_main, R.id.tv_tele_usb_connect_activity_main, R.id.tv_tele_wifi_connect_activity_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_micro_usb_connect_activity_main:
                startActivity(UsbMicroCamActivity.class);
                break;
            case R.id.tv_micro_wifi_connect_activity_main:
                startActivity(WifiMicroCamActivity.class);
                break;
            case R.id.tv_tele_usb_connect_activity_main:
                startActivity(UsbTeleCamActivity.class);
                break;
            case R.id.tv_tele_wifi_connect_activity_main:
                startActivity(WifiTeleCamActivity.class);
                break;
            default:
                break;
        }
    }
}