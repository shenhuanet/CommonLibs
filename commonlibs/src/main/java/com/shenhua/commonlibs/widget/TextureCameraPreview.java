package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.Toast;

import com.shenhua.commonlibs.utils.ImageUtils;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public class TextureCameraPreview extends TextureView implements TextureView.SurfaceTextureListener, Camera.PictureCallback, Camera.ShutterCallback {

    private Camera mCamera;
    private int index = 0;
    private OnPictureTakenListener onPictureTakenListener;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private OnStartVideoListener onStartVideoListener;
    private SurfaceTexture mSurface;

    public TextureCameraPreview(Context context) {
        this(context, null);
    }

    public TextureCameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = surface;
        openCamera();
    }

    private void openCamera() {
        // 1.打开相机
        mCamera = getCameraInstance(getContext(), index);
        if (mCamera != null) {
            // 2.设置相机参数
            Camera.Parameters parameters = mCamera.getParameters();
            // 3.调整预览方向
            mCamera.setDisplayOrientation(90);
            // 4.设置预览尺寸
            parameters.setPreviewSize(1920, 1080);
            parameters.setPictureSize(1920, 1080);
            // 5.调整拍照图片方向
            if (index == 0)
                parameters.setRotation(90);
            if (index == 1)
                parameters.setRotation(270);
            mCamera.setParameters(parameters);
            // 6.开始相机预览
            try {
                mCamera.setPreviewTexture(mSurface);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error setting camera preview:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseCamera();
        return true;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public static Camera getCameraInstance(Context context, int i) {
        Camera c = null;
        try {
            c = Camera.open(i);
        } catch (Exception e) {
            Toast.makeText(context, "相机打开失败", Toast.LENGTH_SHORT).show();
        }
        return c;
    }

    public void takePicture(OnPictureTakenListener listener) {
        if (listener != null) {
            onPictureTakenListener = listener;
            if (mCamera != null) {
                mCamera.takePicture(this, null, this);
            } else {
                onPictureTakenListener.onFailed("拍照失败");
            }
        }
    }

    public void setZoom(int progress) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setZoom((int) (progress * 1.0f / (40 * 100) * 40));
            mCamera.setParameters(parameters);
        }
    }

    public void switchCamera(int index) {
        if (isRecording) {
            stopRecord();
        }
        releaseCamera();
        this.index = index;
        openCamera();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (index == 1) {
            bitmap = ImageUtils.reversalBitmap(bitmap, -1, 1);
        }
        if (onPictureTakenListener != null) {
            onPictureTakenListener.onSuccess(bitmap);
        }

        // 使拍照结束后重新预览
        releaseCamera();
        openCamera();
    }

    @Override
    public void onShutter() {

    }

    public interface OnPictureTakenListener {
        void onSuccess(Bitmap bitmap);

        void onFailed(String msg);
    }

// ---------------- 录像

    private boolean initMediaRecorder(String outputPath) {
        mMediaRecorder = new MediaRecorder();
        if (mCamera != null) {
            // 1.解锁并将相机设置daoMediaRecorder
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            // 2.设置资源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 3.设置CamcorderProfile（需要API级别8或更高版本）
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            // 4.设置输出文件
            mMediaRecorder.setOutputFile(outputPath);
            // 5.准备配置的MediaRecorder
            try {
                mMediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "录像配置准备失败", Toast.LENGTH_SHORT).show();
                releaseMediaRecorder();
                return false;
            }
            return true;
        }
        return false;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void startRecord(String outputPath, OnStartVideoListener listener) {
        this.onStartVideoListener = listener;
        if (initMediaRecorder(outputPath)) {
            new MediaPrepareTask(listener).execute();
        } else {
            Toast.makeText(getContext(), "开始录制视频失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopRecord() {
        if (isRecording) {
            mMediaRecorder.stop();
            releaseMediaRecorder();
            mCamera.lock();
            if (onStartVideoListener != null)
                onStartVideoListener.onStop();
        } else {
            releaseMediaRecorder();
        }
        isRecording = false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        OnStartVideoListener listener;

        MediaPrepareTask(OnStartVideoListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mMediaRecorder.start();
            isRecording = true;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (listener != null)
                listener.onStart();
        }
    }

    public interface OnStartVideoListener {
        void onStart();

        void onStop();
    }

}
