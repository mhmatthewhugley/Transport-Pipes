package de.robotricker.transportpipes.api;

import de.robotricker.transportpipes.ThreadService;
import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.manager.DuctManager;
import de.robotricker.transportpipes.duct.manager.GlobalDuctManager;
import de.robotricker.transportpipes.duct.manager.PipeManager;
import de.robotricker.transportpipes.duct.pipe.Pipe;
import de.robotricker.transportpipes.duct.pipe.items.PipeItem;
import de.robotricker.transportpipes.duct.types.DuctType;
import de.robotricker.transportpipes.listener.TPContainerListener;
import de.robotricker.transportpipes.location.BlockLocation;
import de.robotricker.transportpipes.location.TPDirection;
import de.robotricker.transportpipes.utils.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

public class TransportPipesAPI {

    private static TransportPipesAPI instance;

    @Inject
    private GlobalDuctManager globalDuctManager;

    @Inject
    private DuctRegister ductRegister;

    @Inject
    private ThreadService threadService;

    @Inject
    private TPContainerListener tpContainerListener;

    public TransportPipesAPI() {
        instance = this;
    }

    /**
     * Gets a list of ducts in a world
     * @param world The world to get the ducts in
     * @return A ConcurrentSkipListMap of ducts in a world where the Key is the BlockLocation and the Value is the Duct
     */
    public ConcurrentSkipListMap<BlockLocation, Duct> getDuctsInWorld(World world) {
        return globalDuctManager.getDucts(world);
    }

    /**
     * Build a duct in a world
     * @param baseDuctTypeName The name of the base duct type, such as "pipe"
     * @param ductTypeName The name of the duct type, such as "ColoredPipe"
     * @param blockLocation The BlockLocation of the duct
     * @param world The World the duct is to be built in
     * @param chunk The Chunk the duct is to be built in
     * @throws Exception if a Duct already exists at the BlockLocation or there is a protected Container next to the BlockLocation
     */
    public void buildDuct(String baseDuctTypeName, String ductTypeName, BlockLocation blockLocation, World world, Chunk chunk) throws Exception {

        if (globalDuctManager.getDuctAtLoc(world, blockLocation) != null) {
            throw new Exception("Another duct exists at this location");
        }

        DuctType ductType = ductRegister.baseDuctTypeOf(baseDuctTypeName).ductTypeOf(ductTypeName);

        if (Objects.requireNonNull(ductType).getBaseDuctType().is("pipe")) {
            for (TPDirection dir : TPDirection.values()) {
                if (WorldUtils.lwcProtection(blockLocation.toBlock(world).getRelative(dir.getBlockFace()))) {
                    throw new Exception("Cannot place duct next to protected container block");
                }
            }
        }

        Duct duct = globalDuctManager.createDuctObject(ductType, blockLocation, world, chunk);
        globalDuctManager.registerDuct(duct);
        globalDuctManager.updateDuctConnections(duct);
        globalDuctManager.registerDuctInRenderSystems(duct, true);
        globalDuctManager.updateNeighborDuctsConnections(duct);
        globalDuctManager.updateNeighborDuctsInRenderSystems(duct, true);
    }

    /**
     * Destroy a Duct in a World
     * @param blockLocation The BlockLocation of the Duct to destroy
     * @param world The World that the Duct is in
     * @throws Exception if there is no Duct at the BlockLocation
     */
    public void destroyDuct(BlockLocation blockLocation, World world) throws Exception {
        Duct duct = globalDuctManager.getDuctAtLoc(world, blockLocation);
        if (duct == null) {
            throw new Exception("There is no duct at this location");
        }
        globalDuctManager.unregisterDuct(duct);
        globalDuctManager.unregisterDuctInRenderSystem(duct, true);
        globalDuctManager.updateNeighborDuctsConnections(duct);
        globalDuctManager.updateNeighborDuctsInRenderSystems(duct, true);
        globalDuctManager.playDuctDestroyActions(duct, null);
    }

