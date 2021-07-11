# PlotSquaredAddon
This is an addon for the SimplePets v5 plugin.
This allows the plugin to link into the PlotSquared plugin to allow/deny certain events (spawn/move)

### Requirements:
- [PlotSquared](https://www.spigotmc.org/resources/34315/) Plugin `(At least v6.0.0-SNAPSHOT)`

### Default configuration (Located in `plugins/SimplePets/Addons/config/PlotSquared.yml`)
```yaml
spawning-checks:
  # Should there be ANY checks for when a pet is spawned
  enabled: true
  # Should the addon check if the player can spawn the pet on the road
  roads: false
  # Should the addon check if the player can spawn the pet on an unclaimed plot
  unclaimed: false
  
move-checks:
  # Should there be ANY checks for when a pet is moving
  enabled: true
  # If any of the checks fail, should the pet in question be removed?
  remove-pet: true
  # Should the addon check if the pet can move/walk on the road
  roads: false
  # Should the addon check if the pet can move/walk on an unclaimed plot
  unclaimed: false
  # Should the addon check if the pet can move/walk on a plot their owner is denied on
  denied: true
  
bypass-permissions:
  # This is the master permission, Will ignore all individual bypass permissions listed below
  master: pet.plotsquared.bypass
  spawn:
    # This is the master permission for when a pet is spawned, Will ignore all individual bypass permissions listed below
    master: pet.plotsquared.spawn
    # This is the bypass permission for when a pet is spawned on the road, If a player has this they will be allowed to spawn the pet on the road
    road: pet.plotsquared.spawn.road
    # This is the bypass permission for when a pet is spawned on an unclaimed plot, If a player has this they will be allowed to spawn the pet on an unclaimed plot
    unclaimed: pet.plotsquared.spawn.unclaimed
  move:
    # This is the master permission for when a pet is moving, Will ignore all individual bypass permissions listed below
    master: pet.plotsquared.move
    # This is the bypass permission for when a pet is moving on the road
    road: pet.plotsquared.move.road
    # This is the bypass permission for when a pet is moving on an unclaimed plot
    unclaimed: pet.plotsquared.move.unclaimed
    # This is the bypass permission for when a pet is moving on a plot the owner is denied on
    denied: pet.plotsquared.move.denied

```
