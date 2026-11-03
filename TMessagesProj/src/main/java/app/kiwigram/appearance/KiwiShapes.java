package app.kiwigram.appearance;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;

import org.telegram.messenger.AndroidUtilities;

/**
 * Токены форм Material 3 Expressive для View-системы.
 *
 * Радиусы соответствуют M3-шкале (extra-small 4 … extra-large 28);
 * {@link #pressMorphBackground} собирает фирменный M3E-приём —
 * shape morphing при нажатии (угол «затягивается» пружиной, см.
 * KiwiMotion.SPATIAL_*) — и возвращает дравабл, чтобы вью могла
 * обновлять цвета при смене темы.
 */
public final class KiwiShapes {

    public static final float CORNER_EXTRA_SMALL = 4f;
    public static final float CORNER_SMALL = 8f;
    public static final float CORNER_MEDIUM = 12f;
    public static final float CORNER_LARGE = 16f;
    public static final float CORNER_EXTRA_LARGE = 28f;

    private KiwiShapes() {
    }

    public static float dp(float value) {
        return AndroidUtilities.dpf2(value);
    }

    /** Радиус полного круга для квадратной формы со стороной sizePx. */
    public static float fullRadius(float sizePx) {
        return sizePx / 2f;
    }

    /** Скругление вью вместе с тенью и клипом (outline). */
    public static void setRoundedOutline(View view, float radiusPx) {
        final float r = radiusPx;
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
            }
        });
        view.setClipToOutline(true);
    }

    /**
     * M3E-морф при нажатии: фон-прямоугольник, чей радиус пружиной
     * уходит из rest-значения к pressed (по умолчанию — 60% от rest).
     * Вызывать после того, как известен целевой размер формы; при смене
     * темы создать заново или выставить цвета через дравабл.
     *
     * @param restRadiusPx    радиус в покое, px (уже сконвертированный dp)
     * @param color           цвет в покое
     * @param colorPressed    цвет при нажатии
     */
    public static MorphableRoundRectDrawable pressMorphBackground(View view, float restRadiusPx, int color, int colorPressed) {
        MorphableRoundRectDrawable drawable = new MorphableRoundRectDrawable(restRadiusPx, restRadiusPx * 0.6f, color, colorPressed);
        view.setBackground(drawable);
        return drawable;
    }
}
