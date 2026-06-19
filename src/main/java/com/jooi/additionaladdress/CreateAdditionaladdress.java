package com.jooi.additionaladdress;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreateAdditionaladdress.MODID)
public class CreateAdditionaladdress {

    public static final String MODID = "createadditionaladdress";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateAdditionaladdress(IEventBus modEventBus) {

        AllPackets.register();

    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}