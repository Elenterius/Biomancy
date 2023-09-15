<p align="center">
<img src="media/v2/text_logo_big.png" alt="Biomancy" height="150px"/>
<h1 align="center">Biomancy</h1>
</p>

<p align="center">
<a aria-label="Build Status" href="https://github.com/Elenterius/Biomancy/actions/workflows/gh_release.yml">
<img alt="" src="https://img.shields.io/github/actions/workflow/status/Elenterius/Biomancy/gh_release.yml?logo=github&style=for-the-badge"></a>

<a aria-label="Latest Release" href="https://github.com/Elenterius/Biomancy/releases/">
<img alt="" src="https://img.shields.io/github/v/release/elenterius/biomancy?include_prereleases&logo=github&style=for-the-badge"></a>

<a aria-label="Project Tracker" href="https://github.com/orgs/Creative-Chasm/projects/2/">
<img alt="" src="https://img.shields.io/badge/-Project-black?style=for-the-badge&logo=github"></a>

<a aria-label="Downloads on CurseForge" href="https://www.curseforge.com/minecraft/mc-mods/biomancy">
<img alt="" src="https://cf.way2muchnoise.eu/full_492939_downloads(f16436-f16436-fff-000-fff).svg?badge_style=for_the_badge"></a>

<a aria-label="Downloads on Modrinth" href="https://modrinth.com/mod/biomancy">
<img alt="" src="https://img.shields.io/modrinth/dt/uAAuyU4M?logo=modrinth&style=for-the-badge&color=1bd96a&labelColor=&logoColor=1bd96a"></a>

<a aria-label="Biomancy Discord" href="https://discord.gg/424awTDdJJ">
<img alt="" src="https://img.shields.io/discord/920005236645572662?logo=discord&logoColor=white&color=5865F2&label=&style=for-the-badge"></a>

<a aria-label="Donate" href="https://ko-fi.com/elenterius">
<img alt="" src="https://img.shields.io/badge/support me on Ko--fi-F16061?logo=ko-fi&logoColor=white&style=for-the-badge"></a>
</p>

Biomancy is magi-tech Mod for Minecraft. The mod is inspired by Biopunk and Bio-Manipulation and features a fleshy art
style.

* [Download]
* [Discord]
* [Wiki]
* [Trello]

## Tech Stack

- [MinecraftForge](https://github.com/MinecraftForge/MinecraftForge) (modding API for Minecraft)
- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) (mixin and bytecode weaving framework)
- [GeckoLib](https://github.com/bernie-g/geckolib) (animation library)

### Integration with other Mods

- [JEI]

## Credits

Special thanks to **RhinoW** for artwork and game design help.

View full [List of Contributors](CREDITS.md)

## License

All code is licensed under the [MIT License](https://opensource.org/licenses/MIT).

All artwork (images, textures, models, animations, etc.) is licensed under
the [Creative Commons Attribution-NonCommercial 4.0 International License](http://creativecommons.org/licenses/by-nc/4.0/)
, unless stated otherwise.

## Development

### Setup

- open the repository with IntelliJ IDEA
- wait for gradle project import to finish, you might have to cancel it if it gets stuck and reload the gradle project
- run gradle task genIntellijRuns (`Tasks > forgegradle runs > genIntellijRuns`) to generate all run configurations

> [!info] The repository will not contain most resource and data pack assets.
> These need to be generated on demand and requires you to run
> the `runData` **configuration** to generate the missing recipes, tags and other things.
> You can checkout the `datagen` source-set to see which assets are generated.

### Maven

Atm there is no dedicated maven, you can use https://www.cursemaven.com/ to include the mod as a dependency.

### Contributing

Pull Request are welcome.

For new features or major changes related to the **gameplay** or **art style** please [join our Discord][Discord] and
request to join the dev team.<br>
This will give you access to the private mod development channels and resources such as the biomancy design document and
concept board.

You can track the development progress via our [Trello Board][Trello].

This project uses **Conventional Commits Messages** (https://www.conventionalcommits.org/en/v1.0.0/) to automatically
genereate
changelogs on release and to determine the semantic version bump.

## Support
If you need help feel free to [join our Discord][Discord].

## User Guide
The mod provides no ingame guide book but uses tooltip descriptions & flavor texts instead. If you need further information you can read the github [Wiki].

Read the [Getting Started Guide](https://github.com/Elenterius/Biomancy/wiki/v2/Getting-Started) section if you don't know what to do at all.

### Recipes
To conveniently look up recipes ingame I recommend the use of the [JEI] mod.


[Download]: https://www.curseforge.com/minecraft/mc-mods/biomancy
[Discord]: https://discord.gg/424awTDdJJ
[Wiki]: https://github.com/Elenterius/Biomancy/wiki/v2
[Trello]: https://trello.com/b/GUKjOSAl
[JitPack]: https://jitpack.io/#Elenterius/Biomancy

[JEI]:https://www.curseforge.com/minecraft/mc-mods/jei
