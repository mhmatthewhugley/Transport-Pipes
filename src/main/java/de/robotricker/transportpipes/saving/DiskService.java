package de.robotricker.transportpipes.saving;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.bukkit.World;

import de.robotricker.transportpipes.TransportPipes;
import de.robotricker.transportpipes.log.SentryService;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

public class DiskService {

    @Inject
    private TransportPipes transportPipes;
    @Inject
    private DuctSaver ductSaver;
    @Inject
    private SentryService sentry;

    public void loadDuctsSync(World world) {
        try {
            Path p = Paths.get(world.getWorldFolder().getAbsolutePath(), "ducts.dat");
            if (!Files.isRegularFile(p)) {
                p = Paths.get(world.getWorldFolder().getAbsolutePath(), "pipes.dat");
            }
            CompoundTag compoundTag = (CompoundTag) NBTUtil.read(p.toFile()).getTag();
            String version = compoundTag.getString("version");
            if(version == null || version.isEmpty()) {
                version = compoundTag.getString("PluginVersion");
            }

            long versionLong = transportPipes.convertVersionToLong(version);
            DuctLoader ductLoader;

            //one more than the real version to handle all versions below
            if (versionLong <= transportPipes.convertVersionToLong("4.3.2")) {
                ductLoader = transportPipes.getInjector().getSingleton(LegacyDuctLoader_v4_3_1.class);
            } else {
                ductLoader = transportPipes.getInjector().getSingleton(DuctLoader.class);
            }

            if (ductLoader == null) {
                throw new IOException("Could not load ducts.dat file because version " + version + " is not supported!");
            }

            ductLoader.loadDuctsSync(world, compoundTag);

        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
            sentry.record(e);
        }
    }

    public void saveDuctsSync(World world) {
        try {
            ductSaver.saveDuctsSync(world);
        } catch (Exception e) {
            sentry.record(e);
        }
    }

}
