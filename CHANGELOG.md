## [1.2.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.1.0...1.19.2-v2.1.2.0) (2023-05-02)


### Features

* add enchantment descriptions for "Enchantment Descriptions" mod ([60aca25](https://github.com/Elenterius/Biomancy/commit/60aca25d065aecb50da82e21071bf689776aadc8))
* trigger item sacrifice advancements for thrown items thrown into the Cradle by a player ([214ca4e](https://github.com/Elenterius/Biomancy/commit/214ca4efefef0ade47b36ab493f8d9956e84c4cf))


### Bug Fixes

* fix advancements not firing for sacrificing item in the cradle ([9566f3d](https://github.com/Elenterius/Biomancy/commit/9566f3dd21f4d6fe4562f24a504a0aaac267fe17))
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

