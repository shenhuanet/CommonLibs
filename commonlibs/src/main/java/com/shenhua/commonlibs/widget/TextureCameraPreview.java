package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.Toast;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public class TextureCameraPreview extends TextureView implements TextureView.SurfaceTextureListener, Camera.PictureCallback, Camera.ShutterCallback {

    private Camera mCamera;
    private ToneGenerator tone;
    private OnPictureTakenListener onPictureTakenListener;

    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private OnStartVideoListener onStartVideoListener;

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
        // 1.打开相机
        mCamera = getCameraInstance(getContext());
        if (mCamera != null) {
            // 2.设置相机参数
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(1920, 1080);
            parameters.setPictureSize(1920, 1080);
            mCamera.setParameters(parameters);
            // 3.开始相机预览
            try {
                mCamera.setPreviewTexture(surface);
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
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public static Camera getCameraInstance(Context context) {
        Camera c = null;
        try {
            c = Camera.open(0);
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

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        if (onPictureTakenListener != null) {
            onPictureTakenListener.onSuccess(data);
        }
    }

    @Override
    public void onShutter() {
        if (tone == null)
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
        tone.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    public interface OnPictureTakenListener {
        void onSuccess(byte[] data);

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
