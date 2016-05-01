package com.roodie.materialmovies.views.custom_views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 14.03.2016.
 */
public class GradientView extends View {
    private GradientDrawable drawable;
    private int[] colors;
    private GradientDrawable.Orientation orientation;

    public GradientView(Context context) {
        super(context);
        this.orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        this.colors = new int[] {16777216, -1};
        this.refreshDrawable();

    }

    public GradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int angle;
        if (!this.isInEditMode()) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientView);
            this.colors = new int[] { a.getColor( R.styleable.GradientView_startColor, 16777216), a.getColor( R.styleable.GradientView_endColor, -1) };
            angle = (int)a.getFloat(R.styleable.GradientView_angle, 0.0f);
            a.recycle();
        } else {
            this.colors = new int[] {16777216, -1};
            angle = 0;
        }

        switch (angle / 90) {
            default: {
                this.orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            }
            case 0: {
                this.orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            }
            case 1: {
                this.orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            }
            case 2: {
                this.orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            }
            case 3: {
                this.orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            }
        }
        this.drawable = null;
        this.refreshDrawable();
    }

    @TargetApi(16)
    private void refreshDrawable() {
        if (Build.VERSION.SDK_INT < 16) {
            this.setBackgroundDrawable(null);
            return;
        }
        this.setBackground(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.drawable == null) {
            this.drawable = new GradientDrawable(this.orientation, this.colors);
        }
        this.drawable.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.drawable == null) {
            this.drawable = new GradientDrawable(this.orientation, this.colors);
        }
        this.drawable.setBounds(0, 0, right - left, bottom - top);
        super.onLayout(changed, left, top, right, bottom);
    }
}
