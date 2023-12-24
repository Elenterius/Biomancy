## [2.8.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.7.0...1.19.2-v2.2.8.0) (2023-12-24)


### Features

* **acid:** reduce probability for acid particles to produce fizzling smoke when landing on ground ([df8e315](https://github.com/Elenterius/Biomancy/commit/df8e31512cc59eada311f5d7584daec4d6673e53))
* add bloomlight recipe ([2de5134](https://github.com/Elenterius/Biomancy/commit/2de51343c0140e8fe6dbc827f1c3020371e55eb0))
* buff strength of `FleshyBone` material (increase destroy time from 3 to 4 and explosion resistance from 3 to 6) ([9b7bf5c](https://github.com/Elenterius/Biomancy/commit/9b7bf5cd45e08f88da93ba9984335c455095a8ae))
* change Primordial Lantern recipe to require a Sapberry ([33f3c25](https://github.com/Elenterius/Biomancy/commit/33f3c25cff5bad886656e8cbf55a0a032c0824d9))
* change the block material of Flesh Pillar, Chiseled Flesh and Ornate Flesh to `FleshyBone` material ([2c9074a](https://github.com/Elenterius/Biomancy/commit/2c9074af6775f61db591d025e6864940f4d07894))
* **flesh-mound:** add chamber decorations such as pillars, orifices, etc. ([8f0bdb2](https://github.com/Elenterius/Biomancy/commit/8f0bdb2c836c63dd6c7d47e6f61957c652de53f9))
* increase explosion resistance of Packed Flesh from 6 to 12 ([cf05380](https://github.com/Elenterius/Biomancy/commit/cf05380ee3e8eb7b9a921517984657cc364b2f18))
* **item-tag:** add `cannot_be_eaten_by_cradle` tag for items that should not be eaten by the Cradle ([52d2b60](https://github.com/Elenterius/Biomancy/commit/52d2b60e3f61c75ea849404549019fc09247d74f))
* **item-tag:** add items to forge tags for doors, trapdoors and chests ([138f2f1](https://github.com/Elenterius/Biomancy/commit/138f2f1ed6a68eaa4e6af9ea461f14faf340b53b))
* make bony flesh blocks play flesh or bone sounds (with equal probability) ([648650c](https://github.com/Elenterius/Biomancy/commit/648650c3ee001f232fedc7ffb9bf71bf655234ee))
* make primal orifices milk-able with empty buckets ([2949671](https://github.com/Elenterius/Biomancy/commit/2949671e004e770ec831bed03fd68e294c532010))
* tweak decomposing recipe of bloomlight ([844b217](https://github.com/Elenterius/Biomancy/commit/844b21763b9dcc55673f87a79c20a45a3c572c81))


### Bug Fixes

* prevent storage sac from being eaten by the Cradle ([d1e9c66](https://github.com/Elenterius/Biomancy/commit/d1e9c663a22917063e89459384582d468d16dad1))

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

