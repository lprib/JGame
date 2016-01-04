package com.jaxforreal.jgame.tile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jaxforreal.jgame.GameManager;
import com.sun.istack.internal.Nullable;

public abstract class Tile {
    //All tiles in game here

    /** ID is used to save tiles back to text format */
    public final int id;
    public GameManager gameManager;

    public Tile(@Nullable GameManager gameManager, int id) {
        this.gameManager = gameManager;
        this.id = id;
    }

    public abstract void update(float delta);

    public abstract void render(SpriteBatch spriteBatch, float x, float y, float w, float h);

    public abstract Tile getClone();

    public abstract boolean isSolid();
}