    /**
     * Put an item into a Pipe
     * @param pipe The Pipe to put the item into
     * @param item The Item to put into the Pipe
     * @param direction The TPDirection the Item should initially travel in
     */
    public void putItemInPipe(Pipe pipe, ItemStack item, TPDirection direction) {
        ((PipeManager) (DuctManager<?>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).putPipeItemInPipe(new PipeItem(item, pipe.getWorld(), pipe.getBlockLoc(), direction));
    }

    /**
     * Register a TransportPipesContainer
     * @param container The TransportPipesContainer to register
     * @param blockLocation The BlockLocation of the TransportPipesContainer
     * @param world The World the TransportPipesContainer is in
     * @throws Exception if there is already a TransportPipesContainer or Duct at the BlockLocation
     */
    public void registerTransportPipesContainer(TransportPipesContainer container, BlockLocation blockLocation, World world) throws Exception {
        PipeManager pipeManager = (PipeManager) (DuctManager<?>) ductRegister.baseDuctTypeOf("pipe").getDuctManager();
        if (pipeManager.getContainerAtLoc(blockLocation.toLocation(world)) != null || globalDuctManager.getDuctAtLoc(world, blockLocation) != null) {
            throw new Exception("The given location is not empty");
        }
        pipeManager.getContainers(world).put(blockLocation, container);

        for (TPDirection dir : TPDirection.values()) {
            Duct duct = globalDuctManager.getDuctAtLoc(world, blockLocation.getNeighbor(dir));
            if (duct instanceof Pipe) {
                globalDuctManager.updateDuctConnections(duct);
                globalDuctManager.updateDuctInRenderSystems(duct, true);
            }
        }
    }

    /**
     * Unregister a TransportPipesContainer
     * @param blockLocation The BlockLocation of the TransportPipesContainer
     * @param world The World the TransportPipesContainer is in
     * @throws Exception if there is no TransportPipesContainer at the BlockLocation
     */
    public void unregisterTransportPipesContainer(BlockLocation blockLocation, World world) throws Exception {
        PipeManager pipeManager = (PipeManager) (DuctManager<?>) ductRegister.baseDuctTypeOf("pipe").getDuctManager();
        TransportPipesContainer container = pipeManager.getContainerAtLoc(blockLocation.toLocation(world));
        if (container == null) {
            throw new Exception("There is no TransportPipesContainer at the given location");
        }
        pipeManager.getContainers(world).remove(blockLocation);

        for (TPDirection dir : TPDirection.values()) {
            Duct duct = globalDuctManager.getDuctAtLoc(world, blockLocation.getNeighbor(dir));
            if (duct instanceof Pipe) {
                globalDuctManager.updateDuctConnections(duct);
                globalDuctManager.updateDuctInRenderSystems(duct, true);
            }
        }
    }

    /**
     * Gets the current TransportPipes TPS
     * @return The current TPS
     */
    public int getTPS() {
        return threadService.getCurrentTPS();
    }

    /**
     * Gets the preferred TransportPipes TPS
     * @return The preferred TPS
     */
    public int getPreferredTPS() {
        return threadService.getPreferredTPS();
    }

    /**
     * Adds or removes a vanilla Container Block
     * @param block The Block to update
     * @param placed True to add the container block, False to remove it
     */
    public void updateVanillaContainerBlock(Block block, boolean placed) {
        tpContainerListener.updateContainerBlock(block, placed, true);
    }

    /**
     * Get the Duct at a BlockLocation
     * @param blockLocation The BlockLocation of the Duct
     * @param world The World the Duct is in
     * @return The Duct at the BlockLocation in the World
     */
    public Duct getDuct(BlockLocation blockLocation, World world) {
        return globalDuctManager.getDuctAtLoc(world, blockLocation);
    }

    /**
     * Get the number of Ducts in a World
     * @param world The World to check
     * @return The number of Ducts in the World
     */
    public int getDuctCount(World world) {
        return globalDuctManager.getDucts(world).size();
    }

    /**
     * Get the registered TransportPipesContainers
     * @param world The World to get the TransportPipesContainers in
     * @return A ConcurrentSkipListMap of registered TransportPipesContainers in the World. Key is BlockLocation, Value is TransportPipesContainer.
     */
    public ConcurrentSkipListMap<BlockLocation, TransportPipesContainer> getRegisteredContainers(World world) {
        return ((PipeManager) (DuctManager<? extends Duct>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).getContainers(world);
    }

    /**
     * Get the registered TransportPipesContainers at a Location
     * @param location The Location to get the TransportPipesContainer at
     * @return The TransportPipesContainer at the Location or Null if there isn't one
     */
    public TransportPipesContainer getContainerAtLocation(Location location) {
        return ((PipeManager) (DuctManager<? extends Duct>) ductRegister.baseDuctTypeOf("pipe").getDuctManager()).getContainerAtLoc(location);
    }

    /**
     * Get the TransportPipesAPI Instance
     * @return TransportPipesAPI Instance
     * @throws Exception if TransportPipes is not yet initialized
     */
    public static TransportPipesAPI getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("TransportPipes is not yet initialized");
        }
        return instance;
    }
}
