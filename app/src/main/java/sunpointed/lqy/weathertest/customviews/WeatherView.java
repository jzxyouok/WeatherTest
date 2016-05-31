package sunpointed.lqy.weathertest.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import sunpointed.lqy.weathertest.R;

/**
 * Created by lqy on 16/5/30.
 */
public class WeatherView extends View implements SensorEventListener {

    public static final int SUNSHINE = 0;
    public static final int CLOUD_SUN = 1;
    public static final int CLOUDY = 2;
    public static final int RAIN = 3;

    private static final int LINE_COUNT = 50;
    private static final int ANGLE = 360 / 9;
    private static final int COLOR_ARRAY[] = {
            0xFFFF5722, 0xFFFF6722, 0xFFFF7722, 0xFFFF8722,
            0xFFFF9722, 0xFFFFA722, 0xFFFFB722, 0xFFFFC722,
            0xFFFFD722};

    int mWeatherStyle;

    Paint mPaint;

    int mWidth;
    int mHeight;

    Path mSunshinePath;
    int mSunshineX[];
    int mSunshineY[];
    float mSunshineStartAngle;
    int mSunshineLenthX;
    int mSunshineLenthY;
    int mLightAngle;

    float mRainCenterX;
    float mRainCenterY;
    int mRainCount;
    int mRainPositionX[];
    int mRainPositionY[];
    int mRainLineNow[];
    int mRainLineCount[];

    float mOthersX;
    float mOthersY;

