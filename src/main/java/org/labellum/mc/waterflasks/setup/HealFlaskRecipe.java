package org.labellum.mc.waterflasks.setup;

import java.util.function.BiFunction;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.recipes.DelegateRecipe;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class HealFlaskRecipe extends DelegateRecipe<IShapedRecipe<CraftingContainer>, CraftingContainer> implements CraftingRecipe
{
    // todo: remove when the recipe serializer becomes public
    public static Serializer<?> getShapedSerializer(BiFunction<ResourceLocation, IShapedRecipe<CraftingContainer>, DelegateRecipe<IShapedRecipe<CraftingContainer>, CraftingContainer>> factory)
    {
        return Serializer.shaped(factory);
    }

    protected HealFlaskRecipe(ResourceLocation id, IShapedRecipe<CraftingContainer> recipe)
    {
        super(id, recipe);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        FluidStack fluid = FluidStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (Helpers.isItem(stack, Registration.FLASKS))
            {
                fluid = stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)).orElse(FluidStack.EMPTY);
                break;
            }
        }
        final ItemStack result = super.assemble(inv);
        if (!fluid.isEmpty())
        {
            final FluidStack fillFluid = fluid;
            result.getCapability(Capabilities.FLUID_ITEM).ifPresent(cap ->
                cap.fill(fillFluid, IFluidHandler.FluidAction.EXECUTE)
            );
        }
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem() && Helpers.isItem(stack, TFCTags.Items.KNIVES))
            {
                items.set(i, Helpers.damageCraftingItem(stack, 1).copy());
            }
        }
        return items;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Registration.HEAL_FLASK_SERIALIZER.get();
    }

}
