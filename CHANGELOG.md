## [5.2.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.5.1.0...1.20.1-v2.5.2.0) (2024-04-02)


### Features

* add Biometric Membrane ([914cccb](https://github.com/Elenterius/Biomancy/commit/914cccb4e44973c3a43457d8da963b26cbe8b4d5))
* add Chrysalis Block ([49f04db](https://github.com/Elenterius/Biomancy/commit/49f04db5de9ffad23d0fcaa3d693034128bbb355))
* add Mob Essence ([63c59ea](https://github.com/Elenterius/Biomancy/commit/63c59ea143b58bbe25c4b6d866959975f77eb238))
* add mob Essence Extractor ([99d8c7b](https://github.com/Elenterius/Biomancy/commit/99d8c7b49d30689587ffbaa342cdea2e675eec88))
* add Modular Larynx ([63e55a2](https://github.com/Elenterius/Biomancy/commit/63e55a210bd02f97bf6c5c056847a9282bf3828a))
* add Surgical Precision enchantment ([710f3ed](https://github.com/Elenterius/Biomancy/commit/710f3ed9d992dc0ab1ba9fcac464f7231101ea18))
* make Primal Bloom mineable with shears or silk-touch ([891e340](https://github.com/Elenterius/Biomancy/commit/891e340b71f5623d69b31bc797fbece45621b05b))
* make Suspicious Stew a valid source of life energy for the Primordial Cradle ([1710d5b](https://github.com/Elenterius/Biomancy/commit/1710d5b9b89c4f181133c1ea310e9a2b5bf768bc)), closes [#125](https://github.com/Elenterius/Biomancy/issues/125)
* **nerf:** decrease value of Suspicious Stew for cradle sacrifices ([5e0bd05](https://github.com/Elenterius/Biomancy/commit/5e0bd0514ed48982c784034688177627a4d2447b))


### Bug Fixes

* fix custom shader being applied incorrectly ([5e9e458](https://github.com/Elenterius/Biomancy/commit/5e9e45883b2b95c134bc3eced46d9880772c5ac3))
* fix the glowing eye of the Primordial Cradle not displaying the amount life energy correctly ([7ca00fa](https://github.com/Elenterius/Biomancy/commit/7ca00fad03ab0b3b2124feff865f2a6d57e9842a))
* properly fix block tinting issues ([4819c58](https://github.com/Elenterius/Biomancy/commit/4819c586ae61deaca0b443e85bb8f93334354a55))
* remove biomancy's stonecutting recipes from the minecraft namespace ([ac0b552](https://github.com/Elenterius/Biomancy/commit/ac0b5524122846926e6365b7552239b8cf68733c))

## [5.1.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.5.0.0...1.20.1-v2.5.1.0) (2024-03-19)


### Features

* improve filtering of items that can't be eaten by the cradle ([c451857](https://github.com/Elenterius/Biomancy/commit/c451857ec4c75179c4a686d43321e628b5488ba9))
* **nerf:** prevent insertion of Bundles into storage sacs ([5cac542](https://github.com/Elenterius/Biomancy/commit/5cac54258faf894fdbd8bb04c36f0c1d488dd314)), closes [#121](https://github.com/Elenterius/Biomancy/issues/121)
* **nerf:** reduce max stack size of Storage Sac to 1 ([5a3b253](https://github.com/Elenterius/Biomancy/commit/5a3b2530df822b818bd97ed9db0bc78f22faea99))
* tweak advancement ([6565ba0](https://github.com/Elenterius/Biomancy/commit/6565ba0e1db565737d4758abd4a7b071d00575dc))
* tweak Primordial Core recipe ([8b429aa](https://github.com/Elenterius/Biomancy/commit/8b429aac6c433f9a7033d9ea181af8f862e53a10))


### Bug Fixes

* add missing translation strings for death messages ([d4b1dbc](https://github.com/Elenterius/Biomancy/commit/d4b1dbc38377736734dff273b788d2f2970cae2e)), closes [#123](https://github.com/Elenterius/Biomancy/issues/123)
* fix crash when placing down a Primordial Cradle ([f21e77f](https://github.com/Elenterius/Biomancy/commit/f21e77f23cf970d48ae6de9b13c281c64508f365))

## [5.0.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.5.0...1.20.1-v2.5.0.0) (2024-03-16)


### ⚠ BREAKING CHANGES

* removes the following EntityType tags: sharp_fang, sharp_claw, sinew, toxin_gland, volatile_gland, bile_gland, bone_marrow, withered_bone_marrow
* migrate from entity tags to loot-table based despoil loot

### Features

* add ornamental flesh slab ([eea89b4](https://github.com/Elenterius/Biomancy/commit/eea89b4981714320e50ebd0d342d9227425f12e7))
* add Smooth & Porous Primal Flesh variants for blocks, slabs, stairs and walls ([32229cc](https://github.com/Elenterius/Biomancy/commit/32229cca7381b3662c4dddbf62438f0b4f0c4c1a))
* change the requirements of the "Kitty Cat" advancement to also trigger for ocelots ([52ba6ff](https://github.com/Elenterius/Biomancy/commit/52ba6ff9c93981fdb08f739332f3af41a256a0fa))
* include primal flesh variants in flesh mound growth ([3144a5c](https://github.com/Elenterius/Biomancy/commit/3144a5c485bddbb7cb5725422cf05bfe055b150c))
* make despoil loot-table driven where each mob has its own loot-table ([dda22ef](https://github.com/Elenterius/Biomancy/commit/dda22efed27486edd5691301649f28439c8b84df))
* make Primal Bloom placeable on all primal block variants ([44f7f9f](https://github.com/Elenterius/Biomancy/commit/44f7f9f09c0165090c842e362fc9b5a866074528))
* remove entity tags for despoil loot ([f115445](https://github.com/Elenterius/Biomancy/commit/f1154459c62289ee9b3bc306c17689269eee9e1d))


### Bug Fixes

* fix block placement preview render not working for primal & malignant slabs ([8ae13d8](https://github.com/Elenterius/Biomancy/commit/8ae13d8d11e4429fcc942c920f5e38f0b62ed780))
* fix Fleshkin Chests being breakable by block breaker machines (e.g. Create Drill) ([cc70b33](https://github.com/Elenterius/Biomancy/commit/cc70b3377606b9789a7319b8324fdea904bf60cf)), closes [#109](https://github.com/Elenterius/Biomancy/issues/109)
* fix maw hopper stealing items from villagers & witches ([0167e51](https://github.com/Elenterius/Biomancy/commit/0167e5194ceb21a829008432b73c4320836a6eeb)), closes [#122](https://github.com/Elenterius/Biomancy/issues/122)

## [4.5.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.4.0...1.20.1-v2.4.5.0) (2024-03-06)


### Features

* add backwards compatibility for the removed `biomancy:weapons` bio-forge tab ([9d9550f](https://github.com/Elenterius/Biomancy/commit/9d9550f35f85f9a4c5a58b66fc7eae4ee2e2f013)), closes [#118](https://github.com/Elenterius/Biomancy/issues/118)
* **alexs-caves:** rework decomposer recipe result for sea pigs ([8a4f91d](https://github.com/Elenterius/Biomancy/commit/8a4f91dadf9cf45216cd989368b49a90fa79003c))
* **buff:** make Primordial Core crafting recipe cheaper ([e1158f2](https://github.com/Elenterius/Biomancy/commit/e1158f258de8381e6a8fbbeaeabe845518352f7d))
* change decomposer recipe output for any Eggs from `mineral fragments` to `hormone secretions` ([071d3a2](https://github.com/Elenterius/Biomancy/commit/071d3a2bc3d6ff69a35b355ebcec135a7f9d9297))
* **nerf:** reduce digester recipe output for nether wart blocks ([e123e81](https://github.com/Elenterius/Biomancy/commit/e123e81d50b01f5f143f2e0be61db7fd922cc18d))
* **nerf:** replace Nutrients with Nutrient Paste in crafting recipes ([a6f3d79](https://github.com/Elenterius/Biomancy/commit/a6f3d79feba8a51acbd1cfe36888be0ca7e4ef27))
* remove Nutrients to Nutrient Paste recipe ([f3d006a](https://github.com/Elenterius/Biomancy/commit/f3d006a60a6b4d59cb861d6f0bd3f2384771295f))
* rework starting advancements ([be58fc8](https://github.com/Elenterius/Biomancy/commit/be58fc88442a96b7babc705d9bd37128171e7434))

## [4.4.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.3.0...1.20.1-v2.4.4.0) (2024-02-12)


### Features

* add despoil loot to Alex's Caves mobs ([5da2dd4](https://github.com/Elenterius/Biomancy/commit/5da2dd4a28dadb7344d30aa0fe4a684c946dac4e))
* add thick pane variants of the membrane blocks ([c6696b3](https://github.com/Elenterius/Biomancy/commit/c6696b3c22cc19e5a0629e39806416f2305e2722))
* make Alxe's Mobs eggs digestible ([76d9375](https://github.com/Elenterius/Biomancy/commit/76d9375f58cba3b50aced12cdae2d5e1e4fb7476))
* make Membranes valid windmill sails for Create ([2d8ed8b](https://github.com/Elenterius/Biomancy/commit/2d8ed8b7a4c02649fb04732fd8319d4c8db74db9))
* rename sound events ([7211edf](https://github.com/Elenterius/Biomancy/commit/7211edf2226ac762721729749fb411e51793fd7b))
* update membrane block model & colors ([c2d9174](https://github.com/Elenterius/Biomancy/commit/c2d91744da9acf7c985e068bfb578bdd7111a723))


### Bug Fixes

* add missing sound translations ([7079b3a](https://github.com/Elenterius/Biomancy/commit/7079b3ac28732fa52522bd661f771d284a733af0))
* **data-gen:** fix advancement parsing errors spamming the logs ([750b6e4](https://github.com/Elenterius/Biomancy/commit/750b6e49973ccc4bec8fe75e070559f4da898633)), closes [#115](https://github.com/Elenterius/Biomancy/issues/115)
* fix coloring of vials in the vial holder not always updating and defaulting to white ([4712b27](https://github.com/Elenterius/Biomancy/commit/4712b271869760bceb26c27006482cfbc589e43b))
* fix fertilizer not being consumed and converting dirt into the wrong grass block ([9806d3f](https://github.com/Elenterius/Biomancy/commit/9806d3f0aa9237be118e501c6e1988dd5d2edb41))
* fix missing acid fluid blockstate model file spamming the logs with errors ([d69ecc7](https://github.com/Elenterius/Biomancy/commit/d69ecc75d14bc15e49cc08fc7b0a1ce9f1f6d093))

