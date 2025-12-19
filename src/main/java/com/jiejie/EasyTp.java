package com.jiejie;
import com.jiejie.server.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//1.2新增帮助功能
public class EasyTp implements ModInitializer {
    public static final String MOD_ID = "easytp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("easyTp开始初始化,作者:杰杰大王");
        new EasyTpJoin().playerJoin();
        new EasyTpHelp().EasyTpHelpStart();
        new EasyTpTp().easyTp();
        new EasyTpKill().killStart();
        new EasyTpPascalCase().PascalCaseStars();
        LOGGER.info("easyTp初始化完成,作者:杰杰大王");
    }

}