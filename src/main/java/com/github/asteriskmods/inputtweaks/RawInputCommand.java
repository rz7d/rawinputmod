package com.github.asteriskmods.inputtweaks;

import com.github.asteriskmods.inputtweaks.rawinput.RawInputMouseHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RawInputCommand extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return true;
    }

    @Override
    public String getName() {
        return "rawinput";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rawinput <on|off>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Arrays.asList("on", "off");
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Raw Input is currently " + (InputTweaks.isRawInput ? (ChatFormatting.GREEN + "Enabled") : (ChatFormatting.RED + "Disabled"))));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "enable":
            case "true":
            case "on":
                InputTweaks.rawInput.mouseXYChange();
                InputTweaks.isRawInput = true;
                minecraft.mouseHelper = InputTweaks.rawInput;
                sender.sendMessage(new TextComponentString("Raw Input: " + ChatFormatting.GREEN + "Enabled"));
                break;
            case "disable":
            case "false":
            case "off":
                InputTweaks.isRawInput = false;
                minecraft.mouseHelper = InputTweaks.original;
                sender.sendMessage(new TextComponentString("Raw Input: " + ChatFormatting.RED + "Disabled"));
                break;
            default:
                sender.sendMessage(new TextComponentString("Usage: /rawinput <on|off>"));
        }
    }

}
