package com.jiejie.domin;

import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;

public class Tp {
    private ServerWorld world;

    private double destX;
    private double destY;
    private double destZ;
    private Set<PositionFlag> flags;
    private float yaw;
    private float pitch;

    public ServerWorld getWorld() {
        return world;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public double getDestX() {
        return destX;
    }

    public void setDestX(double destX) {
        this.destX = destX;
    }

    public double getDestY() {
        return destY;
    }

    public void setDestY(double destY) {
        this.destY = destY;
    }

    public double getDestZ() {
        return destZ;
    }

    public void setDestZ(double destZ) {
        this.destZ = destZ;
    }

    public Set<PositionFlag> getFlags() {
        return flags;
    }

    public void setFlags(Set<PositionFlag> flags) {
        this.flags = flags;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Tp() {
    }

    public Tp(ServerWorld world, double destX, double destY, double destZ, float yaw, float pitch) {
        this.world = world;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public String toString() {
        return "Tp{" +
                "world=" + world +
                ", destX=" + destX +
                ", destY=" + destY +
                ", destZ=" + destZ +
                ", flags=" + flags +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
