package com.github.asteriskmods.inputtweaks.rawinput;

import net.minecraft.util.MouseHelper;

public class RawInputMouseHelper extends MouseHelper {

    public static int dx;
    public static int dy;

    @Override
    public void mouseXYChange() {
        this.deltaX = dx;
        dx = 0;
        this.deltaY = -dy;
        dy = 0;
    }

}
