package top.vmctcn.vmtranslationupdate;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;

import dev.architectury.platform.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.vmctcn.vmtranslationupdate.util.*;

import java.io.File;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class VMTranslationUpdate {
    public static Random random;
    public static int tickCounter;
    public static final String MODNAME = "VMTranslationUpdate";
    public static final String MOD_ID = "vmtranslationupdate";
    static MinecraftClient client = MinecraftClient.getInstance();
    // Stenographer 兼容
    private static final boolean isStenographerLoaded = Platform.isModLoaded("stenographer");
    public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

    public static void init() {
        if (ModConfigUtil.getConfig().autoSwitchLanguage && !isStenographerLoaded) {
            client.options.language = (Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()).toLowerCase();
        }

        random = new Random();
        PackDownloadUtil.downloadResPack();
        ClientTickEvent.CLIENT_POST.register((client) -> {
            tickCounter++;
            int tickInterval = 20 * 60 * TipsUtil.getMinutes();
            if (tickCounter >= tickInterval) {
                tickCounter = 0;
                CompletableFuture.supplyAsync(() -> TipsUtil.getRandomMessageFromURLAsync(ModConfigUtil.getConfig().tipsUrl))
                        .thenAccept(message -> {
                            String randomMessage = TipsUtil.getRandomMessageFromURL(ModConfigUtil.getConfig().tipsUrl);
                            if (message != null) {
                                if (client.player != null) {
                                    client.player.sendSystemMessage(new TranslatableText(randomMessage), Util.NIL_UUID);
                                }
                            }
                        });
            }
        });
        NameUtil.init();
        if (ModConfigUtil.getConfig().updateUrl.length() > 0) {
            PlayerEvent.PLAYER_JOIN.register((player) -> {
                String localVersion = ModConfigUtil.getConfig().translationVersion;
                String onlineVersion = VersionCheckUtil.getOnlineVersion(player);

                if (!localVersion.equals(onlineVersion)) {
                    Text message = new TranslatableText("vmtranslationupdate.message.update2")
                            .append(new TranslatableText(ModConfigUtil.getConfig().downloadUrl).setStyle(
                                    Style.EMPTY
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ModConfigUtil.getConfig().downloadUrl))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("vmtranslationupdate.message.hover")))
                                            .withColor(Formatting.AQUA)
                            ))
                            .append(new TranslatableText("vmtranslationupdate.message.update3"));

                    player.sendSystemMessage(message, Util.NIL_UUID);
                }

                if (new File(PackDownloadUtil.resourcePackDir.toFile(), PackDownloadUtil.resourcePackName).exists()
                        && !client.options.resourcePacks.contains(PackDownloadUtil.resourcePackName) && !client.options.resourcePacks.contains("file/" + PackDownloadUtil.resourcePackName)) {
                    Text message = new TranslatableText("vmtranslationupdate.message.pack", ModConfigUtil.getConfig().packName).setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    player.sendSystemMessage(message, Util.NIL_UUID);
                }
            });
        }
    }
}
