package app.kiwigram.appearance;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;

/**
 * Скруглённый прямоугольник с M3E-морфом радиуса при нажатии.
 *
 * Пресс-фидбек из Material 3 Expressive: угол «затягивается» пружиной
 * (spatialFast, лёгкий отскок) между rest- и pressed-радиусом, цвет
 * плавно уходит в pressed-вариант (effectsFast). Заменяет
 * {@code Theme.createSimpleSelectorRoundRectDrawable} там, где нужен
 * морф, — состояние приходит от вью через {@link #setState}.
 */
public class MorphableRoundRectDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final FloatValueHolder value = new FloatValueHolder(0f);
    private final SpringAnimation animation;

    private final float radiusRest;
    private final float radiusPressed;
    private final int color;
    private final int colorPressed;

    public MorphableRoundRectDrawable(float radiusRestPx, float radiusPressedPx, int color, int colorPressed) {
        this.radiusRest = radiusRestPx;
        this.radiusPressed = Math.min(radiusPressedPx, radiusRestPx);
        this.color = color;
        this.colorPressed = colorPressed;
        animation = new SpringAnimation(value, 0f);
        animation.getSpring().setDampingRatio(KiwiMotion.SPATIAL_DAMPING);
        animation.getSpring().setStiffness(KiwiMotion.SPATIAL_STIFFNESS_FAST);
        animation.addUpdateListener((a, v, velocity) -> invalidateSelf());
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean pressed = false;
        for (int s : state) {
            if (s == android.R.attr.state_pressed) {
                pressed = true;
                break;
            }
        }
        float target = pressed ? 1f : 0f;
        if (value.getValue() != target) {
            animation.animateToFinalPosition(target);
        }
        return true;
    }

    private float progress() {
        return Math.max(0f, Math.min(1f, value.getValue()));
    }

    private int currentColor() {
        float p = progress();
        if (p <= 0f) {
            return color;
        }
        int a = (int) (android.graphics.Color.alpha(color) + (android.graphics.Color.alpha(colorPressed) - android.graphics.Color.alpha(color)) * p);
        int r = (int) (android.graphics.Color.red(color) + (android.graphics.Color.red(colorPressed) - android.graphics.Color.red(color)) * p);
        int g = (int) (android.graphics.Color.green(color) + (android.graphics.Color.green(colorPressed) - android.graphics.Color.green(color)) * p);
        int b = (int) (android.graphics.Color.blue(color) + (android.graphics.Color.blue(colorPressed) - android.graphics.Color.blue(color)) * p);
        return android.graphics.Color.argb(a, r, g, b);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        RectF bounds = rect;
        bounds.set(getBounds());
        if (bounds.isEmpty()) {
            return;
        }
        float p = progress();
        float radius = radiusRest + (radiusPressed - radiusRest) * p;
        paint.setColor(currentColor());
        canvas.drawRoundRect(bounds, radius, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
