## [6.0.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.5.2.0...1.20.1-v2.6.0.0) (2024-04-12)


### ⚠ BREAKING CHANGES

* refactor living tools
* refactor nutrients api
* rework Nutrients handling and add fluid fuel support

### Features

* add crafting recipes for smooth and porous variants of primal flesh ([e96fd38](https://github.com/Elenterius/Biomancy/commit/e96fd3830415322d1fcf2ef5d9046f2dc0907dd3))
* add distinct block model for Primal Orifices that are half full ([8deb708](https://github.com/Elenterius/Biomancy/commit/8deb7087f66a90bec3451bbe2e89c629579cc985))
* add Gastric Acid Juice ([8fd0003](https://github.com/Elenterius/Biomancy/commit/8fd00034e4f1860ac00ba4ac759e14d924ca38e0))
* add not_removable_with_cleansing_serum mob-effect tag ([803eed5](https://github.com/Elenterius/Biomancy/commit/803eed5f70c6745b4a3567f31d86bacb06b6b9c0))
* add The Thorn shield ([4047282](https://github.com/Elenterius/Biomancy/commit/40472820b5277a88c92dd1aae24154bc373285aa))
* add tooltips to primal items ([048aec0](https://github.com/Elenterius/Biomancy/commit/048aec0c5cd32b16c8c1f458ccfd325506ec68fb))
* **buff:** change the Ravenous Claws damage from generic to bleed ([4351898](https://github.com/Elenterius/Biomancy/commit/43518981d59950e4e91eddd2eb646395a2b6e955))
* **buff:** make bleed and acid damage bypass Invincibility-Frames ([6c3ff9d](https://github.com/Elenterius/Biomancy/commit/6c3ff9d9e5278a8fbdb9c082d871a478a9f0d7ff))
* change attack reach indicator of Ravenous Claws ([9179f7b](https://github.com/Elenterius/Biomancy/commit/9179f7bd4e227c8740b14bae8f017a3a9deef0f8))
* change nutrients recipes ([6562bdb](https://github.com/Elenterius/Biomancy/commit/6562bdbf7a899610fbe9a1033286cc209ac81276))
* improve tooltips looks & readability by removing the horizontal rule bar ([e346026](https://github.com/Elenterius/Biomancy/commit/e346026caaed5e0a822983adaf78482b54b8e19f))
* increase anomaly spawn chance of Cradle by 2% ([f78f3ba](https://github.com/Elenterius/Biomancy/commit/f78f3baab6faf1c3adac4b83fd0e9645f90a601b))
* make all bio-machines fluid fuel consumers (IFluidHandler Capability) ([a9e1cf9](https://github.com/Elenterius/Biomancy/commit/a9e1cf9fbc4476cbe7fcb28e0e6cd6f048337805))
* make Essence Anemia effect non-removable ([434f631](https://github.com/Elenterius/Biomancy/commit/434f6310d45525a14f1771c904c6d1bca4a283da))
* make it possible to also collect acid from Primal Orifices with glass bottles ([945252e](https://github.com/Elenterius/Biomancy/commit/945252e9477a2639fa8000a24165ef20917e50e1))
* make ModularLarynx capable of playing ambient, hurt and death sounds ([6fb18a4](https://github.com/Elenterius/Biomancy/commit/6fb18a476dd8f97a7069b09ad73a79ef4b822ab5))
* make The Thorn shield a living tool ([e416561](https://github.com/Elenterius/Biomancy/commit/e416561c9c7c7a79e063d63046b761ac4b91ab8a))
* move the ravenous claws hud element below the crosshair ([693a108](https://github.com/Elenterius/Biomancy/commit/693a10857f979910a10729b3c110cfc8908c15c3))
* **nerf:** consume extra nutrients/charge when triggering abilities of the Ravenous Claws ([d90c76b](https://github.com/Elenterius/Biomancy/commit/d90c76b8a0ae99082910361be0d47f8c5e462455))
* **nerf:** drastically reduce nutrients fuel value of everything ([ac6ace6](https://github.com/Elenterius/Biomancy/commit/ac6ace6845999bed25ab00f2176255498400d024))
* reduce hostile spawn chance of Cradle by 15% ([4e49e03](https://github.com/Elenterius/Biomancy/commit/4e49e0347969226e8a60cedb202ab6ab365be786))
* rename Acid fluid to Gastric Acid ([1ed0a9d](https://github.com/Elenterius/Biomancy/commit/1ed0a9dd74d7b0ccc18182a599cc695ee77702b0))
* show nutrient fuel value of items inside bio-machine GUIs ([2cc5ab0](https://github.com/Elenterius/Biomancy/commit/2cc5ab0f4335eca95f780d554c89d99973525bdf))
* tweak Essence Anemia effect color ([a2c0868](https://github.com/Elenterius/Biomancy/commit/a2c0868d16e990dec3b987c1e4750f2413f66294))
* update Modular Larynx model ([d39c444](https://github.com/Elenterius/Biomancy/commit/d39c44484f653c464b72c954b82a09538f7e3ea2))


### Bug Fixes

* fix extractor item animation playing on all other extractor items when looking at a mob ([dc4f2b1](https://github.com/Elenterius/Biomancy/commit/dc4f2b175cdc593f4ff5a141ecf24d446d2e5663))
* fix missing acid bucket pickup sounds ([2bdcc61](https://github.com/Elenterius/Biomancy/commit/2bdcc610294b52e47c3eb058a7e81f742313ce57))
* fix missing translation for fleshkin eat sounds ([d2791aa](https://github.com/Elenterius/Biomancy/commit/d2791aa84578200be06e2d745d0b3da8af397196))
* try to fix the rare issue where the Cradle Chamber is unable to form a floor by lowering Acid Chambers by 0.25 units ([6760c47](https://github.com/Elenterius/Biomancy/commit/6760c47d7253e25594159bbf69530fccdfd1065b))


### Miscellaneous Chores

* refactor living tools ([9e6f0db](https://github.com/Elenterius/Biomancy/commit/9e6f0db6be607f6382ffa518848c498c8f3dd948))
* refactor nutrients api ([3e1f68f](https://github.com/Elenterius/Biomancy/commit/3e1f68f7c4c59291a5348a523a32b726a73e0c98))
* rework Nutrients handling and add fluid fuel support ([20a2682](https://github.com/Elenterius/Biomancy/commit/20a2682130d9243060e8779e5cb299d8989c0fdb))

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

