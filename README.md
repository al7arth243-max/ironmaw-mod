# Ironmaw (Forge 1.20.1)

Craft a **Disease Sample** and right-click your tamed wolf with it to turn the wolf into an **Ironmaw**.

## Requirements
- Minecraft 1.20.1 with Forge 47.x
- GeckoLib 4.4.x for Forge 1.20.1 (4.4.9 recommended). This mod uses GeckoLib to draw the model and play its animations, so players need GeckoLib installed too.

## Build the jar
1. Install JDK 17. If Gradle can't find one, it downloads one for you.
2. Open a terminal in this folder and run:
   - Windows: `gradlew build`
   - macOS/Linux: `./gradlew build`
3. The mod jar is created at `build/libs/ironmaw-1.0.0.jar`. Put that jar and the GeckoLib jar in your `mods` folder.

The first build downloads Minecraft and Forge, so it takes a few minutes.
To test in a dev client, run `gradlew runClient`.
To open the project in IntelliJ IDEA, open `build.gradle` as a project.

## Gameplay
**Recipe (shaped):**
```
Rotten Flesh | Bone       | Rotten Flesh
Bone         | Spider Eye | Bone
Rotten Flesh | Bone       | Rotten Flesh
```

**Transformation:** right-click an adult wolf that you've tamed while holding the sample. The new Ironmaw:
- keeps the wolf's owner, name, collar colour, and sitting/standing state
- starts at full health
- uses up one sample (except in Creative)

**Behaviour:** the Ironmaw extends vanilla `Wolf`, so it works exactly like a tamed wolf.
Right-click it to make it sit or stand. It follows you, teleports to you, defends you, attacks what you attack,
and heals when you feed it meat.

| Stat            | Tamed wolf | Ironmaw |
|-----------------|-----------:|--------:|
| Health          | 20         | 60      |
| Attack damage   | 4          | 9       |
| Armor           | 0          | 6       |
| Knockback res.  | 0          | 60%     |
| Speed           | 0.30       | 0.32    |
| Hitbox (w × h)  | 0.6 × 0.85 | 1.1 × 1.2 (polar bear: 1.4 × 1.4) |

**Animations:** idle, walk (plays faster when it runs), a new **sit** pose, and the tongue lash, which plays whenever it lands a bite.

## Tweaking
- Stats: the constants at the top of `entity/IronmawEntity.java`
- Model size: `MODEL_SCALE` in `client/IronmawRenderer.java` (default 0.4). Change the hitbox in `registry/ModEntities.java` to match.
- Recipe: `src/main/resources/data/ironmaw/recipes/disease_sample.json`

## Building on GitHub
Every push runs the **Build** workflow in `.github/workflows/build.yml`.
To download the finished jar, open the **Actions** tab, click the latest run, and download **ironmaw-mod** under *Artifacts*.
