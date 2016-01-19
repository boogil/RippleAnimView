package mylibrary.com.appknot.rippleanimationview_gil_master;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Appknot on 2016-01-13.
 */
public class RippleAnimView extends View implements Runnable{

    private int cvAlpha;        //초기 알바값
    private float cvMaxRadius; // 최대 반지름
    private float durationTime;

    private Paint paint,paint2;

    private float radius = 0; //반지름 초기값
    private float radius2 = 0; //2번째 원 반지름 초기값

    private int cv_circleStyle=0;

    private float alpha; //알파 초기값
    private float alpha2; //2번째 원 알파 초기값

    private float intervalRadius; // 커지는 반지름 단위
    private float intervalAlpha; // 투명 애니메이션 단위

    private float intervalTime=10; //애니메이션 갱신 시간 (0.01초마다 갱신)

    boolean secondThreadBool = false;

    public RippleAnimView(Context context) {
        super(context);
    }

    public RippleAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(final Context context, final AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.RippleAnimView);
        cvAlpha = typedArray.getInt(R.styleable.RippleAnimView_rv_alpha, 255);     //처음 원의 알파값
        cvMaxRadius = typedArray.getFloat(R.styleable.RippleAnimView_rv_maxRadius, 100);  //원의 최대 크기
        durationTime = typedArray.getFloat(R.styleable.RippleAnimView_rv_durationTime, 1000);  //원 하나의 애니메이션 작동 시간
        cv_circleStyle = typedArray.getInteger(R.styleable.RippleAnimView_rv_circleStyle, 0);  //원 스타일 (0: Stroke, 1: FIll )
        typedArray.recycle();

        alpha = cvAlpha;
        alpha2 = cvAlpha;

        intervalRadius = (cvMaxRadius / (durationTime/intervalTime));
        intervalAlpha = cvAlpha / ((durationTime/2)/intervalTime);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(7);
        paint.setAlpha((int) alpha);
        if(cv_circleStyle == 0) {
            paint.setStyle(Paint.Style.STROKE);
        }else{
            paint.setStyle(Paint.Style.FILL);
        }

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setColor(Color.GRAY);
        paint2.setStrokeWidth(7);
        paint2.setAlpha((int) alpha2);
        if(cv_circleStyle == 0) {
            paint2.setStyle(Paint.Style.STROKE);
        }else{
            paint2.setStyle(Paint.Style.FILL);
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);


        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius2, paint2);

    }

    @Override
    public void run() {
            while(true){

                try {
                    Thread.sleep((long) intervalTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                radius = radius + intervalRadius;

                postInvalidate();

                if(radius>cvMaxRadius/2){
                    alpha = alpha - intervalAlpha;

                    if(secondThreadBool==false) {
                        secondThreadBool =true;
                            /*새로운 원 생성*/
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        while (true) {
                                            try {
                                                Thread.sleep((long) intervalTime);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            radius2 = radius2 + intervalRadius;
                                            Log.i("boogil6", "radius2:" + radius2);

                                            postInvalidate();

                                            if (radius2 > cvMaxRadius / 2) {
                                                alpha2 = alpha2 - intervalAlpha;
                                            }

                                            if (radius2 >= cvMaxRadius-1) {
                                                radius2 = 0;
                                                alpha2 = cvAlpha;

                                                secondThreadBool = false;
                                                break;
                                            }
                                        }
                                    }
                                }).start();
                            }

                }

                if(radius >= cvMaxRadius-1){ //
                    radius = 0;
                    alpha = cvAlpha;
                    break;
                }
            }

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                new Thread(this).start();

                break;
        }

        return super.onTouchEvent(event);
    }
}
