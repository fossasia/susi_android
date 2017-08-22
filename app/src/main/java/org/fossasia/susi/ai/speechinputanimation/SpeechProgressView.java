package org.fossasia.susi.ai.speechinputanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SpeechProgressView extends View {

    public static final int BARS_COUNT = 5;

    private static final int CIRCLE_RADIUS_DP = 3;
    private static final int CIRCLE_SPACING_DP = 8;
    private static final int ROTATION_RADIUS_DP = 20;
    private static final int IDLE_FLOATING_AMPLITUDE_DP = 3;

    private static final int[] DEFAULT_BARS_HEIGHT_DP = {40, 30, 50, 35, 46};

    private static final float MDPI_DENSITY = 1.5f;

    private final List<SpeechBar> speechBars = new ArrayList<>();
    private Paint paint;
    private BarParamsAnimator animator;

    private int radius;
    private int spacing;
    private int rotationRadius;
    private int amplitude;

    private float density;

    private boolean isSpeaking;
    private boolean animating;

    private int barColor = -1;

    private int[] barColors = {
            Color.parseColor("#4184f3"),
            Color.parseColor("#BDBDBD"),
            Color.parseColor("#0000D4"),
            Color.parseColor("#78909C"),
            Color.parseColor("#0091EA")
    };

    private int[] barMaxHeights = {40, 48, 38, 50, 34};

    public SpeechProgressView(Context context) {
        super(context);
        init();
    }

    public SpeechProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeechProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Starts animating view
     */
    public void play() {
        startIdleInterpolation();
        animating = true;
    }

    /**
     * Stops animating view
     */
    public void stop() {
        if (animator != null) {
            animator.stop();
            animator = null;
        }
        isSpeaking = false;
        animating = false;
        resetBars();
    }

    /**
     * Set one color to all bars in view
     * @param color bar color
     */
    public void setSingleColor(int color) {
        barColor = color;
    }

    /**
     * Set different colors to bars in view
     *
     * @param colors - array with size = {@link #BARS_COUNT}
     */
    public void setColors(int[] colors) {
        if (colors == null) return;

        barColors = new int[BARS_COUNT];
        if (colors.length < BARS_COUNT) {
            System.arraycopy(colors, 0, barColors, 0, colors.length);
            for (int i = colors.length; i < BARS_COUNT; i++) {
                barColors[i] = colors[0];
            }
        } else {
            System.arraycopy(colors, 0, barColors, 0, BARS_COUNT);
        }
    }

    /**
     * Set sizes of bars in view
     *
     * @param heights - array with size = {@link #BARS_COUNT},
     *                if not set uses default bars heights
     */
    public void setBarMaxHeightsInDp(int[] heights) {
        if (heights == null) return;

        barMaxHeights = new int[BARS_COUNT];
        if (heights.length < BARS_COUNT) {
            System.arraycopy(heights, 0, barMaxHeights, 0, heights.length);
            for (int i = heights.length; i < BARS_COUNT; i++) {
                barMaxHeights[i] = heights[0];
            }
        } else {
            System.arraycopy(heights, 0, barMaxHeights, 0, BARS_COUNT);
        }
    }

    private void init() {
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);

        density = getResources().getDisplayMetrics().density;

        radius = (int) (CIRCLE_RADIUS_DP * density);
        spacing = (int) (CIRCLE_SPACING_DP * density);
        rotationRadius = (int) (ROTATION_RADIUS_DP * density);
        amplitude = (int) (IDLE_FLOATING_AMPLITUDE_DP * density);

        if (density <= MDPI_DENSITY) {
            amplitude *= 2;
        }

        startIdleInterpolation();
        animating = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (speechBars.isEmpty()) {
            initBars();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (speechBars.isEmpty()) {
            return;
        }

        if (animating) {
            animator.animate();
        }

        for (int i = 0; i < speechBars.size(); i++) {
            SpeechBar bar = speechBars.get(i);
            if (barColors != null) {
                paint.setColor(barColors[i]);
            } else if (barColor != -1) {
                paint.setColor(barColor);
            }
            canvas.drawRoundRect(bar.getRect(), radius, radius, paint);
        }

        if (animating) {
            invalidate();
        }
    }

    private void initBars() {
        final List<Integer> heights = initBarHeights();
        int firstCirclePosition = getMeasuredWidth() / 2 -
                2 * spacing -
                4 * radius;
        for (int i = 0; i < BARS_COUNT; i++) {
            int x = firstCirclePosition + (2 * radius + spacing) * i;
            SpeechBar bar = new SpeechBar(x, getMeasuredHeight() / 2, 2 * radius, heights.get(i), radius);
            speechBars.add(bar);
        }
    }

    private List<Integer> initBarHeights() {
        final List<Integer> barHeights = new ArrayList<>();
        if (barMaxHeights == null) {
            for (int i = 0; i < BARS_COUNT; i++) {
                barHeights.add((int) (DEFAULT_BARS_HEIGHT_DP[i] * density));
            }
        } else {
            for (int i = 0; i < BARS_COUNT; i++) {
                barHeights.add((int) (barMaxHeights[i] * density));
            }
        }
        return barHeights;
    }

    private void resetBars() {
        for (SpeechBar bar : speechBars) {
            bar.setX(bar.getStartX());
            bar.setY(bar.getStartY());
            bar.setHeight(radius * 2);
            bar.update();
        }
    }

    private void startIdleInterpolation() {
        animator = new IdleAnimator(speechBars, amplitude);
        animator.start();
    }

    private void startRmsInterpolation() {
        resetBars();
        animator = new RmsAnimator(speechBars);
        animator.start();
    }

    private void startTransformInterpolation() {
        resetBars();
        animator = new TransformAnimator(speechBars, getWidth() / 2, getHeight() / 2, rotationRadius);
        animator.start();
        ((TransformAnimator) animator).setOnInterpolationFinishedListener(new TransformAnimator.OnInterpolationFinishedListener() {
            @Override
            public void onFinished() {
                startRotateInterpolation();
            }
        });
    }

    private void startRotateInterpolation() {
        animator = new RotatingAnimator(speechBars, getWidth() / 2, getHeight() / 2);
        animator.start();
    }

    public void onBeginningOfSpeech() {
        isSpeaking = true;
    }

    public void onRmsChanged(float rmsdB) {
        if (animator == null || rmsdB < 1f) {
            return;
        }

        if (!(animator instanceof RmsAnimator) && isSpeaking) {
            startRmsInterpolation();
        }

        if (animator instanceof RmsAnimator) {
            ((RmsAnimator) animator).onRmsChanged(rmsdB);
        }
    }

    public void onEndOfSpeech() {
        isSpeaking = false;
        startTransformInterpolation();
    }

    public void onResultOrOnError() {
        stop();
        play();
    }
}
