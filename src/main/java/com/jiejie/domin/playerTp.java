package com.jiejie.domin;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

//1.4新添加 玩家查看传送者请求
public class playerTp {
    private ServerPlayerEntity player;//传送者
    private ServerPlayerEntity playerTo;//被传送者
    private boolean Agree ;//是否同意

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    public ServerPlayerEntity getPlayerTo() {
        return playerTo;
    }

    public void setPlayerTo(ServerPlayerEntity playerTo) {
        this.playerTo = playerTo;
    }

    public boolean isAgree() {
        return Agree;
    }

    public void setAgree(boolean agree) {
        Agree = agree;
    }

    public playerTp() {
    }

    public playerTp(ServerPlayerEntity player, ServerPlayerEntity playerTo) {
        this.player = player;
        this.playerTo = playerTo;
    }
    public playerTp(ServerPlayerEntity player, ServerPlayerEntity playerTo, boolean agree) {
        this.player = player;
        this.playerTo = playerTo;
        Agree = agree;
    }

    @Override
    public String toString() {
        return "playerTp{" +
                "player=" + player +
                ", playerTo=" + playerTo +
                ", Agree=" + Agree +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        playerTp playerTp = (playerTp) object;
        return Objects.equals(player, playerTp.player) && Objects.equals(playerTo, playerTp.playerTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, playerTo);
    }
}
