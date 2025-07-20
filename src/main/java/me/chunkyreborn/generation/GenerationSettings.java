package me.chunkyreborn.generation;

import org.bukkit.World;

public class GenerationSettings {
    private World world;
    private ShapeType shape = ShapeType.SQUARE;
    private int centerX = 0;
    private int centerZ = 0;
    private int radius = 5;
    private String pattern = "spiral";
    private boolean silent = false;
    private int quietInterval = 5;

    public GenerationSettings(World world) {
        this.world = world;
    }

    public World getWorld() { return world; }
    public void setWorld(World world) { this.world = world; }

    public ShapeType getShape() { return shape; }
    public void setShape(ShapeType shape) { this.shape = shape; }

    public int getCenterX() { return centerX; }
    public void setCenterX(int centerX) { this.centerX = centerX; }

    public int getCenterZ() { return centerZ; }
    public void setCenterZ(int centerZ) { this.centerZ = centerZ; }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public boolean isSilent() { return silent; }
    public void setSilent(boolean silent) { this.silent = silent; }

    public int getQuietInterval() { return quietInterval; }
    public void setQuietInterval(int quietInterval) { this.quietInterval = quietInterval; }
}
