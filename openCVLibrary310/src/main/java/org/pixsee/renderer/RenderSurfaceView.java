package org.pixsee.renderer;

import android.content.Context;
import android.util.AttributeSet;

import org.rajawali3d.view.SurfaceView;

/**
 * Created by Tudor on 2016-05-24.
 */

/**
 * A {@link SurfaceView} that can be adjusted to a specified aspect ratio.
 */
public class RenderSurfaceView extends SurfaceView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public RenderSurfaceView(Context context) {
        super(context);
    }

    public RenderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(final int width, final int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
