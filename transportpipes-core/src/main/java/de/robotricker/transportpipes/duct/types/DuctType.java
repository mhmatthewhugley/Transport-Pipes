package de.robotricker.transportpipes.duct.types;

import de.robotricker.transportpipes.duct.Duct;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class DuctType {

    private final BaseDuctType<? extends Duct> baseDuctType;
    private final String name;
    private final String displayName;
    private final Set<DuctType> connectables;
    private Recipe ductRecipe;
    private final String craftingPermission;

    public DuctType(BaseDuctType<? extends Duct> baseDuctType, String name, String displayName, String craftingPermission) {
        this.baseDuctType = baseDuctType;
        this.name = name;
        this.displayName = displayName;
        this.craftingPermission = craftingPermission;
        this.connectables = new HashSet<>();
    }

    public void connectTo(String... ductTypeNames) {
        for (String name : ductTypeNames) {
            connectables.add(getBaseDuctType().ductTypeOf(name));
        }
    }

    public DuctType connectToAll() {
        connectables.addAll(getBaseDuctType().ductTypes());
        return this;
    }

    public DuctType connectToClasses(Class<? extends DuctType> clazz) {
        for (DuctType dt : getBaseDuctType().ductTypes()) {
            if (clazz.isAssignableFrom(dt.getClass())) {
                connectables.add(dt);
            }
        }
        return this;
    }

    public DuctType disconnectFrom(String... ductTypeNames) {
        for (String name : ductTypeNames) {
            connectables.remove(getBaseDuctType().ductTypeOf(name));
        }
        return this;
    }

    public DuctType disconnectFromClasses(Class<? extends DuctType> clazz) {
        for (DuctType dt : getBaseDuctType().ductTypes()) {
            if (clazz.isAssignableFrom(dt.getClass())) {
                connectables.remove(dt);
            }
        }
        return this;
    }

    public BaseDuctType<? extends Duct> getBaseDuctType() {
        return baseDuctType;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean is(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public String getFormattedTypeName() {
        return displayName;
    }

    public Set<DuctType> getConnectables() {
        return connectables;
    }

    public Recipe getDuctRecipe() {
        return ductRecipe;
    }

    public void setDuctRecipe(Recipe ductRecipe) {
        this.ductRecipe = ductRecipe;
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
        	if (iterator.next().getResult().equals(ductRecipe.getResult())) return;
        }
        Bukkit.addRecipe(ductRecipe);
    }

    public String getCraftingPermission() {
        return craftingPermission;
    }

    public boolean hasPlayerCraftingPermission(Player player) {
        return craftingPermission == null || player.hasPermission(craftingPermission);
    }

    public boolean connectsTo(DuctType otherDuctType) {
        return baseDuctType.equals(otherDuctType.baseDuctType) && connectables.contains(otherDuctType) && otherDuctType.connectables.contains(this);
    }

    @Override
    public String toString() {
        return "DuctType: " + name + "\n" +
                "Connectables: " + connectables.stream().map(dt -> dt.name).collect(Collectors.joining(", "));
    }

}
