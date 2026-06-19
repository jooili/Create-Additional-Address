package com.jooi.additionaladdress.mixin;

import com.jooi.additionaladdress.StockTickerBlockEntityAccess;
import com.jooi.additionaladdress.content.logistics.SecondAddressPackageOrderRequestPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.*;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenExtraAddressMixin extends AbstractSimiContainerScreen<StockKeeperRequestMenu> {

    public StockKeeperRequestScreenExtraAddressMixin(StockKeeperRequestMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Unique
    private AddressEditBox createadditionaladdress$SecondAddressBox;

    @Shadow
    private int windowHeight;

    @Shadow
    StockTickerBlockEntity blockEntity;

    @Shadow
    boolean encodeRequester;

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void createadditionaladdress$on_init(CallbackInfo ci) {

        if (encodeRequester) {
            createadditionaladdress$SecondAddressBox = null;
            return;
        }

        StockKeeperRequestScreen screen = (StockKeeperRequestScreen) (Object) this;

        int x = screen.getGuiLeft();
        int y = screen.getGuiTop();
        int winHeight = this.windowHeight;

        boolean initial = createadditionaladdress$SecondAddressBox == null;
        String secondPreviouslyUsedAddress = initial ? ((StockTickerBlockEntityAccess) blockEntity).createadditionaladdress$getSecondPreviouslyUsedAddress() : createadditionaladdress$SecondAddressBox.getValue();

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

        createadditionaladdress$SecondAddressBox.setValue(secondPreviouslyUsedAddress);

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
        if (createadditionaladdress$SecondAddressBox == null) {return;}
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
    private void createadditionaladdress$renderSecondAddressBoxBg(
            GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}
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
        if (createadditionaladdress$SecondAddressBox == null) {return;}

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
            GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

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
            double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

        if (createadditionaladdress$SecondAddressBox.isFocused()) {
            boolean result = createadditionaladdress$SecondAddressBox.mouseClicked(pMouseX, pMouseY, pButton);
            if (createadditionaladdress$SecondAddressBox.isHovered() || result) {
                cir.setReturnValue(result);
                return;
            }
            createadditionaladdress$SecondAddressBox.setFocused(false);
        }
    }

    @Inject(
            method = "mouseScrolled",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createadditionaladdress$handleSecondAddressBoxScroll(
            double mouseX, double mouseY, double scrollX, double scrollY,
            CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

        if (createadditionaladdress$SecondAddressBox.isFocused()
                && createadditionaladdress$SecondAddressBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
        {
            cir.setReturnValue(true);
        }
    }

    @Shadow
    private boolean ignoreTextInput;

    @Inject(
            method = "charTyped",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createadditionaladdress$handleCharTyped(
            char pCodePoint, int pModifiers, CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

        if (ignoreTextInput)
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (createadditionaladdress$SecondAddressBox.isFocused()
                && createadditionaladdress$SecondAddressBox.charTyped(pCodePoint, pModifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Shadow
    public AddressEditBox addressBox;
    @Shadow
    public EditBox searchBox;

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createadditionaladdress$handleChatKeyWithSecondBox(
            int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

        ignoreTextInput = false;

        if (!addressBox.isFocused()
                && !createadditionaladdress$SecondAddressBox.isFocused()
                && !searchBox.isFocused()
                && minecraft.options.keyChat.matches(pKeyCode, pScanCode))
        {
            ignoreTextInput = true;
            searchBox.setFocused(true);
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/EditBox;getValue()Ljava/lang/String;",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void createadditionaladdress$handleKeyPressed(
            int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) {return;}

        if (createadditionaladdress$SecondAddressBox.isFocused()
                && createadditionaladdress$SecondAddressBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "removed",
            at = @At("HEAD")
    )
    private void createadditionaladdress$onRemoved(CallbackInfo ci)
    {

        if (createadditionaladdress$SecondAddressBox == null) {return;}

        BlockPos pos = blockEntity.getBlockPos();

        CatnipServices.NETWORK.sendToServer(
                new SecondAddressPackageOrderRequestPacket(
                        pos,
                        PackageOrderWithCrafts.empty(),
                        createadditionaladdress$SecondAddressBox.getValue(),
                        false
                )
        );
    }

    @Unique
    private boolean createadditionaladdress$isSecondConfirmHovered(int mouseX, int mouseY) {
        int confirmX = getGuiLeft() + 143;
        int confirmY = getGuiTop() + windowHeight - 21;
        int confirmW = 78;
        int confirmH = 18;

        if (mouseX < confirmX || mouseX >= confirmX + confirmW)
            return false;
        if (mouseY < confirmY || mouseY >= confirmY + confirmH)
            return false;
        return true;
    }

    @Shadow
    public List<BigItemStack> itemsToOrder;

    @Shadow
    int successTicks;

    @Shadow
    int windowWidth;

    @Inject(
            method = "renderBg",
            at = @At("RETURN")
    )
    private void createadditionaladdress$renderSecondConfirmButton(
            GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {

        if (createadditionaladdress$SecondAddressBox == null) return;

        int x = getGuiLeft();
        int y = getGuiTop();

        PoseStack ms = graphics.pose();

        boolean justSent = itemsToOrder.isEmpty() && successTicks > 0;

        if (createadditionaladdress$isSecondConfirmHovered(mouseX, mouseY) && !justSent)
            AllGuiTextures.STOCK_KEEPER_REQUEST_SEND_HOVER.render(graphics, x + windowWidth - 81,
                    y + windowHeight - 22);


        MutableComponent component =
                CreateLang.translate(encodeRequester ? "gui.stock_keeper.configure" : "gui.stock_keeper.send")
                        .component();

        if (justSent) {
            float alpha = Mth.clamp((successTicks + partialTicks - 5f) / 5f, 0f, 1f);
            ms.pushPose();
            ms.translate(alpha * alpha * 50, 0, 0);
            if (successTicks < 10)
                graphics.drawString(font, component, x + windowWidth - 42 - font.width(component) / 2,
                        y + windowHeight - 16, new Color(0x252525).setAlpha(1 - alpha * alpha)
                                .getRGB(),
                        false);
            ms.popPose();

        } else {
            graphics.drawString(font, component, x + windowWidth - 42 - font.width(component) / 2,
                    y + windowHeight - 16, 0x252525, false);
        }
    }

    @Invoker("revalidateOrders")
    public abstract void callRevalidateOrders();

    @Shadow
    private InventorySummary forcedEntries;

    @Shadow
    private boolean canRequestCraftingPackage;

    @Shadow
    public List<CraftableBigItemStack> recipesToOrder;

    @Invoker("isSchematicListMode")
    public abstract boolean callIsSchematicListMode();

    @Unique
    private void createadditionaladdress$sendIt() {
        callRevalidateOrders();
        if (itemsToOrder.isEmpty())
            return;

        forcedEntries = new InventorySummary();
        InventorySummary summary = blockEntity.getLastClientsideStockSnapshotAsSummary();
        for (BigItemStack toOrder : itemsToOrder) {
            // momentarily cut the displayed stack size until the stock updates come in
            int countOf = summary.getCountOf(toOrder.stack);
            if (countOf == BigItemStack.INF)
                continue;
            forcedEntries.add(toOrder.stack.copy(), -1 - Math.max(0, countOf - toOrder.count));
        }

        PackageOrderWithCrafts order = PackageOrderWithCrafts.simple(itemsToOrder);

        if (canRequestCraftingPackage && !itemsToOrder.isEmpty() && !recipesToOrder.isEmpty()) {
            List<PackageOrderWithCrafts.CraftingEntry> craftList = new ArrayList<>();
            for (CraftableBigItemStack cbis : recipesToOrder) {
                if (!(cbis.recipe instanceof CraftingRecipe cr))
                    continue;
                int craftedCount = 0;
                int targetCount = cbis.count / cbis.getOutputCount(blockEntity.getLevel());
                List<BigItemStack> mutableOrder = BigItemStack.duplicateWrappers(itemsToOrder);

                while (craftedCount < targetCount) {
                    // Carefully split the ordered recipes based on what exactly will be used to craft them
                    PackageOrder pattern = new PackageOrder(FactoryPanelScreen.convertRecipeToPackageOrderContext(cr, mutableOrder, true));
                    int maxCrafts = targetCount - craftedCount;
                    int availableCrafts = 0;

                    boolean itemsExhausted = false;
                    Outer:
                    while (availableCrafts < maxCrafts && !itemsExhausted) {
                        List<BigItemStack> previousSnapshot = BigItemStack.duplicateWrappers(mutableOrder);
                        itemsExhausted = true;
                        Pattern:
                        for (BigItemStack patternStack : pattern.stacks()) {
                            if (patternStack.stack.isEmpty())
                                continue;
                            for (BigItemStack ordered : mutableOrder) {
                                if (!ItemStack.isSameItemSameComponents(ordered.stack, patternStack.stack))
                                    continue;
                                if (ordered.count == 0)
                                    continue;
                                ordered.count -= 1;
                                itemsExhausted = false;
                                continue Pattern;
                            }
                            mutableOrder = previousSnapshot;
                            break Outer;
                        }
                        availableCrafts++;
                    }

                    if (availableCrafts == 0)
                        break;

                    craftList.add(new PackageOrderWithCrafts.CraftingEntry(pattern, availableCrafts));
                    craftedCount += availableCrafts;
                }

            }
            order = new PackageOrderWithCrafts(order.orderedStacks(), craftList);
        }

        CatnipServices.NETWORK.sendToServer(
                new SecondAddressPackageOrderRequestPacket(blockEntity.getBlockPos(), order, createadditionaladdress$SecondAddressBox.getValue(), encodeRequester));

        itemsToOrder = new ArrayList<>();
        recipesToOrder = new ArrayList<>();
        ((StockTickerBlockEntityAccess) blockEntity).createadditionaladdress$setTicksSinceLastUpdate(10);
        successTicks = 1;

        if (callIsSchematicListMode())
            menu.player.closeContainer();

    }

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createadditionaladdress$handleSecondConfirmButtonClicked(
            double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir)
    {
        if (createadditionaladdress$SecondAddressBox == null) return;

        boolean lmb = pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        if (lmb && createadditionaladdress$isSecondConfirmHovered((int) pMouseX, (int) pMouseY)) {
            createadditionaladdress$sendIt();
            playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            cir.setReturnValue(true);
        }
    }
}

