package com.jiejie.util;

import com.jiejie.domin.playerTp;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalVariables {
    private GlobalVariables() {
    }

    public static final String EASY_TP = "[EasyTp]";
    public static final Map<UUID, List<playerTp>> PLAYER_TP_MAP = new ConcurrentHashMap<>();
}
