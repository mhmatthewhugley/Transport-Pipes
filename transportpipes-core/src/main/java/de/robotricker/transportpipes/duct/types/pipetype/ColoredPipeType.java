package de.robotricker.transportpipes.duct.types.pipetype;

import de.robotricker.transportpipes.duct.pipe.Pipe;
import de.robotricker.transportpipes.duct.types.BaseDuctType;
import org.bukkit.Material;

public class ColoredPipeType extends PipeType {

    private final Material coloringMaterial;

    public ColoredPipeType(BaseDuctType<Pipe> baseDuctType, String name, String displayName, Material coloringMaterial, String craftingPermission) {
        super(baseDuctType, name, displayName, craftingPermission);
        this.coloringMaterial = coloringMaterial;
    }

    public Material getColoringMaterial() {
        return coloringMaterial;
    }
}
