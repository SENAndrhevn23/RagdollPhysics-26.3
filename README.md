# Ragdoll Physics — Minecraft 26.3

An experimental Fabric mod project exploring articulated ragdoll physics for Minecraft players and entities.

## Current prototype (v0.1.0)

- Fabric mod bootstrap for Minecraft Java 26.3.
- Press **R** to toggle the client-side ragdoll state.
- The selected physics mode is read from `config/ragdollphysics.json`.
- A small standalone Verlet-style articulated-body solver is included in `physics/` as the foundation for later renderer integration.
- GitHub Actions builds and uploads the mod JAR on pushes, pull requests, and manual runs.

**Important:** this is the first project scaffold. The R key currently toggles and reports the ragdoll state; it does not yet replace the player model with a visible, simulated ragdoll. The physics solver is not yet connected to Minecraft's renderer, player movement, or world collision system.

## Toolchain

- Minecraft: `26.3`
- Java: `25`
- Fabric Loader: `0.20.1`
- Fabric Loom: `1.19-SNAPSHOT`
- Fabric API: `0.166.2+26.3`
- Gradle: `9.8.2`

These values follow Fabric's current 26.3 development template and the latest Fabric API release for 26.3 at project creation time.

## Build locally

You need Java 25 and an internet connection for the first build.

Linux:

```bash
./gradlew clean build
```

Windows:

```bat
gradlew.bat clean build
```

The included `gradlew` scripts are small bootstrappers that download the pinned Gradle distribution the first time they are run. They are not the standard Gradle Wrapper JAR scripts.

The compiled mod is created in:

```text
build/libs/ragdoll-physics-mc26.3-0.1.0.jar
```

## Configure the prototype

After the mod has started once, edit:

```text
.minecraft/config/ragdollphysics.json
```

Example:

```json
{
  "physicsMode": "ACTIVE"
}
```

Supported values:

- `ACTIVE` — intended active-ragdoll behaviour.
- `COMPLETE` — intended fully limp ragdoll behaviour.

The config currently selects the mode label for the prototype. A proper in-game settings screen and the actual two physics behaviours are future work.

## GitHub Actions

The workflow is `.github/workflows/main.yml`. Push the project to GitHub, open the **Actions** tab, and download the `ragdoll-physics-mc26.3` artifact from a successful run.

## Planned milestones

- `v0.1.0` — project scaffold, R-key state toggle, config file, physics solver foundation.
- `v0.2.0` — render an articulated player rig and connect it to the solver.
- `v0.3.0` — player movement hand-off, ground/block collision handling, recovery behaviour.
- `v0.4.0` — active-ragdoll balance controller and additional entity support.
- `v1.0.0` — polished settings screen, multiplayer synchronisation, performance pass.

## License

MIT. See `LICENSE`.
