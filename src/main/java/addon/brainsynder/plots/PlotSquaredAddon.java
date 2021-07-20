package addon.brainsynder.plots;

import com.google.common.collect.Lists;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import simplepets.brainsynder.addon.AddonConfig;
import simplepets.brainsynder.addon.AddonPermissions;
import simplepets.brainsynder.addon.PermissionData;
import simplepets.brainsynder.addon.PetAddon;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.event.entity.PetEntitySpawnEvent;
import simplepets.brainsynder.api.event.entity.PetMoveEvent;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.debug.DebugBuilder;

import java.util.List;

@Namespace(namespace = "PlotSquared")
public class PlotSquaredAddon extends PetAddon {
    private PermissionData masterBypass, spawnMaster, spawnRoad, spawnUnclaimed, moveMaster, moveRoad, moveUnclaimed, moveDenied;

    private String missingPermission;

    private boolean checkSpawningRoads = false;
    private boolean checkSpawningUnclaimed = false;
    private boolean checkMovingRoads = false;
    private boolean checkMovingUnclaimed = false;
    private boolean checkMovingDenied = false;

    private boolean removePet = true;

    private boolean checkSpawning = true;
    private boolean checkMoving = true;


    @Override
    public void init() {
        AddonPermissions.register(this, masterBypass);
        AddonPermissions.register(this, spawnMaster, spawnRoad);
        AddonPermissions.register(this, spawnMaster, spawnUnclaimed);
        AddonPermissions.register(this, moveMaster, moveRoad);
        AddonPermissions.register(this, moveMaster, moveUnclaimed);
        AddonPermissions.register(this, moveMaster, moveDenied);
    }

