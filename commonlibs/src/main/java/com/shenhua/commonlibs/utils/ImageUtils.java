package com.shenhua.commonlibs.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.shenhua.commonlibs.handler.BaseThreadHandler;
import com.shenhua.commonlibs.handler.CommonRunnable;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 处理图片的工具类
 * Created by shenhua on 8/22/2016.
 */
public class ImageUtils {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 保存字节图片
     *
     * @return 返回保存成功后的绝对路径
     */
    public static String saveBytesImage(Context context, byte[] bytes, String title, String dirName, boolean shouldRefreshGallery) throws Exception {
        File dir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, title);
        if (!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        if (shouldRefreshGallery)
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dirName + file.getAbsolutePath())));
        return file.getAbsolutePath();
    }

    /**
     * 保存图片到手机存储
     *
     * @param context              上下文
     * @param bitmap               bitmap对象
     * @param title                文件名
     * @param dirName              文件夹名称
     * @param shouldRefreshGallery 是否刷新图库
     * @return 返回保存成功后的绝对路径
     * @throws Exception e
     */
    public static String saveBitmapImage(Context context, Bitmap bitmap, String title, String dirName, boolean shouldRefreshGallery) throws Exception {
        File dir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, title);
        if (!file.exists()) file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        if (bitmap == null) throw new Exception("bitmap is null");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        if (shouldRefreshGallery)
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dirName + file.getAbsolutePath())));
        return file.getAbsolutePath();
    }

    /**
     * 翻转图片
     * 左右翻转 传递值为（bitmap,-1,1）上下翻转传递值为 ( bitmap,1,-1)
     *
     * @param srcBitmap bitmap
     * @param sx        x
     * @param sy        y
     * @return bitmap
     */
    public static Bitmap reversalBitmap(Bitmap srcBitmap, float sx, float sy) {
        Bitmap cacheBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int w = cacheBitmap.getWidth();
        int h = cacheBitmap.getHeight();
        Canvas canvas = new Canvas(cacheBitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, matrix, true);
        canvas.drawBitmap(bitmap, new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()), new Rect(0, 0, w, h), null);
        return bitmap;
    }

    /**
     * Glide 加载图片
     *
     * @param context   context
     * @param imgUrl    url
     * @param photoView target imageview
     */
    public static void loadImageWithGlide(Context context, String imgUrl, ImageView photoView) {
        loadImageWithGlide(context, imgUrl, photoView, null);
    }

    /**
     * Glide 加载图片
     *
     * @param context     context
     * @param imgUrl      url
     * @param photoView   target imageview
     * @param progressBar progressBar
     */
    public static void loadImageWithGlide(Context context, String imgUrl, ImageView photoView, final ProgressBar progressBar) {
        if (imgUrl.endsWith("gif")) {
            Glide.with(context).load(imgUrl).asGif().crossFade().fitCenter().into(new Target<GifDrawable>() {
                @Override
                public void onLoadStarted(Drawable placeholder) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadCleared(Drawable placeholder) {

                }

                @Override
                public void getSize(SizeReadyCallback cb) {

                }

                @Override
                public void setRequest(Request request) {

                }

                @Override
                public Request getRequest() {
                    return null;
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {

                }

                @Override
                public void onDestroy() {

                }
            });
        } else {
            Glide.with(context).load(imgUrl).crossFade().fitCenter().into(new GlideDrawableImageViewTarget(photoView) {
                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    if (progressBar != null)
                        progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                    super.onResourceReady(resource, animation);
                    if (progressBar != null)
                        progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    if (progressBar != null)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    /**
     * 使用Glide下载图片
     *
     * @param context context
     * @param imgUrl  url
     * @param dir     保存目录
     */
    public static void downLoadImage(final Context context, final String dir, final String imgUrl) {
        downLoadImage(context, imgUrl, dir, true);
    }

    /**
     * 使用Glide下载图片
     *
     * @param context   context
     * @param imgUrl    url
     * @param dir       保存目录
     * @param useDialog 是否显示进度条,默认使用
     */
    public static void downLoadImage(final Context context, final String imgUrl, final String dir, final boolean useDialog) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("图片保存中...");
        if (useDialog) {
            dialog.show();
        }
        BaseThreadHandler.getInstance().sendRunnable(new CommonRunnable<String>() {

            @Override
            public String doChildThread() {
                try {
                    Bitmap b = Glide.with(context).load(imgUrl).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    String title = imgUrl.substring(1 + imgUrl.lastIndexOf("/"), imgUrl.length());
                    return saveBitmapImage(context, b, title, dir, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void doUiThread(String s) {
                if (useDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (s == null) {
                    Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "图片已保存到：" + s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 普通下载图片
     *
     * @param context context
     * @param imgUrl  url
     * @param dir     保存目录
     */
    public static void downLoadImageDefault(final Context context, final String imgUrl, final String dir) {
        downLoadImageDefault(context, imgUrl, dir, false);
    }

    /**
     * 普通下载图片
     *
     * @param context   context
     * @param imgUrl    url
     * @param dir       保存目录
     * @param useDialog 是否使用进度条,默认不使用
     */
    public static void downLoadImageDefault(final Context context, final String imgUrl, final String dir, final boolean useDialog) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("图片保存中...");
        if (useDialog) {
            dialog.show();
        }
        Glide.with(context).load(imgUrl).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                if (useDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    String title = imgUrl.substring(1 + imgUrl.lastIndexOf("/"), imgUrl.length());
                    String result = saveBytesImage(context, bytes, title, dir, true);
                    Toast.makeText(context, "图片已保存到：" + result, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
