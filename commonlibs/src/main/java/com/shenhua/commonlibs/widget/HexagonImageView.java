package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 六边形ImageView
 * Created by Shenhua on 10/2/2016.
 */
public class HexagonImageView extends ImageView {

    private final Matrix mShaderMatrix = new Matrix();
    private final RectF mDrawableRect = new RectF();
    private Paint mBitmapPaint = new Paint();
    private Path mPath = new Path();
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private ColorFilter mColorFilter;
    private boolean mReady;
    private boolean mSetupPending;

    public HexagonImageView(Context context) {
        this(context, null);
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_CROP);
        if (getDrawable() != null)
            mBitmap = getBitmapFromDrawable(getDrawable());
        mReady = true;
        if (mSetupPending) {
            setupView();
            mSetupPending = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) return;
        canvas.drawPath(mPath, mBitmapPaint);
    }

    private void setupView() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) return;
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        mDrawableRect.set(0, 0, getWidth(), getHeight());
        /**
         * 正六边形的边长l就是宽的一半，正六边形的高是 Math.sqrt(3)*l ，<br>
         * 然后可以算出正六边形顶部的top值，之后只要依次遍历连接每个点，即可画出正六边形。
         */
        float l = mDrawableRect.width() / 2;
        float h = (float) (Math.sqrt(3) * l);
        float top = (mDrawableRect.height() - h) / 2;
        mPath.reset();
        mPath.moveTo(l / 2, top);
        mPath.lineTo(0, h / 2 + top);
        mPath.lineTo(l / 2, h + top);
        mPath.lineTo((float) (l * 1.5), h + top);
        mPath.lineTo(2 * l, h / 2 + top);
        mPath.lineTo((float) (l * 1.5), top);
        mPath.lineTo(l / 2, top);
        mPath.close();
        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;
        mShaderMatrix.set(null);
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.CENTER_CROP;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP)
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds)
            throw new IllegalArgumentException("adjustViewBounds not supported.Must be centerCrop");
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setupView();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setupView();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setupView();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setupView();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) return;
        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
