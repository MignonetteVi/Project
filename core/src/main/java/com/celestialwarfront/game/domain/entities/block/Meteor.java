package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.celestialwarfront.game.contract.IDamageable;

/**
 * Анимированный метеорит с 4 кадрами полёта, не требующий передачи texture в конструктор.
 */
public class Meteor implements IDamageable {
    // Статические текстуры для кадров анимации
    private static final Texture FRAME1 = new Texture(Gdx.files.internal("meteor1.png"));
    private static final Texture FRAME2 = new Texture(Gdx.files.internal("meteor2.png"));
    private static final Texture FRAME3 = new Texture(Gdx.files.internal("meteor3.png"));
    private static final Texture FRAME4 = new Texture(Gdx.files.internal("meteor4.png"));

    // Анимация полёта: 0.1s на кадр
    private static final Animation<TextureRegion> FLY_ANIMATION;
    static {
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(FRAME1));
        frames.add(new TextureRegion(FRAME2));
        frames.add(new TextureRegion(FRAME3));
        frames.add(new TextureRegion(FRAME4));
        FLY_ANIMATION = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    public float x, y;
    private final float speed;
    private final float width, height;
    private float stateTime = 0f;
    private boolean destroyed = false;

    /**
     * @param x стартовая позиция X
     * @param y стартовая позиция Y
     * @param speed скорость падения (px/s)
     */
    public Meteor(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        // размер из первого кадра с масштабом 0.5
        TextureRegion first = FLY_ANIMATION.getKeyFrame(0f);
        this.width = first.getRegionWidth() * 0.5f;
        this.height = first.getRegionHeight() * 0.5f;
    }

    @Override
    public void applyDamage(int damage) {
        destroyed = true;
    }

    /** Обновление позиции и анимации */
    public void update(float delta) {
        y -= speed * delta;
        stateTime += delta;
    }

    /** Рендер текущего кадра, если метеорит ещё не уничтожен */
    public void render(SpriteBatch batch) {
        if (destroyed) return;
        TextureRegion frame = FLY_ANIMATION.getKeyFrame(stateTime);
        batch.draw(frame, x, y, width, height);
    }

    /** Граница для коллизии */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** Если метеорит уничтожен или ушёл за экран */
    public boolean isDestroyed() {
        return destroyed || y + height < 0;
    }

    /** Освободить ресурсы (вызывать в GameManager.dispose()) */
    public static void dispose() {
        FRAME1.dispose();
        FRAME2.dispose();
        FRAME3.dispose();
        FRAME4.dispose();
    }
}
