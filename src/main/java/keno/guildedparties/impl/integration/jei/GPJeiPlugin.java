package keno.guildedparties.impl.integration.jei;

//? if =1.21.1 {
/*import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.data.guilds.items.GPComponents;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.vanilla.IJeiIngredientInfoRecipe;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class GPJeiPlugin implements IModPlugin {
    public GPJeiPlugin() {

    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return GuildedParties.GPLoc("guilded_plugin");
    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
        modAliasRegistration.addModAliases(GuildedParties.MOD_ID, "guilded");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        GuildedParties.LOGGER.info("Jei Detected!");
        IIngredientManager manager = jeiRuntime.getIngredientManager();
        Collection<ItemStack> stacks = manager.getAllItemStacks();
        List<IJeiIngredientInfoRecipe> recipes = createRecipes(manager);
        jeiRuntime.getRecipeManager().addRecipes(RecipeTypes.INFORMATION, recipes);
    }

    private static List<IJeiIngredientInfoRecipe> createRecipes(IIngredientManager manager) {
        Collection<ItemStack> stacks = manager.getAllItemStacks();
        List<IJeiIngredientInfoRecipe> recipes = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack.getComponents().contains(GPComponents.GUILD_COMPONENT)) {
                List<Identifier> ids = stack.get(GPComponents.GUILD_COMPONENT);
                recipes.add(createGuildItemInfo(manager, stack, ids));
            }
        }
        return recipes;
    }

    private static IJeiIngredientInfoRecipe createGuildItemInfo(IIngredientManager manager, ItemStack item, List<Identifier> ids) {
        ITypedIngredient<ItemStack> ingredient = manager.createTypedIngredient(VanillaTypes.ITEM_STACK, item).orElseThrow();

        MutableText text = Text.translatable("guildedparties.jei.guild_item").copy();
        for (Identifier id : ids) {
            text.append(id.getPath());
            if (!id.equals(ids.getLast())) {
                text.append(", ");
            }
        }

        return new IJeiIngredientInfoRecipe() {
            @Override
            public @Unmodifiable @NotNull List<ITypedIngredient<?>> getIngredients() {
                return List.of(ingredient);
            }

            @Override
            public @Unmodifiable @NotNull List<StringVisitable> getDescription() {
                return List.of(text);
            }
        };
    }
}
*///?}
//? if >1.21.1
public class GPJeiPlugin {}