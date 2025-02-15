package top.vmctcn.vmtranslationupdate.util;

import dev.architectury.platform.Platform;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

import top.vmctcn.vmtranslationupdate.VMTranslationUpdate;
import top.vmctcn.vmtranslationupdate.config.ConfigScreen;

public class ModConfigUtil {
    private static ConfigScreen configScreen;

    public static ConfigScreen getConfig() {
        if (configScreen == null) {
            AutoConfig.register(ConfigScreen.class, Toml4jConfigSerializer::new);
            configScreen = AutoConfig.getConfigHolder(ConfigScreen.class).getConfig();
        }
        return configScreen;
    }

    public static void setConfigScreen() {
        Platform.getMod(VMTranslationUpdate.MOD_ID).registerConfigurationScreen(parent -> AutoConfig.getConfigScreen(ConfigScreen.class, parent).get());
    }
}