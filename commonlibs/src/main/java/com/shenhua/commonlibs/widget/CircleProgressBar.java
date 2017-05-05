package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.shenhua.commonlibs.utils.ConvertUtils;
import com.shenhua.libs.common.R;

/**
 * Created by shenhua on 5/5/2017.
 * Email shenhuanet@126.com
 */
public class CircleProgressBar extends View {

    private Paint paint;// 画笔对象
    private int width;// View宽度
    private int height;// View高度
    private int result = 0;// 默认宽高值
    private int padding = 0;// 默认padding值
    private int ringColor;// 圆环的颜色
    private int ringProgressColor;// 圆环进度颜色
    private int textColor;// 文字颜色
    private float textSize;// 文字大小
    private float ringWidth;// 圆环宽度
    private int max;// 最大值
    private int type;// 百分比样式
    private int progress;// 进度值
    private boolean textIsShow;// 是否显示文字
    private int style;// 圆环进度条的样式
    public static final int STROKE = 0;// 空心样式
    public static final int FILL = 1;// 实心样式
    public static final int PENCENT_TYPE = 0;// 百分比 20%
    public static final int QUOTIENT_TYPE = 1;// 分数比 3/4
    private OnProgressListener mOnProgressListener;// 进度回调接口
    private int centre;// 圆环中心
    private int radius;// 圆环半径

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        result = ConvertUtils.dp2px(100);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        ringColor = mTypedArray.getColor(R.styleable.CircleProgressBar_ringColor, Color.GRAY);
        ringProgressColor = mTypedArray.getColor(R.styleable.CircleProgressBar_ringProgressColor, Color.WHITE);
        textColor = mTypedArray.getColor(R.styleable.CircleProgressBar_textColor, Color.BLACK);
        textSize = mTypedArray.getDimension(R.styleable.CircleProgressBar_textSize, 16);
        ringWidth = mTypedArray.getDimension(R.styleable.CircleProgressBar_ringWidth, 5);
        max = mTypedArray.getInteger(R.styleable.CircleProgressBar_max, 100);
        textIsShow = mTypedArray.getBoolean(R.styleable.CircleProgressBar_textIsShow, true);
        style = mTypedArray.getInt(R.styleable.CircleProgressBar_style, 0);
        type = mTypedArray.getInt(R.styleable.CircleProgressBar_type, PENCENT_TYPE);
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centre = getWidth() / 2;
        radius = (int) (centre - ringWidth / 2);
        drawCircle(canvas);
        drawTextContent(canvas);
        drawProgress(canvas);
    }

    /**
     * 绘制外层圆环
     */
    private void drawCircle(Canvas canvas) {
        paint.setColor(ringColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ringWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, radius, paint);
    }

    /**
     * 绘制进度文本
     */
    private void drawTextContent(Canvas canvas) {
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT);
        if (type == QUOTIENT_TYPE) {
            String text = progress + "/" + max;
            float textWidth = paint.measureText(text);
            if (textIsShow && style == STROKE) {
                canvas.drawText(text, centre - textWidth / 2, centre + textSize / 2, paint);
            }
        } else {
            int percent = (int) (((float) progress / (float) max) * 100);
            float textWidth = paint.measureText(percent + "%");
            if (textIsShow && percent != 0 && style == STROKE) {
                canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, paint);
            }
        }
    }

    /**
     * 绘制进度条
     */
    private void drawProgress(Canvas canvas) {
        paint.setStrokeWidth(ringWidth);
        paint.setColor(ringProgressColor);
        // Stroke样式
        RectF strokeOval = new RectF(centre - radius, centre - radius, centre + radius,
                centre + radius);
        // Fill样式
        RectF fillOval = new RectF(centre - radius + ringWidth + padding,
                centre - radius + ringWidth + padding, centre + radius - ringWidth - padding,
                centre + radius - ringWidth - padding);
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawArc(strokeOval, -90, 360 * progress / max, false, paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                if (progress != 0) {
                    canvas.drawArc(fillOval, -90, 360 * progress / max, true, paint);
                }
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            width = result;
        } else {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = result;
        } else {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        padding = ConvertUtils.dp2px(5);
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The max progress of 0");
        }
        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("The progress of 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
        if (progress == max) {
            if (mOnProgressListener != null) {
                mOnProgressListener.progressToComplete();
            }
        }
    }

    public int getRingColor() {
        return ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    public int getRingProgressColor() {
        return ringProgressColor;
    }

    public void setRingProgressColor(int ringProgressColor) {
        this.ringProgressColor = ringProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    public interface OnProgressListener {
        void progressToComplete();
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }
}
