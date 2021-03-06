package com.yusys.mpos.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.yusys.mpos.R;
import com.yusys.mpos.base.manager.DeviceManager;
import com.yusys.mpos.login.ui.LoginActivity;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

import java.io.File;

/**
 * 应用启动界面
 *
 * @author yuanshuai (marshall.yuan@foxmail.com)
 * @since 2016-04-01
 */
public class StartActivity extends Activity {

    private static final int DELAY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_start, null);
        setContentView(view);
        // 渐变动画
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(DELAY);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectToLogin();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cacheVersion = PreferenceHelper.readInt(this, "first_install",
                "first_install", -1);
        int currentVersion = DeviceManager.getVersionCode();
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(this, "first_install", "first_install", currentVersion);
            cleanImageCache();
        }
    }

    private void cleanImageCache() {
        final File folder = FileUtils.getSaveFolder("YXmPos/imagecache");
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
            }
        });
    }

    /**
     * 跳转到...
     */

    private void redirectToLogin() {
//        Intent uploadLog = new Intent(this, LogUploadService.class);
//        startService(uploadLog);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}