    SensorManager mManger;
    Sensor mSensor;

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherView);

        mWeatherStyle = typedArray.getInt(R.styleable.WeatherView_weather, 0);

        mRainCount = typedArray.getInt(R.styleable.WeatherView_rain_count, 100);
        mRainPositionX = new int[mRainCount];
        mRainPositionY = new int[mRainCount];
        mRainLineNow = new int[mRainCount];
        mRainLineCount = new int[mRainCount];

        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSunshinePath = new Path();

        mSunshineX = new int[9];
        mSunshineY = new int[9];
        mSunshineStartAngle = (float) (Math.random() * 360);
        mSunshineLenthX = 0;
        mSunshineLenthY = 0;
        mLightAngle = 60;

        mManger = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mWeatherStyle == SUNSHINE) {
            drawSunshine(canvas);
        } else if (mWeatherStyle == CLOUD_SUN) {
            drawCloudSun(canvas);
        } else if (mWeatherStyle == CLOUDY) {
            drawCloudy(canvas);
        } else if (mWeatherStyle == RAIN) {
            drawRain(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mOthersX = 0;
        mOthersY = mHeight / 3;
        mRainCenterX = mWidth / 2;
        mRainCenterY = mHeight / 2;
        for (int i = 0; i < mRainCount; i++) {
            mRainPositionX[i] = (int) (Math.random() * mWidth);
            mRainPositionY[i] = (int) (Math.random() * mHeight);
            mRainLineNow[i] = (int) (Math.random() * mRainCount);
            mRainLineCount[i] = LINE_COUNT + (int) (Math.random() * mRainCount);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];

        if(mWeatherStyle == RAIN) {
            mRainCenterX -= (int) x;
            mRainCenterY += (int) y;
        }else if(mWeatherStyle == SUNSHINE) {
            int pX = mSunshineLenthX;
            mSunshineLenthX -= (int) x;
            if (mSunshineLenthX < 0) {
                mSunshineLenthX = 0;
            } else if (mSunshineLenthX > mWidth) {
                mSunshineLenthX = mWidth;
            }
            mSunshineLenthY += (int) y;
            if (mSunshineLenthY < 0) {
                mSunshineLenthY = 0;
            } else if (mSunshineLenthY > mHeight / 2) {
                mSunshineLenthY = mHeight / 2;
            }
            mSunshineStartAngle += 0.2;
            mLightAngle += (float)(mSunshineLenthX - pX)/mWidth * 60;
        }else if(mWeatherStyle == CLOUD_SUN){

        }else if(mWeatherStyle == CLOUDY){

        }

        postInvalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void drawSunshine(Canvas canvas) {
        mPaint.setColor(0xFF03A9F4);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);

        int pRadius = mWidth / 7 + mWidth / 10 * 8;
        int pX = mWidth + mSunshineLenthX;
        int pY = 0 + mSunshineLenthY;
        float sAngle = mSunshineStartAngle;
        int alpha = 60;

        //9个9边形组成太阳效果
        for (int i = 8; i > -1; i--) {
            mPaint.setColor(COLOR_ARRAY[i]);
            mPaint.setAlpha(alpha);
            for (int j = 0; j < 9; j++) {
                mSunshineX[j] = (int) (pX + pRadius * Math.cos((sAngle + ANGLE * j) * Math.PI / 180));
                mSunshineY[j] = (int) (pY + pRadius * Math.sin((sAngle + ANGLE * j) * Math.PI / 180));
                if (j == 0) {
                    mSunshinePath.moveTo(mSunshineX[j], mSunshineY[j]);
                } else {
                    mSunshinePath.lineTo(mSunshineX[j], mSunshineY[j]);
                }
            }
            canvas.drawPath(mSunshinePath, mPaint);
            alpha += 195 / 10;
            pRadius -= mWidth / 10;
            sAngle += ANGLE / 2;
            mSunshinePath.reset();
        }

        pRadius = mWidth / 7 + mWidth / 10 * 4;
        mPaint.setAlpha(255);
        mPaint.setColor(0xFFFFFFFF);
        pX = (int) (pX - pRadius * Math.cos(mLightAngle * Math.PI / 180));
        pY = (int) (pY + pRadius * Math.sin(mLightAngle * Math.PI / 180));
        for (int i = 0; i < 3; i++) {
            pRadius = mWidth / ((3 - i) * 10);
            for (int j = 0; j < 6; j++) {
                mSunshineX[j] = (int) (pX + pRadius * Math.cos((sAngle + mLightAngle * j) * Math.PI / 180));
                mSunshineY[j] = (int) (pY + pRadius * Math.sin((sAngle + mLightAngle * j) * Math.PI / 180));
                if (j == 0) {
                    mSunshinePath.moveTo(mSunshineX[j], mSunshineY[j]);
                } else {
                    mSunshinePath.lineTo(mSunshineX[j], mSunshineY[j]);
                }
            }
            pX -= mWidth / 5 * (i + 1) * Math.cos(mLightAngle * Math.PI / 180);
            pY += mWidth / 5 * (i + 1) * Math.sin(mLightAngle * Math.PI / 180);
            canvas.drawPath(mSunshinePath, mPaint);
            sAngle += ANGLE / 2;
            mSunshinePath.reset();
        }
    }

    private void drawCloudSun(Canvas canvas) {
        // TODO: 16/5/30
    }

    private void drawCloudy(Canvas canvas) {
        // TODO: 16/5/30
    }

    private void drawRain(Canvas canvas) {
        mPaint.setColor(0xFF303F9F);
        mPaint.setStrokeWidth(3);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        float pX = 0, pY = 0, aX = 0, aY = 0;
        for (int i = 0; i < mRainCount; i++) {
            if (i % 2 == 0) {
                mPaint.setColor(0xFF448AFF);
            } else {
                mPaint.setColor(0xFFFF9800);
            }

            float dx = (mRainPositionX[i] - mRainCenterX) / mRainLineCount[i];
            float dy = (mRainPositionY[i] - mRainCenterY) / mRainLineCount[i];

            pX = mRainPositionX[i] - dx * (mRainLineNow[i]);
            pY = mRainPositionY[i] - dy * (mRainLineNow[i]);

            aX = pX - dx;
            aY = pY - dy;

            canvas.drawLine(pX, pY, aX, aY, mPaint);
        }
        for (int i = 0; i < mRainCount; i++) {
            if (mRainLineNow[i] > mRainLineCount[i] - LINE_COUNT / 2) {
                mRainLineNow[i] = 1;
            } else {
                mRainLineNow[i]++;
            }
        }
    }

    public void onResume() {
        if (mManger != null) {
            mManger.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void onPause() {
        if (mManger != null) {
            mManger.unregisterListener(this);
        }
    }

    public void setWeatherStyle(int weatherStyle) {
        mWeatherStyle = weatherStyle;
        postInvalidate();
    }
}
