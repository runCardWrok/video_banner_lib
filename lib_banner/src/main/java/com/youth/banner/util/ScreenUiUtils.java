package com.youth.banner.util;

import android.content.Context;

import androidx.annotation.NonNull;

public class ScreenUiUtils {

    /****
     * 根据宽度百分比与UI高度计算屏幕实际高度
     *
     *  int height = (int) (ConfigConst.screenWidth * 1.0f * 144 / 375);
     *
     * @param uiDp                  ui图上的高度
     * @param widthPercentage       ui图上的宽度占比
     * @param screenWidth           屏幕实际宽度
     *
     * @return 在屏幕上的高度
     */
    public static int uiDpToScreenPxForWidthPercentage(@NonNull int uiDp, @NonNull int widthPercentage, @NonNull int screenWidth) {
        return (int) (screenWidth * 1.0f * uiDp / widthPercentage);
    }

}
