package com.jooi.additionaladdress.mixin;

import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenExtraAddressMixin extends AbstractSimiContainerScreen {

    public StockKeeperRequestScreenExtraAddressMixin(AbstractContainerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Unique
    private AddressEditBox createadditionaladdress$SecondAddressBox;

    @Shadow
    private int windowHeight;

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void createadditionaladdress$on_init(CallbackInfo ci) {

        StockKeeperRequestScreen screen = (StockKeeperRequestScreen) (Object) this;

        int x = screen.getGuiLeft();
        int y = screen.getGuiTop();
        int winHeight = this.windowHeight;

        createadditionaladdress$SecondAddressBox = new AddressEditBox(
                screen,
                new NoShadowFontWrapper(Minecraft.getInstance().font),
                x + 27,
                y + winHeight - 17,
                92,
                10,
                true
        );

        createadditionaladdress$SecondAddressBox.setTextColor(0x714A40);

        createadditionaladdress$SecondAddressBox.setValue("");

        addRenderableWidget(createadditionaladdress$SecondAddressBox);
    }

    @Inject(
            method = "containerTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/AddressEditBox;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void createadditionaladdress$onContainerTick(CallbackInfo ci) {
        createadditionaladdress$SecondAddressBox.tick();
    }

    @Shadow
    private static AllGuiTextures HEADER;

    @Shadow
    private static AllGuiTextures BODY;

    @Shadow
    private static AllGuiTextures FOOTER;

    @Inject(
            method = "renderBg",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;getGuiTop()I",
                    ordinal = 1
            )
    )
    private void createadditionaladdress$renderSecondAddressBox(
            GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci)
    {
        StockKeeperRequestScreen screen = (StockKeeperRequestScreen) (Object) this;
        int x = screen.getGuiLeft();
        int y = screen.getGuiTop() + HEADER.getHeight()
                + (windowHeight - HEADER.getHeight() - FOOTER.getHeight()) / BODY.getHeight() * BODY.getHeight();

        com.jooi.additionaladdress.AllGuiTextures.STOCK_KEEPER_REQUEST_ADDRESS.render(graphics, x + 1, y + 53);
    }

    @Inject(
            method = "renderBg",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;getGuiTop()I",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void createadditionaladdress$renderSecondAddressBoxHint(
            GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (createadditionaladdress$SecondAddressBox.getValue().isBlank()
                && !createadditionaladdress$SecondAddressBox.isFocused())
        {
            graphics.drawString(Minecraft.getInstance().font, CreateLang.translate("gui.stock_keeper.package_adress")
                            .style(ChatFormatting.ITALIC)
                            .component(),
                    createadditionaladdress$SecondAddressBox.getX(),
                    createadditionaladdress$SecondAddressBox.getY(),
                    0xff_CDBCA8,
                    false
            );
        }
    }

    @Inject(
            method = "renderForeground",
            at = @At("RETURN")
    )
    private void createadditionaladdress$renderSecondAddressBoxTooltip(
            GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (createadditionaladdress$SecondAddressBox.getValue().isBlank()
                && !createadditionaladdress$SecondAddressBox.isFocused()
                && createadditionaladdress$SecondAddressBox.isHovered()) {
            graphics.renderComponentTooltip(
                    font,
                    List.of(
                            CreateLang.translate("gui.factory_panel.restocker_address")
                                    .color(ScrollInput.HEADER_RGB)
                                    .component(),
                            CreateLang.translate("gui.schedule.lmb_edit")
                                    .style(ChatFormatting.DARK_GRAY)
                                    .style(ChatFormatting.ITALIC)
                                    .component()
                    ),
                    mouseX,
                    mouseY
            );
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createadditionaladdress$handleSecondAddressBoxFocus(
            double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (createadditionaladdress$SecondAddressBox.isFocused()) {
            boolean result = createadditionaladdress$SecondAddressBox.mouseClicked(pMouseX, pMouseY, pButton);
            if (createadditionaladdress$SecondAddressBox.isHovered() || result) {
                cir.setReturnValue(result);
                return;
            }
            createadditionaladdress$SecondAddressBox.setFocused(false);
        }
    }



}

