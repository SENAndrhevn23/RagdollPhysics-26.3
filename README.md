# Ragdoll Physics — Minecraft Java 26.3

An experimental Fabric mod that applies an articulated physics simulation to the local player's visible third-person model.

## v0.1.2 — render partial-tick compatibility fix

- Fixed the client mixin compile error by obtaining interpolation progress from `Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)` instead of reading a nonexistent `AvatarRenderState.partialTick` field in the current Fabric compile environment.

## v0.1.1 — visible player ragdoll prototype

- Press **R** to start/stop the local ragdoll simulation.
- Press **F5** to switch to third-person view so you can see your player body.
- The simulation uses a 15-node humanoid rig, Verlet integration, distance constraints, gravity and simple floor contact.
- **Complete** mode is loose and limp. **Active** mode adds a spring-like righting force toward the standing pose.
- The physics pose is applied after vanilla player animations, including sleeves, jacket and pants overlays.
- The configured mode is read from `config/ragdollphysics.json`.
- GitHub Actions builds and uploads the mod JAR.

## Important prototype limitations

This is now a visible, simulated model pose, but it is not yet a full replacement for Minecraft's player entity physics. The player still uses vanilla collision and movement; the ragdoll rig currently has simple ground contact rather than full block-shape collision. The simulation is client-side and local-player-only, so other players do not see the same ragdoll yet. In first-person, Minecraft renders the hand/item view instead of the full player model; use **F5** to inspect the full-body ragdoll.

## Toolchain

- Minecraft: `26.3`
- Java: `25`
- Fabric Loader: `0.20.1`
- Fabric Loom: `1.19-SNAPSHOT`
- Fabric API: `0.166.0+26.3`
- Gradle: `9.8.2`

## Build locally

You need JDK 25, Gradle 9.8.2, and an internet connection for the first build. The repository workflow installs Gradle 9.8.2 and runs the command below.

```bash
gradle --no-daemon clean build
```

The compiled mod is placed in `build/libs/`.

## Configure the mode

After the first launch, edit:

```text
.minecraft/config/ragdollphysics.json
```

Set `physicsMode` to either `ACTIVE` or `COMPLETE`:

```json
{
  "physicsMode": "COMPLETE"
}
```

## GitHub Actions

Push the repository to GitHub, open **Actions**, and download the `ragdoll-physics-mc26.3` artifact from a successful build.

## Next milestones

- `v0.2.0` — proper block collision, player movement hand-off and recovery controls.
- `v0.3.0` — active balance improvements and first-person arm pose.
- `v0.4.0` — zombie ragdolls and network synchronisation.
- `v1.0.0` — settings screen, broader entity support and optimisation.

## License

MIT. See `LICENSE`.
