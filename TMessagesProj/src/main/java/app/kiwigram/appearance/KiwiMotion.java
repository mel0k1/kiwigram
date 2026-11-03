package app.kiwigram.appearance;

import android.view.View;
import android.view.animation.Interpolator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import java.util.WeakHashMap;

/**
 * Схема движения Material 3 Expressive, переведённая на View-систему.
 *
 * В Compose-мире это {@code MotionScheme.expressive()}; здесь те же шесть
 * спринг-спеков поверх androidx.dynamicanimation:
 *
 *  - spatial (позиция, размер, форма) — dampingRatio 0.6, лёгкий отскок:
 *    fast 1400, default 700, slow 300;
 *  - effects (цвет, прозрачность, видимость) — без отскока:
 *    fast 3800, default 1600, slow 800.
 *
 * Спринги прерываемы: повторный вызов с новой целью сохраняет текущую
 * скорость (в отличие от tween), поэтому интерфейс не «дёргается» на
 * быстрых тапах. Числа и мотивация — M3 Expressive Motion System
 * (skills/m3e-guide, 05_Motion_and_Theming/02_Motion_System.md).
 */
public final class KiwiMotion {

    public static final float SPATIAL_DAMPING = 0.6f;
    public static final float SPATIAL_STIFFNESS_FAST = 1400f;
    public static final float SPATIAL_STIFFNESS_DEFAULT = 700f;
    public static final float SPATIAL_STIFFNESS_SLOW = 300f;

    public static final float EFFECTS_DAMPING = SpringForce.DAMPING_RATIO_NO_BOUNCY;
    public static final float EFFECTS_STIFFNESS_FAST = 3800f;
    public static final float EFFECTS_STIFFNESS_DEFAULT = 1600f;
    public static final float EFFECTS_STIFFNESS_SLOW = 800f;

    private static final WeakHashMap<View, SpringAnimationsHolder> HOLDERS = new WeakHashMap<>();

    private KiwiMotion() {
    }

    public static SpringForce spatialFast() {
        return new SpringForce().setDampingRatio(SPATIAL_DAMPING).setStiffness(SPATIAL_STIFFNESS_FAST);
    }

    public static SpringForce spatialDefault() {
        return new SpringForce().setDampingRatio(SPATIAL_DAMPING).setStiffness(SPATIAL_STIFFNESS_DEFAULT);
    }

    public static SpringForce spatialSlow() {
        return new SpringForce().setDampingRatio(SPATIAL_DAMPING).setStiffness(SPATIAL_STIFFNESS_SLOW);
    }

    public static SpringForce effectsFast() {
        return new SpringForce().setDampingRatio(EFFECTS_DAMPING).setStiffness(EFFECTS_STIFFNESS_FAST);
    }

    public static SpringForce effectsDefault() {
        return new SpringForce().setDampingRatio(EFFECTS_DAMPING).setStiffness(EFFECTS_STIFFNESS_DEFAULT);
    }

    public static SpringForce effectsSlow() {
        return new SpringForce().setDampingRatio(EFFECTS_DAMPING).setStiffness(EFFECTS_STIFFNESS_SLOW);
    }

    public static SpringAnimation animateAlpha(View view, float target, SpringForce force) {
        return springBy(view, DynamicAnimation.ALPHA, target, force);
    }

    public static SpringAnimation animateScaleX(View view, float target, SpringForce force) {
        return springBy(view, DynamicAnimation.SCALE_X, target, force);
    }

    public static SpringAnimation animateScaleY(View view, float target, SpringForce force) {
        return springBy(view, DynamicAnimation.SCALE_Y, target, force);
    }

    public static SpringAnimation animateTranslationY(View view, float target, SpringForce force) {
        return springBy(view, DynamicAnimation.TRANSLATION_Y, target, force);
    }

    /**
     * Пружина вью-свойства с кэшем на вью: повторный вызов с новой целью
     * не создаёт конкурирующих анимаций, а перенаправляет существующую
     * с сохранением накопленной скорости (прерываемость из M3E).
     */
    private static SpringAnimation springBy(View view, DynamicAnimation.ViewProperty property, float target, SpringForce force) {
        SpringAnimationsHolder holder = HOLDERS.get(view);
        if (holder == null) {
            holder = new SpringAnimationsHolder();
            HOLDERS.put(view, holder);
        }
        SpringAnimation animation = holder.get(property);
        if (animation == null) {
            animation = new SpringAnimation(view, property);
            animation.getSpring().setDampingRatio(force.getDampingRatio());
            animation.getSpring().setStiffness(force.getStiffness());
            holder.put(property, animation);
        } else {
            animation.getSpring().setDampingRatio(force.getDampingRatio());
            animation.getSpring().setStiffness(force.getStiffness());
        }
        animation.animateToFinalPosition(target);
        return animation;
    }

    private static final class SpringAnimationsHolder {
        private final SpringAnimation[] animations = new SpringAnimation[6];

        SpringAnimation get(DynamicAnimation.ViewProperty property) {
            return animations[index(property)];
        }

        void put(DynamicAnimation.ViewProperty property, SpringAnimation animation) {
            animations[index(property)] = animation;
        }

        private static int index(DynamicAnimation.ViewProperty property) {
            if (property == DynamicAnimation.ALPHA) return 0;
            if (property == DynamicAnimation.SCALE_X) return 1;
            if (property == DynamicAnimation.SCALE_Y) return 2;
            if (property == DynamicAnimation.TRANSLATION_X) return 3;
            if (property == DynamicAnimation.TRANSLATION_Y) return 4;
            return 5;
        }
    }

    /**
     * Физически точный спринг-интерполятор для легаси-путей на ValueAnimator:
     * аналитическое решение уравнения пружины с теми же параметрами
     * (dampingRatio, stiffness), что и у SpringForce. Длительность — время
     * устоя с точностью 0.1%; ValueAnimator сам растягивает её на свою duration.
     */
    public static Interpolator springInterpolator(float dampingRatio, float stiffness) {
        return new SpringInterpolator(dampingRatio, stiffness);
    }

    public static final class SpringInterpolator implements Interpolator {

        private static final float EPSILON = 0.001f;

        private final float zeta;
        private final float omega;
        private final float duration;

        public SpringInterpolator(float dampingRatio, float stiffness) {
            this.zeta = dampingRatio;
            this.omega = (float) Math.sqrt(stiffness);
            this.duration = estimateDuration();
        }

        private float position(float t) {
            if (zeta < 1f) {
                float omegaD = omega * (float) Math.sqrt(1f - zeta * zeta);
                return 1f - (float) Math.exp(-zeta * omega * t)
                        * ((float) Math.cos(omegaD * t) + (zeta * omega / omegaD) * (float) Math.sin(omegaD * t));
            } else if (zeta == 1f) {
                return 1f - (float) Math.exp(-omega * t) * (1f + omega * t);
            } else {
                float omegaS = omega * (float) Math.sqrt(zeta * zeta - 1f);
                return 1f - (float) Math.exp(-zeta * omega * t)
                        * ((float) Math.cosh(omegaS * t) + (zeta * omega / omegaS) * (float) Math.sinh(omegaS * t));
            }
        }

        private float estimateDuration() {
            float t = 0f;
            float step = 1f / 120f;
            while (t < 8f) {
                if (Math.abs(position(t) - 1f) < EPSILON && Math.abs(position(t + step) - 1f) < EPSILON) {
                    break;
                }
                t += step;
            }
            return Math.max(t, step);
        }

        @Override
        public float getInterpolation(float input) {
            return position(input * duration);
        }
    }
}