    @Override
    public boolean shouldEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if ((plugin != null) && plugin.isEnabled()) return true;
        SimplePets.getDebugLogger().debug(DebugBuilder.build(getClass()).setLevel(SimplePets.ADDON).setMessages(
                "You seem to be missing the PlotSquared plugin...",
                "Download it here: https://www.spigotmc.org/resources/77506/"
        ));
        return false;
    }

    @Override
    public void loadDefaults(AddonConfig config) {
        config.addDefault("Missing-Permission", "&cYou are missing the &7{permission} &cpermission",
                "This is the message that will be sent if a pet fails to spawn due to missing a permission\n" +
                        "It will show up when you hover the mouse over the failed message");
        config.addDefault("spawning-checks.enabled", false,
                "Should there be ANY checks for when a pet is spawned");
        config.addDefault("spawning-checks.roads", false,
                "Should the addon check if the player can spawn the pet on the road");
        config.addDefault("spawning-checks.unclaimed", false,
                "Should the addon check if the player can spawn the pet on an unclaimed plot");

        config.addDefault("move-checks.enabled", true,
                "Should there be ANY checks for when a pet is moving");
        config.addDefault("move-checks.remove-pet", true,
                "If any of the checks fail, should the pet in question be removed?");
        config.addDefault("move-checks.roads", false,
                "Should the addon check if the pet can move/walk on the road");
        config.addDefault("move-checks.unclaimed", false,
                "Should the addon check if the pet can move/walk on an unclaimed plot");
        config.addDefault("move-checks.denied", true,
                "Should the addon check if the pet can move/walk on a plot their owner is denied on");

        config.addDefault("bypass-permissions.master", "pet.plotsquared.bypass",
                "This is the master permission, Will ignore all individual bypass permissions listed below");
        config.addDefault("bypass-permissions.spawn.master", "pet.plotsquared.spawn",
                "This is the master permission for when a pet is spawned, Will ignore all individual bypass permissions listed below");
        config.addDefault("bypass-permissions.spawn.road", "pet.plotsquared.spawn.road",
                "This is the bypass permission for when a pet is spawned on the road, If a player has this they will be allowed to spawn the pet on the road");
        config.addDefault("bypass-permissions.spawn.unclaimed", "pet.plotsquared.spawn.unclaimed",
                "This is the bypass permission for when a pet is spawned on an unclaimed plot, If a player has this they will be allowed to spawn the pet on an unclaimed plot");

        config.addDefault("bypass-permissions.move.master", "pet.plotsquared.move",
                "This is the master permission for when a pet is moving, Will ignore all individual bypass permissions listed below");
        config.addDefault("bypass-permissions.move.road", "pet.plotsquared.move.road",
                "This is the bypass permission for when a pet is moving on the road");
        config.addDefault("bypass-permissions.move.unclaimed", "pet.plotsquared.move.unclaimed",
                "This is the bypass permission for when a pet is moving on an unclaimed plot");
        config.addDefault("bypass-permissions.move.denied", "pet.plotsquared.move.denied",
                "This is the bypass permission for when a pet is moving on a plot the owner is denied on");

        missingPermission = config.getString("Missing-Permission", "&cYou are missing the &7{permission} &cpermission");

        checkSpawning = config.getBoolean("spawning-checks.enabled", true);
        checkSpawningRoads = config.getBoolean("spawning-checks.roads", false);
        checkSpawningUnclaimed = config.getBoolean("spawning-checks.unclaimed", false);

        checkMoving = config.getBoolean("move-checks.enabled", true);
        removePet = config.getBoolean("move-checks.remove-pet", true);
        checkMovingRoads = config.getBoolean("move-checks.roads", false);
        checkMovingUnclaimed = config.getBoolean("move-checks.unclaimed", false);
        checkMovingDenied = config.getBoolean("move-checks.denied", true);


        masterBypass = new PermissionData(config.getString("bypass-permissions.master", "pet.plotsquared.bypass"))
                .setDescription("This is the master permission, Will ignore all individual bypass permissions listed below");
        spawnMaster = new PermissionData(config.getString("bypass-permissions.spawn.master", "pet.plotsquared.spawn"))
                .setDescription("This is the master permission for when a pet is spawned, Will ignore all individual bypass permissions listed below");
        spawnRoad = new PermissionData(config.getString("bypass-permissions.spawn.road", "pet.plotsquared.spawn.road"))
                .setDescription("This is the bypass permission for when a pet is spawned on the road, If a player has this they will be allowed to spawn the pet on the road");
        spawnUnclaimed = new PermissionData(config.getString("bypass-permissions.spawn.unclaimed", "pet.plotsquared.spawn.unclaimed"))
                .setDescription("This is the bypass permission for when a pet is spawned on an unclaimed plot, If a player has this they will be allowed to spawn the pet on an unclaimed plot");

        moveMaster = new PermissionData(config.getString("bypass-permissions.move.master", "pet.plotsquared.move"))
                .setDescription("This is the master permission for when a pet is moving, Will ignore all individual bypass permissions listed below");
        moveRoad = new PermissionData(config.getString("bypass-permissions.move.road", "pet.plotsquared.move.road"))
                .setDescription("This is the bypass permission for when a pet is moving on the road");
        moveUnclaimed = new PermissionData(config.getString("bypass-permissions.move.unclaimed", "pet.plotsquared.move.unclaimed"))
                .setDescription("This is the bypass permission for when a pet is moving on an unclaimed plot");
        moveDenied = new PermissionData(config.getString("bypass-permissions.move.denied", "pet.plotsquared.move.denied"))
                .setDescription("This is the bypass permission for when a pet is moving on a plot the owner is denied on");
    }

    @Override
    public void cleanup() {
    }

    @Override
    public double getVersion() {
        return 0.2;
    }

    @Override
    public String getAuthor() {
        return "brainsynder";
    }

    @Override
    public List<String> getDescription() {
        return Lists.newArrayList(
                "&7This addon links into the PlotSquared plugin",
                "&7to allow/deny certain pet events from happening",
                "&7on certain parts of a plot world (road/unclaimed/denied)"
        );
    }

    @EventHandler
    public void onSpawn(PetEntitySpawnEvent event) {
        if (!checkSpawning) return;
        Player player = event.getUser().getPlayer();
        org.bukkit.Location location = event.getUser().getPlayer().getLocation();

        Location loc = Location.at(location.getWorld().getName(), BlockVector3.at(location.getX(), location.getY(), location.getZ()), location.getYaw(), location.getPitch());
        PlotArea area = PlotSquared.get().getPlotAreaManager().getPlotArea(loc);

        // Not a plots world
        if (area == null) return;

        Plot plot = area.getPlot(loc);

        // Road
        if (plot == null) {
            if (!checkSpawningRoads) return;
            if (AddonPermissions.hasPermission(this, player, masterBypass.getPermission())) return;
            if (AddonPermissions.hasPermission(this, player, spawnRoad.getPermission())) return;
            event.setCancelled(true, missingPermission.replace("{permission}", spawnRoad.getPermission()));
            return;
        }

        // Unclaimed plot
        if (!plot.hasOwner()) {
            if (!checkSpawningUnclaimed) return;
            if (AddonPermissions.hasPermission(this, player, masterBypass.getPermission())) return;
            if (AddonPermissions.hasPermission(this, player, spawnUnclaimed.getPermission())) return;
            event.setCancelled(true, missingPermission.replace("{permission}", spawnUnclaimed.getPermission()));
        }

        // I don't check if the player is denied here, because the denied player cant enter the plot anyway
    }

    @EventHandler
    public void onMove(PetMoveEvent event) {
        if (!checkMoving) return;
        Player player = event.getEntity().getPetUser().getPlayer();
        org.bukkit.Location location = event.getEntity().getEntity().getLocation();

        Location loc = Location.at(location.getWorld().getName(), BlockVector3.at(location.getX(), location.getY(), location.getZ()), location.getYaw(), location.getPitch());
        PlotArea area = PlotSquared.get().getPlotAreaManager().getPlotArea(loc);

        // Not a plots world
        if (area == null) return;

        Plot plot = area.getPlot(loc);

        // Road
        if (plot == null) {
            if (!checkMovingRoads) return;
            if (AddonPermissions.hasPermission(this, player, masterBypass.getPermission())) return;
            if (AddonPermissions.hasPermission(this, player, moveRoad.getPermission())) return;
            if (removePet) {
                event.getEntity().getPetUser().removePet(event.getEntity().getPetType());
                return;
            }
            event.setCancelled(true);
            return;
        }

        // Unclaimed plot
        if (!plot.hasOwner()) {
            if (!checkMovingUnclaimed) return;
            if (AddonPermissions.hasPermission(this, player, masterBypass.getPermission())) return;
            if (AddonPermissions.hasPermission(this, player, moveUnclaimed.getPermission())) return;
            if (removePet) {
                event.getEntity().getPetUser().removePet(event.getEntity().getPetType());
                return;
            }
            event.setCancelled(true);
        }

        // Denied Plot
        if (!plot.isDenied(player.getUniqueId())) return; // The owner of the pet is not denied

        if (!checkMovingDenied) return;
        if (AddonPermissions.hasPermission(this, player, masterBypass.getPermission())) return;
        if (AddonPermissions.hasPermission(this, player, moveDenied.getPermission())) return;
        if (removePet) {
            event.getEntity().getPetUser().removePet(event.getEntity().getPetType());
            return;
        }
        event.setCancelled(true);
    }
}
