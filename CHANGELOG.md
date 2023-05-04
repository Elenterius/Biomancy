## [1.3.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.1.0...1.19.2-v2.1.3.0) (2023-05-04)


### Features

* add enchantment descriptions for "Enchantment Descriptions" mod ([60aca25](https://github.com/Elenterius/Biomancy/commit/60aca25d065aecb50da82e21071bf689776aadc8))
* add Flesh Pillar ([537bc1d](https://github.com/Elenterius/Biomancy/commit/537bc1dc7ed2ab87c0d53484342201cd57aaa8c5))
* add Primordial Core ([0424223](https://github.com/Elenterius/Biomancy/commit/04242232eef5f61a482902935c4aedd7d4cb0c9d))
* change Armor Shred Effect to reduce armor by 1 point per level ([82573cf](https://github.com/Elenterius/Biomancy/commit/82573cf265f7a0c8a64ea846103d8169e7e66f9c))
* make despoil enchantment applicable to items with the knives tag ([f927d57](https://github.com/Elenterius/Biomancy/commit/f927d570a07ed37f42d16ea5b0f8bfc9f743e7f5))
* render Primordial Cradle eye overlay at full brightness ([bd1e02a](https://github.com/Elenterius/Biomancy/commit/bd1e02ad73d2e98e52e5010828b50f16b19e44fe))
* replace bone cleaver with despoiling sickle ([68882e8](https://github.com/Elenterius/Biomancy/commit/68882e8a6e2c35ef0fafd347a1b63afd9be0ef90))
* retexture Component Items ([426f973](https://github.com/Elenterius/Biomancy/commit/426f973660af9dbe827f0d200bd8a6e66d18479a))
* retexture Enlargement and Shrinking Serums ([190b9d4](https://github.com/Elenterius/Biomancy/commit/190b9d4ade72cb88b75a63f1cbee3eb3423706cd))
* retexture Exotic Flesh Mix ([8ba0657](https://github.com/Elenterius/Biomancy/commit/8ba06570255d4d6b00f53bdd0ecd50a9c081c64b))
* retexture Fertilizer ([629079b](https://github.com/Elenterius/Biomancy/commit/629079b1804d47c4c45949d9f65dcb1dcaf1b542))
* retexture Glass Vial ([bd8559c](https://github.com/Elenterius/Biomancy/commit/bd8559c528d53a2cfebf0868b05e1ab1f3f3b215))
* retexture Living Flesh Item ([39b423b](https://github.com/Elenterius/Biomancy/commit/39b423b5eabfd7164af6d0acafd6249e6525c55e))
* retexture Packed Flesh ([989028f](https://github.com/Elenterius/Biomancy/commit/989028f65c9104ff4a608f5f0c7a83ea81612f8d))
* retexture Serums, Compounds and Additives ([831a9f5](https://github.com/Elenterius/Biomancy/commit/831a9f50dddd7b4cd23ef1117fe495e823c8b64e))
* rework & nerf Despoil Loot Modifier to only drop loot for killing mobs with the despoil enchantment ([c3a2793](https://github.com/Elenterius/Biomancy/commit/c3a279378c34c1f5069f8dedfc630125f5e48bdc))
* trigger item sacrifice advancements for thrown items thrown into the Cradle by a player ([214ca4e](https://github.com/Elenterius/Biomancy/commit/214ca4efefef0ade47b36ab493f8d9956e84c4cf))
* tweak advancement texts ([98bdd54](https://github.com/Elenterius/Biomancy/commit/98bdd54ee8fd55cf43b05ec3ebd1e58b682d727a))
* update flesh spike models and texture ([6e84fd7](https://github.com/Elenterius/Biomancy/commit/6e84fd7eee2be6b2303f5ec970271ff051116c1d))


### Bug Fixes

* fix advancements not firing for sacrificing item in the cradle ([9566f3d](https://github.com/Elenterius/Biomancy/commit/9566f3dd21f4d6fe4562f24a504a0aaac267fe17))
* fix Cradle sacrifices not increasing biomass and life energy at the same time ([f035f9e](https://github.com/Elenterius/Biomancy/commit/f035f9edd93c29c117b1a0185c8daae05e4036fc))
* potentially fix concurrent modification exception when getting voxel shapes for certain blocks ([b0a0eb2](https://github.com/Elenterius/Biomancy/commit/b0a0eb22161f8a57a77b60c67a170918228327af))
* remove ability to insert storage container blocks/items into the storage sac on right clicking the sac ([762e10e](https://github.com/Elenterius/Biomancy/commit/762e10e59aeb7b765eccc6c7a9d0736045e93896))


### Performance Improvements

* avoid creation of objects during the renderer phase of the Cradle ([36442f1](https://github.com/Elenterius/Biomancy/commit/36442f1a8ea756a70c96e58b04ae5216210ffc11))
* refactor intermediary key cache ([5ac54fb](https://github.com/Elenterius/Biomancy/commit/5ac54fb691d0fade66971daca94ab1bdad521ce5))

## [1.1.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.0.0...1.19.2-v2.1.1.0) (2023-04-25)


### Features

* make Cleansing Serum able to remove forced age from applicable mobs ([908c7ef](https://github.com/Elenterius/Biomancy/commit/908c7ef86fea34248a103eb27ec42d43ba2bae90))
* rework Spike block ([4138a5b](https://github.com/Elenterius/Biomancy/commit/4138a5b36389e6d6848495a9b9b433a3717108ec))


### Bug Fixes

* fix Bio-Forge GUI crashing the game when clicking next page button ([4046464](https://github.com/Elenterius/Biomancy/commit/40464647129b6077d70f58bc96db9e7555ba1413))

## [1.0.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.0.53.2...1.19.2-v2.1.0.0) (2023-04-18)


### ⚠ BREAKING CHANGES

* change serum class structure and serum registry
* reorganize package structure

### Bug Fixes

* fix ageing and rejuvenation serum interaction with Tadpoles & Frogs ([af655a6](https://github.com/Elenterius/Biomancy/commit/af655a61385b76731142e01aad3af5c6ee336159))


### Miscellaneous Chores

* change serum class structure and serum registry ([e4f72b0](https://github.com/Elenterius/Biomancy/commit/e4f72b06ed63daa6c4d3f2e528d4c627544d7099))
* reorganize package structure ([98eac84](https://github.com/Elenterius/Biomancy/commit/98eac844d56e443b9d79b02e33ce716b219dfc9f))

### [0.53.2](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.0.53.1...1.19.2-v2.0.53.2) (2023-04-11)


### Bug Fixes

* fix crash on server startup ([4988deb](https://github.com/Elenterius/Biomancy/commit/4988deb88a3e0ff90024ac2dd76ce3d24835c102))

### [0.53.1](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.0.53.0...1.19.2-v2.0.53.1) (2023-04-10)


### Bug Fixes

* fix placement logic of maw hopper allowing it to connect to invalid neighbors ([1cc2f72](https://github.com/Elenterius/Biomancy/commit/1cc2f72a5016d0925282140dfd3316856c961492))

