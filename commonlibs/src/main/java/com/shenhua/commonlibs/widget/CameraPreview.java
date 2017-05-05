package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.shenhua.commonlibs.handler.BaseThreadHandler;
import com.shenhua.commonlibs.handler.CommonRunnable;
import com.shenhua.commonlibs.utils.ImageUtils;

import java.io.IOException;

/**
 * Created by shenhua on 12/14/2016.
 * Email shenhuanet@126.com
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.ShutterCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ToneGenerator tone;
    private OnPictureTakenListener listener;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onResume() {
        onDestroy();
        if (mCamera == null)
            mCamera = getCameraInstance(getContext());
        if (mCamera != null) {
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(1920, 1080);
            parameters.setPictureSize(1920, 1080);
            mCamera.setParameters(parameters);
        }
    }

    public void setZoom(int progress) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setZoom((int) (progress * 1.0f / (40 * 100) * 40));
            mCamera.setParameters(parameters);
        }
    }

    public void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void setCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    public static Camera getCameraInstance(Context context) {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            Toast.makeText(context, "相机打开失败", Toast.LENGTH_SHORT).show();
        }
        return c; // returns null if camera is unavailable
    }

    public Surface getSHolder() {
        return mHolder.getSurface();
    }

    public Camera getmCamera() {
        return mCamera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("shenhua sout:" + "Error setting camera preview: " + e.getMessage());
            Toast.makeText(getContext(), "Error setting camera preview:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mHolder.getSurface() == null)
            return;
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("shenhua sout:" + "Error starting camera preview: " + e.getMessage());
            Toast.makeText(getContext(), "Error starting camera preview: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public void takePicture() throws Exception {
        if (mCamera != null) {
            mCamera.takePicture(this, null, this);
        }
    }

    public void takePicture(OnPictureTakenListener listener) throws Exception {
        this.listener = listener;
        if (mCamera != null) {
            mCamera.takePicture(this, null, this);
        }
    }

    public interface OnPictureTakenListener {
        void onSuccess(String filePath);

        void onFailed(String msg);
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        BaseThreadHandler.getInstance().sendRunnable(new CommonRunnable<String>() {
            @Override
            public String doChildThread() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    return ImageUtils.saveBitmapImage(getContext(), bitmap, "1111", "shenhua-lib", true);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void doUiThread(String s) {
                if (s == null) {
                    listener.onFailed("save picture failed.");
                } else {
                    listener.onSuccess("saved:" + s);
                }
            }
        });
    }

    @Override
    public void onShutter() {
        if (tone == null)
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
        tone.startTone(ToneGenerator.TONE_PROP_BEEP);
    }
}
