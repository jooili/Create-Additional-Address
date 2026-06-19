package com.jooi.additionaladdress.mixin;

import com.jooi.additionaladdress.StockTickerBlockEntityAccess;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StockTickerBlockEntity.class)
public abstract class StockTickerBlockEntityExtraAddressMixin extends StockCheckingBlockEntity
        implements StockTickerBlockEntityAccess
{
    public StockTickerBlockEntityExtraAddressMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    private String createadditionaladdress$secondPreviouslyUsedAddress = "";

    @Override
    public String createadditionaladdress$getSecondPreviouslyUsedAddress() {
        return createadditionaladdress$secondPreviouslyUsedAddress;
    }

    @Override
    public void createadditionaladdress$setSecondPreviouslyUsedAddress(String address) {
        this.createadditionaladdress$secondPreviouslyUsedAddress = address;
    }

    @Inject(
            method = "write",
            at = @At("RETURN")
    )
    private void createadditionaladdress$onWrite(
            CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        tag.putString("createadditionaladdress$secondPreviouslyUsedAddress", createadditionaladdress$secondPreviouslyUsedAddress);
    }

    @Inject(
            method = "read",
            at = @At("RETURN")
    )
    private void createadditionaladdress$onRead(
            CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        createadditionaladdress$secondPreviouslyUsedAddress = tag.getString("createadditionaladdress$secondPreviouslyUsedAddress");
    }

    @Override
    public boolean createadditionaladdress$secondBroadcastPackageRequest(
            RequestType type,
            PackageOrderWithCrafts order,
            IdentifiedInventory ignoredHandler,
            String address) {
        boolean result = super.broadcastPackageRequest(type, order, ignoredHandler, address);
        createadditionaladdress$secondPreviouslyUsedAddress = address;
        notifyUpdate();
        return result;
    }

    @Shadow
    protected int ticksSinceLastUpdate;

    @Override
    public int createadditionaladdress$getTicksSinceLastUpdate() {
        return this.ticksSinceLastUpdate;
    }

    @Override
    public void createadditionaladdress$setTicksSinceLastUpdate(int ticks) {
        this.ticksSinceLastUpdate = ticks;
    }

}