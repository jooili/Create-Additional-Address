package com.jooi.additionaladdress;

import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;

public interface StockTickerBlockEntityAccess {
    String createadditionaladdress$getSecondPreviouslyUsedAddress();
    void createadditionaladdress$setSecondPreviouslyUsedAddress(String address);

    boolean createadditionaladdress$secondBroadcastPackageRequest(
            RequestType type,
            PackageOrderWithCrafts order,
            IdentifiedInventory ignoredHandler,
            String address);
    int createadditionaladdress$getTicksSinceLastUpdate();
    void createadditionaladdress$setTicksSinceLastUpdate(int ticks);
}