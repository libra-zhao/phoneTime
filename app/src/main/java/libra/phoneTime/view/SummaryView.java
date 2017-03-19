/*
 * Copyright (C) 2017 by Libra Zhao <libra.zhao@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libra.phoneTime.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Calendar;

import libra.phoneTime.R;
import libra.phoneTime.db.ScreenOn;
import libra.phoneTime.lib.ZeroTime;

public class SummaryView extends AppCompatTextView {
    private final String TAG = this.getClass().getSimpleName();
    private final float DEF_SWEEP_ANGLE_MIN = 1.0f;
    private ArrayList<ScreenOn> mDatList;
    private Paint mPaintCircleBG;
    private Paint mPaintCircleFG;
    private ZeroTime mZeroTime;

    public SummaryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mZeroTime = new ZeroTime();

        Resources res = getResources();

        mPaintCircleBG = new Paint();
        mPaintCircleBG.setAntiAlias(true);
        mPaintCircleBG.setStyle(Paint.Style.STROKE);
        mPaintCircleBG.setStrokeWidth(res.getDimension(R.dimen.summary_circle_stroke_width));
        mPaintCircleBG.setColor(res.getColor(R.color.summary_circle_bg));

        mPaintCircleFG = new Paint();
        mPaintCircleBG.setAntiAlias(true);
        mPaintCircleFG.setStyle(Paint.Style.STROKE);
        mPaintCircleFG.setStrokeWidth(res.getDimension(R.dimen.summary_circle_stroke_width));
        mPaintCircleFG.setColor(res.getColor(R.color.summary_circle_fg));
    }

    public void setValue(long secs) {
        String value = "";

        if (secs >= 3600) {
            value += String.valueOf(secs/3600)+":";
        }
        if (secs >= 60) {
            value += String.valueOf((secs%3600)/60)+":";
        }
        value += secs % 60;

        setText(value);
    }

    public void setDataList(ArrayList<ScreenOn> list) {
        mDatList = list;
    }

    protected void onDraw(Canvas canvas) {
        float width, height;
        float radius, startAG, sweepAG;
        RectF rectCircle;

        // Draw the background for this view
        super.onDraw(canvas);

        mZeroTime.adjust();

        width = canvas.getWidth();
        height = canvas.getHeight();
        radius = (width > height ? height : width) / 2 * 0.9f;
        rectCircle = new RectF(width / 2 - radius, height / 2 - radius,
                width / 2 + radius, height / 2 + radius);

        canvas.drawArc(rectCircle, 0, 360, false, mPaintCircleBG);

        if (mDatList != null) {
            for (ScreenOn so : mDatList) {
                startAG = getAngle(so.getStartTime());
                sweepAG = getAngle(so.getEndTime()) - startAG;
                if (sweepAG >= DEF_SWEEP_ANGLE_MIN) {
                    canvas.drawArc(rectCircle, startAG, sweepAG, false, mPaintCircleFG);
                }
            }
        }
    }

    private float getAngle(Calendar calendar) {
        long ms = calendar.getTimeInMillis();
        return (ms - mZeroTime.getTimeInMillis()) * 360.0f / (24 * 3600 * 1000) + 90;
    }
}
