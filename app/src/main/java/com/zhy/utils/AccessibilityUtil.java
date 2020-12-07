package com.zhy.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.zhy.service.FloatingService;

/**
 * @author:yhz
 * @time:2020/12/7
 * @email:309581534@qq.com
 * @describe:
 */
public class AccessibilityUtil {

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled;
        final String service = context.getPackageName() + "/" + FloatingService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            if (accessibilityEnabled == 1) {
                String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    mStringColonSplitter.setString(settingValue);
                    while (mStringColonSplitter.hasNext()) {
                        String accessibilityService = mStringColonSplitter.next();
                        if (accessibilityService.equalsIgnoreCase(service)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
