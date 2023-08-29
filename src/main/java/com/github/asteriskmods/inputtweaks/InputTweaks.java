package com.github.asteriskmods.inputtweaks;

import com.github.asteriskmods.inputtweaks.rawinput.LWJGLAccess;
import com.github.asteriskmods.inputtweaks.rawinput.RawInputMouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = "inputtweaks"
)
public final class InputTweaks {

    public static MouseHelper original;
    public static MouseHelper rawInput;

    public static volatile boolean isRawInput = true;

    @Mod.EventHandler
    public static void onInit(FMLInitializationEvent event) {
        LWJGLAccess.inject();
        MinecraftForge.EVENT_BUS.register(InputTweaks.class);
        ClientCommandHandler.instance.registerCommand(new RawInputCommand());
        Minecraft minecraft = Minecraft.getMinecraft();
        original = minecraft.mouseHelper;
        rawInput = new RawInputMouseHelper();
    }

    @SubscribeEvent
    public static void onGUIOpen(GuiOpenEvent event) {
        InputTweaks.rawInput.mouseXYChange();
    }

}
