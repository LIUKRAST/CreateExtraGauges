package net.liukrast.eg;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class TEST {

    public static List<BigItemStack> repackBasedOnRecipes(InventorySummary summary, PackageOrderWithCrafts order, String address, RandomSource r) {
        if (order.orderedCrafts().isEmpty())
            return List.of();

        List<BigItemStack> packages = new ArrayList<>();
        for (PackageOrderWithCrafts.CraftingEntry craftingEntry : order.orderedCrafts()) {
            int packagesToCreate = 0;
            Crafts: for (int i = 0; i < craftingEntry.count(); i++) {
                for (BigItemStack required : craftingEntry.pattern().stacks()) {
                    if (required.stack.isEmpty())
                        continue;
                    if (summary.getCountOf(required.stack) <= 0)
                        break Crafts;
                    summary.add(required.stack, -1);
                }
                packagesToCreate++;
            }

            ItemStackHandler target = new ItemStackHandler(PackageItem.SLOTS);
            List<BigItemStack> stacks = summary.getStacks();
            for (int currentSlot = 0; currentSlot < Math.min(stacks.size(), target.getSlots()); currentSlot++)
                target.setStackInSlot(currentSlot, stacks.get(currentSlot).stack.copy());

            ItemStack box = PackageItem.containing(target);
            PackageItem.setOrder(box, r.nextInt(), 0, true, 0, true,
                    PackageOrderWithCrafts.singleRecipe(craftingEntry.pattern()
                            .stacks()));
            packages.add(new BigItemStack(box, packagesToCreate));
        }

        return packages;
    }
}
