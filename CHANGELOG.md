## [2.7.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.6.0...1.19.2-v2.2.7.0) (2023-12-11)


### Features

* **acid:** add acid fluid ([61da539](https://github.com/Elenterius/Biomancy/commit/61da539e64936ad6d8020c95a4e408d67d60cd7c))
* **acid:** add dripping acid particle ([dd32f9d](https://github.com/Elenterius/Biomancy/commit/dd32f9de834d39906799c357f77702a563a7c2a2))
* **acid:** add fluid compat for open pipes from Create ([dc17391](https://github.com/Elenterius/Biomancy/commit/dc173912e85795326cc80b94c2fb209ae9314b20))
* **acid:** add fluid interaction with lava & water ([050bf47](https://github.com/Elenterius/Biomancy/commit/050bf47bf08ab59297716c84f380f21cb57be3e7))
* add primal orifice block ([02a2c84](https://github.com/Elenterius/Biomancy/commit/02a2c84e141bba509855cba92da8bebb30367b75))
* change chiseled flesh block/texture ([be7e5d9](https://github.com/Elenterius/Biomancy/commit/be7e5d9273be27dd16849993419b1b580dea206e))
* **flesh-mound:** make Malignant Bloom spawn less likely inside the mound and more likely at the edges or outside ([3acb916](https://github.com/Elenterius/Biomancy/commit/3acb91623456fb86f247a1dfb60857cc79d1976c))
* **flesh-mound:** tweak default mound gen settings ([7f06b15](https://github.com/Elenterius/Biomancy/commit/7f06b15508f5c86aa85836a3ce391c88fba4084f))


### Bug Fixes

* **flesh-mound:** fix flesh veins being unable to find the flesh mound when other types of shapes exist in the same area ([8750164](https://github.com/Elenterius/Biomancy/commit/8750164116ab17518679a615ed8b2e545ec79392))
* **gradle:** fix build configuration not including the api sourceset in the primary build artifact ([95c32e7](https://github.com/Elenterius/Biomancy/commit/95c32e7f04836a4a0189b2b8321e4e4a9910dc67))

## [2.6.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.5.0...1.19.2-v2.2.6.0) (2023-12-09)


### Features

* add neural interceptor block ([4ecc094](https://github.com/Elenterius/Biomancy/commit/4ecc09448bedd3d2c3fcb9b7ba1f7a417d3991aa))
* add primal membrane block for living mobs ([a0ea6e8](https://github.com/Elenterius/Biomancy/commit/a0ea6e87eb5a29014a5f9a24bedc7cc057615d81))
* add undead-permeable membrane ([0902a3b](https://github.com/Elenterius/Biomancy/commit/0902a3b936ae782a4b43cc75591124c3c2a630f7))
* **flesh-mound:** add bloomlight block as flesh type replacement of shroomlight in mounds ([bdfa12f](https://github.com/Elenterius/Biomancy/commit/bdfa12f363b72874f1009d1d8f3b80218f74e98a))
* **flesh-mound:** allow flesh mounds to excavate chambers and convert the destroyed blocks into primal energy ([593f9c9](https://github.com/Elenterius/Biomancy/commit/593f9c92971a9e8fdb198aec97fbd8d02a4c0b03))
* **flesh-mound:** create "Doors" between two adjacent chambers with Primal Membranes ([2680d0e](https://github.com/Elenterius/Biomancy/commit/2680d0ed802d75c0abb892627be786a64051e0cb))
* **flesh-mound:** improve storage and lookup of mound shapes ([e127cf6](https://github.com/Elenterius/Biomancy/commit/e127cf622b0aac3eb1f892f3dcc02421e1386929))
* **flesh-mound:** make mound shapes persist across unloaded chunks and store mound shape seed in primordial cradle block/item ([32c2efd](https://github.com/Elenterius/Biomancy/commit/32c2efd8a74fdea3e45cd67d231d27217ae2e042))
* **flesh-mound:** prevent flesh veins from eating living flesh ([36b6e65](https://github.com/Elenterius/Biomancy/commit/36b6e65e96f8c0cc1eb2d973ba99dd16585b1481))
* **flesh-mound:** prevent natural spawning of mobs inside flesh mounds ([0af5924](https://github.com/Elenterius/Biomancy/commit/0af592432e1ac3dc7f071ec3c3419242ce94328a))


### Bug Fixes

* **flesh-mound:** fix unintentional MoundShape removal when the Cradle BlockEntity is unloaded ([a5cb46b](https://github.com/Elenterius/Biomancy/commit/a5cb46b5f2a8112de9a240bf1b85b61ba02cb0db))

## [2.5.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.4.0...1.19.2-v2.2.5.0) (2023-11-19)


### Features

* add block tags to explicitly allow/disallow attachment of flesh veins to specific blocks ([77c1fee](https://github.com/Elenterius/Biomancy/commit/77c1feee17f736fe2a6768f24b210e072077aedd))
* **flesh-mound:** add first iteration of the flesh mound growth to the primordial cradle ([8175714](https://github.com/Elenterius/Biomancy/commit/8175714e23668e19e0685edfa14e2b40f9fc22f8))
* **flesh-mound:** decrease volume of growth ([e528ba8](https://github.com/Elenterius/Biomancy/commit/e528ba833adb903db90877fc5d93e1183a55e281))

## [2.4.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.3.0...1.19.2-v2.2.4.0) (2023-11-11)


### Features

* **buff:** increase primal energy conversion of sacrifices in the Primordial Cradle ([b3923db](https://github.com/Elenterius/Biomancy/commit/b3923dba38bd909bdea04c02629715670bdd6059))
* make overworld logs replaceable by malignant veins ([f02208e](https://github.com/Elenterius/Biomancy/commit/f02208e10a1fb539dea67bb597fd2d0abc00f306))
* make Primordial Cradle block items display their internal sacrifice information in the tooltip ([f244c70](https://github.com/Elenterius/Biomancy/commit/f244c70ce325787061a9b684390893e889c0a9bc))
* **nerf:** decrease Malignant Bloom spawn rate ([dc1beb9](https://github.com/Elenterius/Biomancy/commit/dc1beb9a0eab70859865005cab529a04087bfc88))


### Bug Fixes

* fix flesh veins inability to spread over dirt paths ([b319e1e](https://github.com/Elenterius/Biomancy/commit/b319e1ee1a16c876064b2bb15483d61f2b7ca335))
* fix Primordial Cradle not persisting internal sacrifice values when mined ([ac17061](https://github.com/Elenterius/Biomancy/commit/ac17061f805f24fa20ccb32e6633631bb3b01b9e))

## [2.3.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.2.0...1.19.2-v2.2.3.0) (2023-11-03)


### Features

* add config for enabling unlimited flesh growth globally ([e28dc68](https://github.com/Elenterius/Biomancy/commit/e28dc68b22fe2d0d0cea75f81659a3b5063a965d))
* add config for enabling unlimited flesh growth near cradles ([826c988](https://github.com/Elenterius/Biomancy/commit/826c9881aeeb9edfd77d0678fca95fa68a448eb7))
* add server config for BioForge recipe unlocking and villager trades ([47a8b12](https://github.com/Elenterius/Biomancy/commit/47a8b120adcf69ed83ebd239cd467d371b35fb6c))
* increase life/primal energy value of sacrificed Nether Stars tenfold ([320cc67](https://github.com/Elenterius/Biomancy/commit/320cc67f0a1a3c399c976e695cd9e01460e4092f))
* make malignant flesh veins replaceable ([c574f3d](https://github.com/Elenterius/Biomancy/commit/c574f3d0fc337b1f87cffe5c603862f15a0bb295))
* make wandering trader trades less expensive ([0eb8f80](https://github.com/Elenterius/Biomancy/commit/0eb8f808b43f4d13cfe6f8ccd755a80808577e2f))
* remove infinite flesh growth near cradles (distance < 8) ([50df048](https://github.com/Elenterius/Biomancy/commit/50df048376bb4cec33912cc760e4ba834c71e3bc))
* remove legacy item tags for biomass ([dd31596](https://github.com/Elenterius/Biomancy/commit/dd31596e1634a9cfa5fff61855c02e9b246933c9))

