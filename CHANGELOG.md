## [7.2.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.7.1.0...1.20.1-v2.7.2.0) (2024-05-24)


### Features

* add rudimentary placeholder icons for Caustic Gunblade ([533e2cc](https://github.com/Elenterius/Biomancy/commit/533e2ccdd84cf85a5e2cbe80580cc4e3652f094e))
* crossbow hold Caustic Gunblade in ranged mode ([5cf5541](https://github.com/Elenterius/Biomancy/commit/5cf5541c9c4f84fefd08a1c1215c5584a8d23df2))
* only deal acid melee damage when Caustic Gunblade is coated in acid ([c4aa926](https://github.com/Elenterius/Biomancy/commit/c4aa926edc690ad834f191a413c8534b24d21fa2))
* show ammo amount status on Caustic Gunblade model ([b734692](https://github.com/Elenterius/Biomancy/commit/b734692cfd9485337edd3b3a608d3847eb8aac65))
* tweak Caustic Gunblade attack speed ([abebc1b](https://github.com/Elenterius/Biomancy/commit/abebc1b27ec828680e06efc057c12d8729a11ad9))
* tweak reload behavior of Caustic Gunblade ([fdc3ad7](https://github.com/Elenterius/Biomancy/commit/fdc3ad7dc8da272371508d4d4ed63ef3e9d8388f))
* use nutrients for Caustic Gunblade durability ([58a79a6](https://github.com/Elenterius/Biomancy/commit/58a79a662b678bc774aa924e50e816a0be227ea8))


### Bug Fixes

* fix Caustic Gunblade animation not showing the acid coat ([684a509](https://github.com/Elenterius/Biomancy/commit/684a5096f4ff1a2a6317b3788d4298d9e42fbf6f))

## [7.1.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.7.0.0...1.20.1-v2.7.1.0) (2024-05-22)


### Features

* add Acid Coat ability to Caustic Gunblade ([c0d7fe0](https://github.com/Elenterius/Biomancy/commit/c0d7fe0953fe4e54a6b39194320b1541c43d2687))
* remove ability to place Acid Fluid with the Caustic Gunblade ([9ffe742](https://github.com/Elenterius/Biomancy/commit/9ffe742f3e6ad07d1f859157eb3d98808ce6c021))


### Bug Fixes

* fix mixin incompatibility with Apothic-Attributes mod ([1c8a57c](https://github.com/Elenterius/Biomancy/commit/1c8a57cd69f5a25ee27f32b8e1d8ef46f31d838d))

## [7.0.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.6.0.0...1.20.1-v2.7.0.0) (2024-05-20)


### ⚠ BREAKING CHANGES

* refactor tributes
* refactor recipe handling and replace all static food digesting recipes with one dynamic recipe

### Features

* add Acolyte Armor ([ef8e594](https://github.com/Elenterius/Biomancy/commit/ef8e59461180e7b78a048eee56d00662d728bc59))
* add Bio-Alchemical Epidermis ability to Acoylte Armor ([31a6cc4](https://github.com/Elenterius/Biomancy/commit/31a6cc49c05fc0abc9e169f0b43690a67004813b))
* add Caustic Gunblade ([e90444a](https://github.com/Elenterius/Biomancy/commit/e90444aedeffcbc6016834ba955d67a303af36a5))
* add proper flesh tongue comments ([14e2256](https://github.com/Elenterius/Biomancy/commit/14e22566a90492fe916047f3cbb44ec090bec1e6))
* **buff:** increase fuel value for Nutrient Paste (2 -> 3) and Nutrient Bar (18 -> 27) ([04a6bd0](https://github.com/Elenterius/Biomancy/commit/04a6bd0b0823681e79c30c620d05f3ded2634801))
* make the durability bar always visible for living tools without max nutrients ([8b0131a](https://github.com/Elenterius/Biomancy/commit/8b0131ac130c0dd1516b82abb91f414f08f22c91))
* re-balance digesting recipes (nerf cocoa beans, bamboo, melon, pumpkin, cake, etc.) ([a2fdc35](https://github.com/Elenterius/Biomancy/commit/a2fdc3569518f90b868841b89f0ca18e7391dd64))
* refactor recipe handling and replace all static food digesting recipes with one dynamic recipe ([52b5a5e](https://github.com/Elenterius/Biomancy/commit/52b5a5e1d53cf51285a065e360bea6a9e64e7056))
* refactor tributes ([ad49699](https://github.com/Elenterius/Biomancy/commit/ad496998442c72791da6c496e20cbe3c17ebfc94))
* remove experimental Toxicus & Bile-Spitter items ([45a3e4f](https://github.com/Elenterius/Biomancy/commit/45a3e4fc28db8095f71b9bbd2ba7d8fdfb271d49))
* slightly tweak cradle behavior and limit Cradle attack on failure to sacrifices with high hostility ([0a3728e](https://github.com/Elenterius/Biomancy/commit/0a3728e661fb841db46c632b9afece16a9faf8ff))
* tweak decomposing recipes (nerf golden carrot & golden apple; buff magma cream) ([f5ccf4d](https://github.com/Elenterius/Biomancy/commit/f5ccf4d5047b9c6349ebbcd00a520f394fd59762))

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

