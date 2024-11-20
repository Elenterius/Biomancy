## [8.20.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.19.0...1.20.1-v2.8.20.0) (2024-11-20)


### Features

* add compatibility for Farmer's Delight and Alex's Delight ([8ed787f](https://github.com/Elenterius/Biomancy/commit/8ed787f73eeb14fb47614742e9ec10ecbc54f36e))
* add decomposing recipe for withered bones in the #forge:bones/wither item tag ([aafedfa](https://github.com/Elenterius/Biomancy/commit/aafedfab7dd523dab422049f259132ced636cd6c))
* add decomposing recipes for the IceAndFire mod ([a818d60](https://github.com/Elenterius/Biomancy/commit/a818d60cc73e7f81ca2aab86b4f6bbc5d7dbaab6))
* add despoil loot for mobs from the Ice And Fire mod ([2ce8144](https://github.com/Elenterius/Biomancy/commit/2ce814451a7cab51c73b5ac83c2a5a806526d4de))
* add digesting recipes for the IceAndFire Mod ([3ab1430](https://github.com/Elenterius/Biomancy/commit/3ab14309f331c3e0de771ef00d5c3a642497f9ba))
* add IceAndFire items to Biomancy tags ([b1548e7](https://github.com/Elenterius/Biomancy/commit/b1548e7d8d400b242ea347b889358a0d5fd756b7))
* add item tag #forge:bones/wither and make it a valid Cradle tribute ([8f01d5a](https://github.com/Elenterius/Biomancy/commit/8f01d5a94cfa8d0f5612b4427ba733e009e61cdb))
* make dragon hearts, dragon blood, hydra heart and etc. valid Cradle tributes ([b970e7f](https://github.com/Elenterius/Biomancy/commit/b970e7fefd6a89322bb0755a38147333f3de87c3))
* re-balance cradle tributes of foods with life energy ([5dad166](https://github.com/Elenterius/Biomancy/commit/5dad166c0cf89764906a4b995cfaa7a1938a5d57))
* resolve recipe conflicts with item and tag based ingredients by making recipe matching of machines biased towards item ingredients ([72ac6d3](https://github.com/Elenterius/Biomancy/commit/72ac6d3ee4a6678024aa34833a6713ad1acc466b))
* rework flesh chicken visuals with model and texture by TheShroome ([9dadee7](https://github.com/Elenterius/Biomancy/commit/9dadee7055fb3f656e2c3526749b13e559e70513))
* rework flesh pig visuals with model and texture by TheShroome ([37986bc](https://github.com/Elenterius/Biomancy/commit/37986bccebaa2b4c3671e1d07c7c28f1c7e5e997))
* update Chinese (zh_cn) translation ([96b4835](https://github.com/Elenterius/Biomancy/commit/96b48351d35bcceaf308492ee99431cf24285da4))

## [8.19.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.18.0...1.20.1-v2.8.19.0) (2024-11-06)


### Features

* change how nutrient cost is visualized ([ceea183](https://github.com/Elenterius/Biomancy/commit/ceea183be5e510d0e172434ca23a7f7423f65277))
* refactor fluid tribute and nutrients api ([ae5ebe4](https://github.com/Elenterius/Biomancy/commit/ae5ebe42d9e9e4483f0179ecca65be51c4fa970b))
* updated Exotic Flesh Mix description ([205a59d](https://github.com/Elenterius/Biomancy/commit/205a59d0033a94af25609218257759b0c9c037d4))


### Bug Fixes

* fix ability to insert fuel from the side into the vial slot of the bio-lab ([7f6bb5b](https://github.com/Elenterius/Biomancy/commit/7f6bb5bfa1ca8af39f5c014c2dc49f5211a453b7)), closes [#154](https://github.com/Elenterius/Biomancy/issues/154)
* fix machines not displaying nutrient cost ([10135f9](https://github.com/Elenterius/Biomancy/commit/10135f93f46563c0037a5e1c0a3f62cc1cfd7fc5))

## [8.18.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.17.0...1.20.1-v2.8.18.0) (2024-11-01)


### Features

* add acolyte helmet upgrade for viewing cradle information ([f9ec5b8](https://github.com/Elenterius/Biomancy/commit/f9ec5b87776d38611145b1fd2c011f671e4e0842))
* add EssenceIngredient to fix rendering issues with Essence in the Bio-Forge ([d314c4f](https://github.com/Elenterius/Biomancy/commit/d314c4f42ec82be7d81dbf45ac6bf2876bbc3009))
* add fluid api for cradle tributes ([c36ddeb](https://github.com/Elenterius/Biomancy/commit/c36ddebb03941d15e09a65e2e5d373f11d106c71))
* add ghost item rendering for lockable bio-lab slots ([7152061](https://github.com/Elenterius/Biomancy/commit/7152061a5a63fc49e60e7120262831e3a150f09d))
* add JEI crafting examples for player specific Biometric Membrane ([06955c0](https://github.com/Elenterius/Biomancy/commit/06955c06235a3b25a441a702f4f4cb1c814b90a5))
* deobfuscate cradle text overlay ([64bdd3f](https://github.com/Elenterius/Biomancy/commit/64bdd3f217fab6ca996eae598ee7062d3678fe06))
* display nutrients cost in machine GUIs ([e06ee55](https://github.com/Elenterius/Biomancy/commit/e06ee55a55937718246f1edcf40429bebc6f6d18))
* improve handling of potion sacrifices, calculate the value based on the potion duration and level instead of using predefined values. ([0469953](https://github.com/Elenterius/Biomancy/commit/0469953514bd43e7b5b3ee9d5cb586a9628eb1c3))
* make it possible to sneak click Vial Holders to bulk insert or extract vials ([51719b4](https://github.com/Elenterius/Biomancy/commit/51719b4c382cd03889711e3489051b7efb03eb74))
* make milk bucket and honey bottle valid cradle tributes ([354d3df](https://github.com/Elenterius/Biomancy/commit/354d3dfdb38f077609cdd2f19d408ee7cf8013be))
* **nerf:** only show cradle info overlay when the acolyte helmet has nutrients ([da6bcbe](https://github.com/Elenterius/Biomancy/commit/da6bcbed5bc8fcde10ebaeccdc1e567964f44bbf))
* **nerf:** re-balance bulk acid digesting by making it slower and reducing the output amount by the crafting cost amount ([a74e8ad](https://github.com/Elenterius/Biomancy/commit/a74e8ad57624d14f1d2fabb3e43c34e3985ddb18))
* re-balance some digesting recipes and nerf warped wart block recipe ([bf819cd](https://github.com/Elenterius/Biomancy/commit/bf819cd1266a490ed68fcc938475d3c6297f8949))
* reorganize recipe ids ([7077e18](https://github.com/Elenterius/Biomancy/commit/7077e1893eb88959483bed58ec2b17beb27c6a58))
* tweak raw meats tag ([b77abd1](https://github.com/Elenterius/Biomancy/commit/b77abd122a898e11b424e126e3f65a48a79bc44a))
* update Alex's Caves compatibility and make all candy biome blocks replaceable by flesh growth ([f6e4919](https://github.com/Elenterius/Biomancy/commit/f6e4919b806b8347251604cddc0f4f8b5d064635))
* update Biometric Membrane tooltip ([690ee27](https://github.com/Elenterius/Biomancy/commit/690ee27e207901d3e88848cf4318c7ded610d7a4))


### Bug Fixes

* fix biometric membrane recipe allowing invalid items to be consumed ([7428e4e](https://github.com/Elenterius/Biomancy/commit/7428e4e92fb28dbe07269de2799f77e05cb357de))
* fix trading advancement ([ae2f0e6](https://github.com/Elenterius/Biomancy/commit/ae2f0e64ed54709b3de3c4a3333be7210a4f3f9e))

## [8.17.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.16.0...1.20.1-v2.8.17.0) (2024-10-17)


### Features

* give the friendly primordial flesh blob the ability to use Cradles from further distances ([03d007b](https://github.com/Elenterius/Biomancy/commit/03d007b26849b6cc107ae4dfd7d2d9b55342419a))
* make primordial flesh blobs immune to acid ([8323a66](https://github.com/Elenterius/Biomancy/commit/8323a664070e2342e323396588aa5b7a9f66b329))
* re-balance ravenous claws, increase base damage by from 3.5 to 4, decrease attack speed from 4 to 3.5 and make attacks ignore invincibility frames only with full attack strength ([83249ea](https://github.com/Elenterius/Biomancy/commit/83249ea64dae7293fc4843d8e88c1f9b7ea8ac46))
* refactor fluid fuel handling & api ([b4abfc9](https://github.com/Elenterius/Biomancy/commit/b4abfc9f030b763faad1135c2e75f0e3e8c9fd9d))
* remove integration for the Create mod and move it into a standalone addon mod ([376bd3d](https://github.com/Elenterius/Biomancy/commit/376bd3df958219aa779c7ac46a0292220d5ccd03))

## [8.16.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.15.0...1.20.1-v2.8.16.0) (2024-10-14)


### Features

* add bulk Digestion of items in Gastric Acid ([aba3a91](https://github.com/Elenterius/Biomancy/commit/aba3a91c87d4b550e15ad28661c59e7c5614d9ff))
* **buff:** increase max stack size of serums from 8 to 16 ([97bbff2](https://github.com/Elenterius/Biomancy/commit/97bbff27a0183a29b6db1bfd35341362cfec86fb))
* make all BoP flesh blocks decomposable ([4ec514f](https://github.com/Elenterius/Biomancy/commit/4ec514f2c573bc4647113d0dba837fc5635f6a09))
* make input slots of Bio-Lab lockable ([80c6f5e](https://github.com/Elenterius/Biomancy/commit/80c6f5e01322427f8babbe645b3e144812b27288))
* **nerf:** make frenzy effect consume food ([e6656c5](https://github.com/Elenterius/Biomancy/commit/e6656c5b0cbad19e5e0c977198058b60a61ef168))
* rebrand Corrosive Additive to Decaying Additive ([2ae27b1](https://github.com/Elenterius/Biomancy/commit/2ae27b13505288f6051d4fa1d93d99defdd658b2))
* tweak bio-lab recipes ([406a056](https://github.com/Elenterius/Biomancy/commit/406a056fe59037802943736c7e7a33edb52a743a))
* tweak withdrawal effect side effects, add poison side effect and buff withdrawal duration reduction from eating sugary food ([79d565c](https://github.com/Elenterius/Biomancy/commit/79d565c9a1725b5ac1625a3af605781ffba2c44e))


### Bug Fixes

* don't render outer skin layer when wearing acolyte armor ([1d49205](https://github.com/Elenterius/Biomancy/commit/1d4920599e20cbf505f5d78eba4a2efa2efefd96))
* fix crash when drinking milk under the frenzy effect ([06d6929](https://github.com/Elenterius/Biomancy/commit/06d69297fc4be64d8b24be28b38949378d112c65))

