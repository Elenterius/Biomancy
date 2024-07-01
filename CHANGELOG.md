## [8.3.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.2.0...1.20.1-v2.8.3.0) (2024-07-01)


### Features

* add Korean (South Korean) translation by Mexwell12 ([2521dcf](https://github.com/Elenterius/Biomancy/commit/2521dcf21c354fa87b9416a5a01bba1da0315574))


### Bug Fixes

* try to fix StackOverflow caused by Biomancy's ScaleModifier implementation (Pehkui integration) ([712cad0](https://github.com/Elenterius/Biomancy/commit/712cad09c25f4ea86cf3f316fe179bd33dc8d135)), closes [#133](https://github.com/Elenterius/Biomancy/issues/133)

## [8.2.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.1.0...1.20.1-v2.8.2.0) (2024-06-17)


### Features

* add russian translation by VladYslaV ([a9312a9](https://github.com/Elenterius/Biomancy/commit/a9312a96330f7d2cb4f5741ce0a959ee0d3a37fd))


### Bug Fixes

* fix durability display of living armor items ([b72bb72](https://github.com/Elenterius/Biomancy/commit/b72bb72f318a829cfe8f516f4205bee8d44a8b1a))
* remove ability to stack Caustic Gunblade items ([59483f4](https://github.com/Elenterius/Biomancy/commit/59483f456dbffd0b0256c98780ce8e12c8e86552))
* remove unintentional fleshtongue obfuscation of Chrysalis error messages ([bc58ba2](https://github.com/Elenterius/Biomancy/commit/bc58ba2e8412be3ff22c83843d39a519d1e30477))

## [8.1.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.0.0...1.20.1-v2.8.1.0) (2024-05-30)


### Features

* update zh_cn translation ([#131](https://github.com/Elenterius/Biomancy/issues/131)) ([82de348](https://github.com/Elenterius/Biomancy/commit/82de34806e467d569a537dec3d306006f5719ba1))

## [8.0.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.4.0...1.20.1-v2.8.0.0) (2024-05-29)


### ⚠ BREAKING CHANGES

* refactor tributes
* refactor recipe handling and replace all static food digesting recipes with one dynamic recipe
* refactor living tools
* refactor nutrients api
* rework Nutrients handling and add fluid fuel support
* removes the following EntityType tags: sharp_fang, sharp_claw, sinew, toxin_gland, volatile_gland, bile_gland, bone_marrow, withered_bone_marrow
* migrate from entity tags to loot-table based despoil loot

### Features

* add Acid Coat ability to Caustic Gunblade ([8a07dc8](https://github.com/Elenterius/Biomancy/commit/8a07dc81225de79db05d9aa7e7d796dee82ce7c7))
* add Acolyte Armor ([173d431](https://github.com/Elenterius/Biomancy/commit/173d431fdeb649ee782acf764b6f70be8559888f))
* add backwards compatibility for the removed `biomancy:weapons` bio-forge tab ([abf2202](https://github.com/Elenterius/Biomancy/commit/abf2202e6ff232102c9ad6d29dcc600d51989d57)), closes [#118](https://github.com/Elenterius/Biomancy/issues/118)
* add Bio-Alchemical Epidermis ability to Acoylte Armor ([d3d1127](https://github.com/Elenterius/Biomancy/commit/d3d11279aae5d8f5bcf8d7dc198f73347973e08c))
* add Biometric Membrane ([6c469a3](https://github.com/Elenterius/Biomancy/commit/6c469a3d4eb2bf137522c9624bba9e83b3637588))
* add Caustic Gunblade ([7ef4c3d](https://github.com/Elenterius/Biomancy/commit/7ef4c3d6c3c0f9dfbc4158e1d0a3808f298b39e5))
* add Chrysalis Block ([701fc0f](https://github.com/Elenterius/Biomancy/commit/701fc0f1662395068bd3af7ec0ca7aec1746f977))
* add crafting recipe for the removal of Flesh Mound settings from a Primordial Cradle item ([e5155ac](https://github.com/Elenterius/Biomancy/commit/e5155ac07c6f825ce5b9fb3c155fd6b56c7fcc05))
* add crafting recipes for smooth and porous variants of primal flesh ([7d03e2b](https://github.com/Elenterius/Biomancy/commit/7d03e2b44f17e787562a9c5616138c71cfdf6563))
* add distinct block model for Primal Orifices that are half full ([1a69683](https://github.com/Elenterius/Biomancy/commit/1a69683e41d4ac38fd26bd8982e1994d33df6dec))
* add Gastric Acid Juice ([ca96c78](https://github.com/Elenterius/Biomancy/commit/ca96c78b13e1a09111c20ff4d760bf059473eac7))
* add living tools with max nutrients amount to creative tab ([78283de](https://github.com/Elenterius/Biomancy/commit/78283ded285545ea529c3e534cc9985a968ca5c4))
* add Mob Essence ([81d8fc4](https://github.com/Elenterius/Biomancy/commit/81d8fc4ce09635a32185c414aba1f4d0c157bcf6))
* add mob Essence Extractor ([b2e6a07](https://github.com/Elenterius/Biomancy/commit/b2e6a07a2fbe92d66c92c811364fa3266ea76641))
* add Modular Larynx ([d967c71](https://github.com/Elenterius/Biomancy/commit/d967c712b041fa0ed222346d3480d3edf1135f07))
* add not_removable_with_cleansing_serum mob-effect tag ([ed6d65a](https://github.com/Elenterius/Biomancy/commit/ed6d65a4ce243ece8b04330b1517e37b5b22111a))
* add ornamental flesh slab ([c2c717d](https://github.com/Elenterius/Biomancy/commit/c2c717dcfa1c948f75d0efa25c0c4d063d8b1158))
* add proper flesh tongue comments ([2f1107d](https://github.com/Elenterius/Biomancy/commit/2f1107d8547ad5b9e636f311084736b9f2b8ac84))
* add recipes for armor and gunblade ([f21e405](https://github.com/Elenterius/Biomancy/commit/f21e40512d2dbd93c2b71d651dbd4577aa055f28))
* add Recipes for Essence Extractor and Modular Larynx ([b783dcd](https://github.com/Elenterius/Biomancy/commit/b783dcdfe72b5382cc432585d11554554e2a1da6))
* add rudimentary placeholder icons for Caustic Gunblade ([5f1c30c](https://github.com/Elenterius/Biomancy/commit/5f1c30cb3c62c181aebb4e85aed91257eebe448c))
* add Self-Feeding enchantment ([28bdfca](https://github.com/Elenterius/Biomancy/commit/28bdfca54904a5fbfd5bdbbe24e8ff30a6adbeda))
* add Smooth & Porous Primal Flesh variants for blocks, slabs, stairs and walls ([db8c38e](https://github.com/Elenterius/Biomancy/commit/db8c38eb45e1c1c6a9ce4f72da533063dd29be48))
* add Surgical Precision enchantment ([63c2404](https://github.com/Elenterius/Biomancy/commit/63c2404cb4900f1d07f6e0797d4eb0c95fdd9e6a))
* add symbiontic repair enchantment ([b59f6fa](https://github.com/Elenterius/Biomancy/commit/b59f6fa3468392faee805a19697dd6d8f99b5e28))
* add The Thorn shield ([c0cbda7](https://github.com/Elenterius/Biomancy/commit/c0cbda7d74f8ea9ca13ae452c7b6707bf1d26197))
* add tooltips to primal items ([61d5f7c](https://github.com/Elenterius/Biomancy/commit/61d5f7c56a795f85e8dcd9d26e1ce811205187d8))
* add usage information to the Modular Larynx tooltip ([730bd46](https://github.com/Elenterius/Biomancy/commit/730bd461553fa16c47beaa552b2552fedd3ea9c0))
* **alexs-caves:** rework decomposer recipe result for sea pigs ([c9d846d](https://github.com/Elenterius/Biomancy/commit/c9d846d134ee4e53025384f5498a751de7039853))
* **buff:** change the Ravenous Claws damage from generic to bleed ([5e738a5](https://github.com/Elenterius/Biomancy/commit/5e738a568795dfa2f880e88ff0c977d8c2e8f8be))
* **buff:** increase fuel value for Nutrient Paste (2 -> 3) and Nutrient Bar (18 -> 27) ([56e0370](https://github.com/Elenterius/Biomancy/commit/56e0370633a91315dac825c8dfb2ff1768e2c770))
* **buff:** increase nutrients storage for acolyte chestplate and leggings from 200 to 250 ([0ebb48f](https://github.com/Elenterius/Biomancy/commit/0ebb48f26be3a6d46e08c3b20ce21d06a526e888))
* **buff:** increase Ravenous Claws attack damage by 0.5 points ([cf14b23](https://github.com/Elenterius/Biomancy/commit/cf14b23c115b1e0a0abc088e717795521f649c34))
* **buff:** increase repair value of nutrients for living items ([0745169](https://github.com/Elenterius/Biomancy/commit/074516919cbfef7d77ca101e54ce6c58d8739640))
* **buff:** make bleed and acid damage bypass Invincibility-Frames ([4af9092](https://github.com/Elenterius/Biomancy/commit/4af90921e7bf268307b780ad5a9df83851438bf9))
* **buff:** make Primordial Core crafting recipe cheaper ([ed00205](https://github.com/Elenterius/Biomancy/commit/ed0020506f212509619116ecd63b6be4a5d2a011))
* change attack reach indicator of Ravenous Claws ([f5a35aa](https://github.com/Elenterius/Biomancy/commit/f5a35aa5367a1774cbee9b92788c53a2457cf57e))
* change decomposer recipe output for any Eggs from `mineral fragments` to `hormone secretions` ([70b096b](https://github.com/Elenterius/Biomancy/commit/70b096b86dc782c4a918839f6e875ffb50c9e267))
* change how mob sounds are found & store them on mob essence ([4919f0f](https://github.com/Elenterius/Biomancy/commit/4919f0fcd42e492a025fd8fc96460d8204aa5bb4))
* change nutrients recipes ([68a6f0f](https://github.com/Elenterius/Biomancy/commit/68a6f0f4d6776e2e53e130325327772693c2fc66))
* change the requirements of the "Kitty Cat" advancement to also trigger for ocelots ([8bc8696](https://github.com/Elenterius/Biomancy/commit/8bc8696554a8c4db5e0cee1e20a12a265cf5213a))
* crossbow hold Caustic Gunblade in ranged mode ([3e3acd0](https://github.com/Elenterius/Biomancy/commit/3e3acd0ff3665ff6d30985d4cc12c52d72b92291))
* improve filtering of items that can't be eaten by the cradle ([20d96c3](https://github.com/Elenterius/Biomancy/commit/20d96c3c2482ab1386908e5492b66da2b0493091))
* improve tooltips looks & readability by removing the horizontal rule bar ([7e57616](https://github.com/Elenterius/Biomancy/commit/7e576164aa9536603657302e24257d4cae6b6d44))
* improve wording of tooltip hint for showing more info ([a650a4e](https://github.com/Elenterius/Biomancy/commit/a650a4e9566da9126eea87b7e00c2659cfc35c98))
* include primal flesh variants in flesh mound growth ([e90cca9](https://github.com/Elenterius/Biomancy/commit/e90cca9d3a602f080455287f0e8584eeb52563ad))
* increase anomaly spawn chance of Cradle by 2% ([b757c6c](https://github.com/Elenterius/Biomancy/commit/b757c6c6ac978f5c3de9f742889402bca7374ece))
* make all bio-machines fluid fuel consumers (IFluidHandler Capability) ([5255124](https://github.com/Elenterius/Biomancy/commit/5255124f133c7d332489896d53d5a921410cdd07))
* make despoil loot-table driven where each mob has its own loot-table ([94594ab](https://github.com/Elenterius/Biomancy/commit/94594ab1aeceef1163823851fe1fc6e30817a1d2))
* make Essence Anemia effect non-removable ([0cf8dd6](https://github.com/Elenterius/Biomancy/commit/0cf8dd6e30d1f896b3e7897f4995102e789d2f1a))
* make it possible to also collect acid from Primal Orifices with glass bottles ([3e72747](https://github.com/Elenterius/Biomancy/commit/3e727475fbc5b7196bc0ebb3488bb4498bd2ac6d))
* make minor tweaks to advancements ([0624a07](https://github.com/Elenterius/Biomancy/commit/0624a07ec19393699e9c4affdd5d804dfebfa060))
* make ModularLarynx capable of playing ambient, hurt and death sounds ([e63cf24](https://github.com/Elenterius/Biomancy/commit/e63cf248fb06384a59a0b5ab2bfa78abeb7a38b8))
* make Primal Bloom mineable with shears or silk-touch ([6107eb5](https://github.com/Elenterius/Biomancy/commit/6107eb5eddc3b84ae49d9bf78c5d2f2179ff184c))
* make Primal Bloom placeable on all primal block variants ([6da7fec](https://github.com/Elenterius/Biomancy/commit/6da7feca612252a6abca473445d2dc2222686375))
* make Suspicious Stew a valid source of life energy for the Primordial Cradle ([460119e](https://github.com/Elenterius/Biomancy/commit/460119ec753a366f7ceed2b23096bc08f35ab228)), closes [#125](https://github.com/Elenterius/Biomancy/issues/125)
* make the durability bar always visible for living tools without max nutrients ([3f9f310](https://github.com/Elenterius/Biomancy/commit/3f9f31024b29f3484d61640fc1727d536c244618))
* make The Thorn shield a living tool ([d81e6fa](https://github.com/Elenterius/Biomancy/commit/d81e6fa7ea1fe3333b9a96de8dc1cf233b266186))
* move the ravenous claws hud element below the crosshair ([0dc1726](https://github.com/Elenterius/Biomancy/commit/0dc1726c95872aa4af84ba27d839ac86ba12f40d))
* **nerf:** consume extra nutrients/charge when triggering abilities of the Ravenous Claws ([547263e](https://github.com/Elenterius/Biomancy/commit/547263e444069aea1cf80098090f7a1cacb4a284))
* **nerf:** decrease value of Suspicious Stew for cradle sacrifices ([c58a791](https://github.com/Elenterius/Biomancy/commit/c58a791e55de88f9c45d9a5a45f18d3582ced1f6))
* **nerf:** drastically reduce nutrients fuel value of everything ([622dd0c](https://github.com/Elenterius/Biomancy/commit/622dd0c937b64b1cab2d6d1edcb668b99c9042da))
* **nerf:** prevent insertion of Bundles into storage sacs ([ece087b](https://github.com/Elenterius/Biomancy/commit/ece087b93fd843a0390669e3d9438741521f043a)), closes [#121](https://github.com/Elenterius/Biomancy/issues/121)
* **nerf:** reduce digester recipe output for nether wart blocks ([86ca22b](https://github.com/Elenterius/Biomancy/commit/86ca22be072e022af0ba6fcccc9fe690b56102f5))
* **nerf:** reduce max stack size of Storage Sac to 1 ([d9aa2ce](https://github.com/Elenterius/Biomancy/commit/d9aa2ce7ba722613b5ee5bc6e07c77d7f135a6a2))
* **nerf:** replace Nutrients with Nutrient Paste in crafting recipes ([cdf1396](https://github.com/Elenterius/Biomancy/commit/cdf139653499557ceb4a44b80f248210671e8f1b))
* only deal acid melee damage when Caustic Gunblade is coated in acid ([4195b1c](https://github.com/Elenterius/Biomancy/commit/4195b1ca66b8d58087347ba86548c074acc0c09c))
* re-balance digesting recipes (nerf cocoa beans, bamboo, melon, pumpkin, cake, etc.) ([fa0c900](https://github.com/Elenterius/Biomancy/commit/fa0c900b8b5589b14ed9cc69cd9764ceb9eac011))
* reduce hostile spawn chance of Cradle by 15% ([603e387](https://github.com/Elenterius/Biomancy/commit/603e38794b3079892d6ff5ac64ee797fdcf9ca7b))
* refactor recipe handling and replace all static food digesting recipes with one dynamic recipe ([13751f6](https://github.com/Elenterius/Biomancy/commit/13751f6757080303f3d8018df9eccad0d7cba1a3))
* refactor tributes ([e3cb685](https://github.com/Elenterius/Biomancy/commit/e3cb6855ab3e5db07b5f3a18df83a14e803da5e9))
* remove ability to place Acid Fluid with the Caustic Gunblade ([bfd4c4f](https://github.com/Elenterius/Biomancy/commit/bfd4c4f9e64f09628c0e03a5b67b8bbc99570962))
* remove entity tags for despoil loot ([a15aa1f](https://github.com/Elenterius/Biomancy/commit/a15aa1ff413d7565bf6214aae40b913fc47c0714))
* remove experimental Toxicus & Bile-Spitter items ([8d4817e](https://github.com/Elenterius/Biomancy/commit/8d4817eee4f22a3fb6c617cdb7c4e7cba003e66c))
* remove Nutrients to Nutrient Paste recipe ([432dace](https://github.com/Elenterius/Biomancy/commit/432dace80648e6677e320cafe715f21a1b564b0b))
* rename Acid fluid to Gastric Acid ([8943ad0](https://github.com/Elenterius/Biomancy/commit/8943ad09baf6eb1c6db17fdeee07fe95cd8e6970))
* rework starting advancements ([f651c8f](https://github.com/Elenterius/Biomancy/commit/f651c8f8224cc736ffa99e98dcb188852ed17598))
* rework symbiontic mending enchantment into a more beneficial but cursed enchantment called Parasitic Metabolism ([a04e17a](https://github.com/Elenterius/Biomancy/commit/a04e17a0e068597ca6250899f2a123975fac7236))
* show ammo amount status on Caustic Gunblade model ([8ef9d49](https://github.com/Elenterius/Biomancy/commit/8ef9d4901db4878fa0c889509dd0f9ab268597db))
* show nutrient fuel value of items inside bio-machine GUIs ([1437cd6](https://github.com/Elenterius/Biomancy/commit/1437cd632d05fca7351ba0974245a771024e3722))
* slightly tweak cradle behavior and limit Cradle attack on failure to sacrifices with high hostility ([0db6ece](https://github.com/Elenterius/Biomancy/commit/0db6ece162b9b421e9a45c1549cad3c67b1ac652))
* tweak advancement ([1d23c35](https://github.com/Elenterius/Biomancy/commit/1d23c351bee7cbb678fc7c0aa98f8033095648fe))
* tweak Caustic Gunblade attack speed ([0f31253](https://github.com/Elenterius/Biomancy/commit/0f31253f83458d657aa5d71aff9a4b6536f5b62d))
* tweak decomposing recipes (nerf golden carrot & golden apple; buff magma cream) ([b24c330](https://github.com/Elenterius/Biomancy/commit/b24c330ad2f2a745aede19484ba3f97862b71cf8))
* tweak Essence Anemia effect color ([8093a81](https://github.com/Elenterius/Biomancy/commit/8093a81193e3bb1b7f9edeb5b9fff029f874a085))
* tweak Primordial Core recipe ([7a57ec6](https://github.com/Elenterius/Biomancy/commit/7a57ec67de686ad9f631eea5d909d3a0cb63c08d))
* tweak reload behavior of Caustic Gunblade ([3434b24](https://github.com/Elenterius/Biomancy/commit/3434b247c741fc80090f8180758d7fc8d12111a2))
* tweak which enchantments can be applied to living tools ([ba83959](https://github.com/Elenterius/Biomancy/commit/ba83959b06cf1a6fbbfc6769dd837b8a17fa3356))
* update Bloomberry texture ([43b3e93](https://github.com/Elenterius/Biomancy/commit/43b3e9305e6deaf98f3e29c2457efa69b89da515))
* update chrysalis textures ([2d83670](https://github.com/Elenterius/Biomancy/commit/2d83670b601e50442b23df0beddccea8079bc7c1))
* update Extractor texture ([e6d8497](https://github.com/Elenterius/Biomancy/commit/e6d8497ddaeaa4285b914dad13067c52e94ea1b4))
* update Flesh Door textures ([6a029f5](https://github.com/Elenterius/Biomancy/commit/6a029f5eb1ac39c51fed2ca0682a8a7eb9a7b5c3))
* update Modular Larynx model ([ca6bafb](https://github.com/Elenterius/Biomancy/commit/ca6bafb23ba404d1940d2b85d2bd185e1564549e))
* update nutrient paste & bar textures ([cf3ffa4](https://github.com/Elenterius/Biomancy/commit/cf3ffa494fe21f21a71b99df71206c33b98edb10))
* update Primordial Flesh Blob textures ([b5a8283](https://github.com/Elenterius/Biomancy/commit/b5a8283fe08d5931c85c7f2aa9165e0248cbaa05))
* use nutrients for Caustic Gunblade durability ([3125e91](https://github.com/Elenterius/Biomancy/commit/3125e917ae35f15d1995a9f31ad135eb0e2d2e1d))


### Bug Fixes

* add missing newline before enchantment tooltips ([7c695ed](https://github.com/Elenterius/Biomancy/commit/7c695edb6b1551188424696ba8528d85e2b0cb1a))
* add missing translation strings for death messages ([7c1614f](https://github.com/Elenterius/Biomancy/commit/7c1614fea2b0b993cb0544d6065c2d5b83d084ed)), closes [#123](https://github.com/Elenterius/Biomancy/issues/123)
* fix acid fluid applying potion effects to spectators ([12db02a](https://github.com/Elenterius/Biomancy/commit/12db02aabfe92df4143d8be9789a566bfa264457))
* fix block placement preview render not working for primal & malignant slabs ([c156829](https://github.com/Elenterius/Biomancy/commit/c15682943223d7f12d3feb54eda9a320ee15c39d))
* fix Caustic Gunblade animation not showing the acid coat ([1a49096](https://github.com/Elenterius/Biomancy/commit/1a49096c7317a6a11100e77cc007f02be304d34e))
* fix crash when placing down a Primordial Cradle ([0ae4d37](https://github.com/Elenterius/Biomancy/commit/0ae4d37acbcb4fbf41971eec7f17f8e41634ef03))
* fix custom shader being applied incorrectly ([e3cd2a2](https://github.com/Elenterius/Biomancy/commit/e3cd2a25e900e6d20262bae7ef7623e8c46bf9a9))
* fix extractor item animation playing on all other extractor items when looking at a mob ([9887b8e](https://github.com/Elenterius/Biomancy/commit/9887b8e4ddfa77b03fdd1f8d00a02d5dfa5c5862))
* fix Fleshkin Chests being breakable by block breaker machines (e.g. Create Drill) ([54616d4](https://github.com/Elenterius/Biomancy/commit/54616d41e0d0899001f8a896235280dfbe4f760c)), closes [#109](https://github.com/Elenterius/Biomancy/issues/109)
* fix maw hopper stealing items from villagers & witches ([db7621b](https://github.com/Elenterius/Biomancy/commit/db7621bcf287f26ebbd7cda5e27d8b3dccf9b9c4)), closes [#122](https://github.com/Elenterius/Biomancy/issues/122)
* fix missing acid bucket pickup sounds ([ff80b23](https://github.com/Elenterius/Biomancy/commit/ff80b23603db754758c2775abc2dfb05b79964d8))
* fix missing translation for fleshkin eat sounds ([b7b37a4](https://github.com/Elenterius/Biomancy/commit/b7b37a410ff7c9b310893b5a13049f280978fac8))
* fix mixin incompatibility with Apothic-Attributes mod ([6a2aeb3](https://github.com/Elenterius/Biomancy/commit/6a2aeb39bbdd3c8929a4f321191860e0ed370328))
* fix the glowing eye of the Primordial Cradle not displaying the amount life energy correctly ([a215ec9](https://github.com/Elenterius/Biomancy/commit/a215ec9ff4d25957e9bf5d1733dd529a20c91fd6))
* properly fix block tinting issues ([35842f6](https://github.com/Elenterius/Biomancy/commit/35842f642937cbc31b23fb841214106e26cd89b5))
* remove ability to enchant flesh armor with unbreaking ([628f0db](https://github.com/Elenterius/Biomancy/commit/628f0db8cd990d43eccf34908d15b8884d88d8c7))
* remove ability to enchant living tools and armor with unbreaking ([59aba3f](https://github.com/Elenterius/Biomancy/commit/59aba3f9c052154746f43382c2dc1943c2eb4740))
* remove biomancy's stonecutting recipes from the minecraft namespace ([7a332fb](https://github.com/Elenterius/Biomancy/commit/7a332fb19b83d9ff77acaaf3048b02d2f466a3e4))
* try to fix the rare issue where the Cradle Chamber is unable to form a floor by lowering Acid Chambers by 0.25 units ([4864f6d](https://github.com/Elenterius/Biomancy/commit/4864f6da898b49f7605ba78346539d549294bebd))


### Miscellaneous Chores

* refactor living tools ([cba0018](https://github.com/Elenterius/Biomancy/commit/cba00188b968340def31fb47650a123a04a75c8c))
* refactor nutrients api ([ca0e39f](https://github.com/Elenterius/Biomancy/commit/ca0e39f8b1dc3835807387e96b473b5fc805cfa2))
* rework Nutrients handling and add fluid fuel support ([e73eef4](https://github.com/Elenterius/Biomancy/commit/e73eef42487d8ef2ca598d69abd9be9332f924a0))

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

