package com.shenhua.commonlibs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shenhua.libs.common.R;

/**
 * 屏幕工具类
 * 获取屏幕宽高等信息、全屏切换、保持屏幕常亮、截屏等
 * Created by shenhua on 8/22/2016.
 */
public class ScreenUtils {

    private static boolean isFullScreen = false;
    private static final String TAG = "ScreenUtils";

    /**
     * 获取状态栏的高度
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) result = context.getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    /**
     * 获取状态栏高度
     *
     * @param activity activity
     * @return 高度 px
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取ToolBar的高度
     *
     * @param context 上下文
     * @return ToolBar高度
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }

    /**
     * 获取NavigationBar的高度
     *
     * @param activity activity
     * @return NavigationBar高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int rid = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) LogUtils.verbose(TAG, "导航栏是否显示?" + resources.getBoolean(rid));
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) return resources.getDimensionPixelSize(resourceId);
        return 0;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    /**
     * Display metrics display metrics.
     *
     * @param context the context
     * @return the display metrics
     */
    public static DisplayMetrics displayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        LogUtils.verbose("screen width=" + dm.widthPixels + "px, screen height=" + dm.heightPixels
                + "px, densityDpi=" + dm.densityDpi + ", density=" + dm.density);
        return dm;
    }

    /**
     * Width pixels int.
     *
     * @param context the context
     * @return the int
     */
    public static int widthPixels(Context context) {
        return displayMetrics(context).widthPixels;
    }

    /**
     * Height pixels int.
     *
     * @param context the context
     * @return the int
     */
    public static int heightPixels(Context context) {
        return displayMetrics(context).heightPixels;
    }

    /**
     * Density float.
     *
     * @param context the context
     * @return the float
     */
    public static float density(Context context) {
        return displayMetrics(context).density;
    }

    /**
     * Density dpi int.
     *
     * @param context the context
     * @return the int
     */
    public static int densityDpi(Context context) {
        return displayMetrics(context).densityDpi;
    }

    /**
     * Is full screen boolean.
     *
     * @return the boolean
     */
    public static boolean isFullScreen() {
        return isFullScreen;
    }

    /**
     * Toggle full displayMetrics.
     *
     * @param activity the activity
     */
    public static void toggleFullScreen(Activity activity) {
        Window window = activity.getWindow();
        int flagFullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (isFullScreen) {
            window.clearFlags(flagFullscreen);
            isFullScreen = false;
        } else {
            window.setFlags(flagFullscreen, flagFullscreen);
            isFullScreen = true;
        }
    }

    /**
     * 保持屏幕常亮
     *
     * @param activity the activity
     */
    public static void keepBright(Activity activity) {
        //需在setContentView前调用
        int keepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        activity.getWindow().setFlags(keepScreenOn, keepScreenOn);
    }

    /**
     * 对view进行截图
     *
     * @param view view
     * @return bitmap
     */
    public static Bitmap viewShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();// 启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());// 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);// 禁用DrawingCache否则会影响性能
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 对屏幕进行截图，不包含状态栏
     * tips: 当对整个view或layout截图时，由于控件与控件之间是透明的，而本实例图片保存类型为png
     * 所以截取后图片出现黑色背景，解决的方法是对view或layout加上background.
     *
     * @param activity activity
     * @return bitmap
     */
    public static Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        int h = getStatusBarHeight(activity);
        Display display = activity.getWindowManager().getDefaultDisplay();
        int widths = display.getWidth();// 获取屏幕宽和高
        int heights = display.getHeight();
        view.setDrawingCacheEnabled(true);// 允许当前窗口保存缓存信息
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, h, widths, heights - h);// 去掉状态栏
        view.destroyDrawingCache();// 销毁缓存信息
        return bmp;
    }
}
