## [3.2.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.3.1.1...1.19.2-v2.3.2.0) (2024-02-11)


### Features

* disable bio-forge recipe progression in the configs by default (temporary measure until progression rework happens) ([48063c0](https://github.com/Elenterius/Biomancy/commit/48063c062331177c4992ed32eb20b499c75de1c5))
* **injector:** change cancel and clear icons in the wheel menu ([72844fb](https://github.com/Elenterius/Biomancy/commit/72844fba90e3b51ce022d380f09a687e2ca1141c))
* **injector:** change item label color to white in the wheel menu ([557da30](https://github.com/Elenterius/Biomancy/commit/557da30f267c3a30ca30154467eec28b85709e46))
* **injector:** tweak serum colors ([abc0a6d](https://github.com/Elenterius/Biomancy/commit/abc0a6d3bb83e06bf003cfcddf485688d54e1ad2))
* make Membranes valid windmill sails for Create ([d74859b](https://github.com/Elenterius/Biomancy/commit/d74859bd6c802aa96a37ec400817eb75a81c8bb0))


### Bug Fixes

* fix coloring of vials in the vial holder not always updating and defaulting to white ([8cb5caa](https://github.com/Elenterius/Biomancy/commit/8cb5caa3608b51cfa0fe9ad5a9621677cd2f3f23))
* fix fertilizer not being consumed and converting dirt into the wrong grass block ([9fc9a19](https://github.com/Elenterius/Biomancy/commit/9fc9a195a164e45cffb3b94138e525d7ddc0e7ac))
* **injector:** fix mismatching serum colors between item and injector model ([89ed8e9](https://github.com/Elenterius/Biomancy/commit/89ed8e93a03467e3638ff7281031779096ba68d8))

### [3.1.1](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.3.1.0...1.19.2-v2.3.1.1) (2024-02-08)


### Bug Fixes

* add missing sound translations ([7cb51a6](https://github.com/Elenterius/Biomancy/commit/7cb51a6c3f2d1a4e77c069447ceb07bbfa5aede4))
* **data-gen:** fix advancement parsing errors spamming the logs ([96ef8f3](https://github.com/Elenterius/Biomancy/commit/96ef8f3dc6164fad1a88319657b5eff6bd9c159d)), closes [#115](https://github.com/Elenterius/Biomancy/issues/115)

## [3.1.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.3.0.1...1.19.2-v2.3.1.0) (2024-01-15)


### Features

* **flesh-mound:** increase the minimum cradle room size and make it more likely that it generates with a solid floor ([c12f842](https://github.com/Elenterius/Biomancy/commit/c12f84238d8ffeb80c45b337f2df12ad171a1ed4))

### [3.0.1](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.3.0.0...1.19.2-v2.3.0.1) (2024-01-10)


### Bug Fixes

* fix incomplete credits ([8b79987](https://github.com/Elenterius/Biomancy/commit/8b799871cc50a31d1a4b8db38f27727529d39c5a))

## [3.0.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.0.0...1.19.2-v2.3.0.0) (2024-01-10)


### ⚠ BREAKING CHANGES

* remove neural interceptor block
* **server-config:** remove doUnlimitedGrowth settings
* **tags:** rename primordial_ecosystem_replaceable tag to flesh_replaceable

### Features

* **acid-effect:** increase acid effect damage for wet entities ([a4ee4e9](https://github.com/Elenterius/Biomancy/commit/a4ee4e98ac2f92f2156457138a6497a667a742e8))
* **acid-effect:** make entities sensitive to water (water splash potion) ([71e34ba](https://github.com/Elenterius/Biomancy/commit/71e34bac26f9592b96dc196ddcd5979b50a22f81))
* **acid-fluid:** make acid fluid able to slowly destroy blocks with the acid_destructible tag ([110b6a1](https://github.com/Elenterius/Biomancy/commit/110b6a1a38ef4922a4fedd698f39e4ba35f8cc96))
* **acid-fluid:** make acid fluid able to sweep away flesh veins ([6ec9050](https://github.com/Elenterius/Biomancy/commit/6ec905095d4270c15c07bcccb918b136b44cff7e))
* **acid-fluid:** make acid randomly destroy flesh veins or crack stone variants ([b32e5f4](https://github.com/Elenterius/Biomancy/commit/b32e5f404e356b15c2f5156b2b4924e538b4ad0b))
* **acid:** add acid fluid ([f042133](https://github.com/Elenterius/Biomancy/commit/f0421336760975afb5dec30baf7c71d633ba39cf))
* **acid:** add dripping acid particle ([bf85b94](https://github.com/Elenterius/Biomancy/commit/bf85b940533c50ecd90553e3188ccc45acdc87fc))
* **acid:** add fluid compat for open pipes from Create ([c6a1d80](https://github.com/Elenterius/Biomancy/commit/c6a1d8030fd6eea492ae9166423c568da9af6200))
* **acid:** add fluid interaction with lava & water ([6c3aba9](https://github.com/Elenterius/Biomancy/commit/6c3aba9a0e1d2485759a111da3fd72928970a974))
* **acid:** reduce probability for acid particles to produce fizzling smoke when landing on ground ([8bf4a6b](https://github.com/Elenterius/Biomancy/commit/8bf4a6b1c79287bdabe9c468225c3aabc7e501be))
* add acid blob projectile and spawn it from primal orifices ([f0298e1](https://github.com/Elenterius/Biomancy/commit/f0298e15daf9b96200e242505aba641da4a49069))
* add block tags to explicitly allow/disallow attachment of flesh veins to specific blocks ([4712992](https://github.com/Elenterius/Biomancy/commit/47129926506536d95f2a88bfd92084cf56c0839b))
* add bloomlight recipe ([49adad4](https://github.com/Elenterius/Biomancy/commit/49adad4929a1f6a7f5cd1d2cb227bb2888c35b9d))
* add config for enabling unlimited flesh growth globally ([3946b8f](https://github.com/Elenterius/Biomancy/commit/3946b8f96b90608207dbc68cb6e6043e87ae0123))
* add config for enabling unlimited flesh growth near cradles ([f0203d4](https://github.com/Elenterius/Biomancy/commit/f0203d434127caefc362211c12260d676febe34c))
* add Malignant Bloom and Sapberry ([316cca0](https://github.com/Elenterius/Biomancy/commit/316cca042f8ef45a779e5b144cf4b44142414c1a))
* add neural interceptor block ([2b91065](https://github.com/Elenterius/Biomancy/commit/2b9106557fad9ff4375dc95dc73dcb5683419de2))
* add primal membrane block for living mobs ([6008f15](https://github.com/Elenterius/Biomancy/commit/6008f155682b03015302f7f0be4716ed07b8f10e))
* add primal orifice block ([e0507a2](https://github.com/Elenterius/Biomancy/commit/e0507a2f35f4fedcae6f4d47b8a558dae5ed34e8))
* add server config for BioForge recipe unlocking and villager trades ([4535338](https://github.com/Elenterius/Biomancy/commit/45353380c05622126038fcf27cbc7ec3712ef801))
* add undead-permeable membrane ([849f89f](https://github.com/Elenterius/Biomancy/commit/849f89f446d05d1adcd065eeed38140736679bd5))
* buff strength of `FleshyBone` material (increase destroy time from 3 to 4 and explosion resistance from 3 to 6) ([f4b0580](https://github.com/Elenterius/Biomancy/commit/f4b058015905746ebaba36bd1a2d6a49b2545476))
* **buff:** increase primal energy conversion of sacrifices in the Primordial Cradle ([743fef2](https://github.com/Elenterius/Biomancy/commit/743fef2bb98109075738cd85a018f9eea0c3292c))
* change chiseled flesh block/texture ([7d7812e](https://github.com/Elenterius/Biomancy/commit/7d7812ef3fa22b0b2b01becc023fe740998d89d9))
* change Primordial Lantern recipe to require a Sapberry ([28ef8e4](https://github.com/Elenterius/Biomancy/commit/28ef8e4b98030be96b515a8f368a6e03ac4da7a8))
* change the block material of Flesh Pillar, Chiseled Flesh and Ornate Flesh to `FleshyBone` material ([cac88ef](https://github.com/Elenterius/Biomancy/commit/cac88efc6970fb901b62532cf8c81185565b8215))
* **flesh-mound:** add bloomlight block as flesh type replacement of shroomlight in mounds ([93d388c](https://github.com/Elenterius/Biomancy/commit/93d388cb71363e29bc66f536c848edab768158e2))
* **flesh-mound:** add bone pillar generation ([34a8000](https://github.com/Elenterius/Biomancy/commit/34a8000e7aa38f45e8f278cbc504243345cd6a43))
* **flesh-mound:** add chamber decorations such as pillars, orifices, etc. ([5edf68d](https://github.com/Elenterius/Biomancy/commit/5edf68d89185c6fd9da6a6851f109c424a5996d7))
* **flesh-mound:** add first iteration of the flesh mound growth to the primordial cradle ([89671ee](https://github.com/Elenterius/Biomancy/commit/89671ee7435549f3850b39d5a03d0e57954a7de5))
* **flesh-mound:** allow destruction of melons, pumpkins and moss ([9470aa3](https://github.com/Elenterius/Biomancy/commit/9470aa372453af1a551b42f5f72d3443da7c6cbc))
* **flesh-mound:** allow flesh mounds to excavate chambers and convert the destroyed blocks into primal energy ([cba5f67](https://github.com/Elenterius/Biomancy/commit/cba5f67b938b0ea0aa820238c267b5fcab7b1d44))
* **flesh-mound:** create "Doors" between two adjacent chambers with Primal Membranes ([bef79c0](https://github.com/Elenterius/Biomancy/commit/bef79c0d55a4dcd3dcd60917589ea37d8fba765c))
* **flesh-mound:** decrease volume of growth ([d5a746a](https://github.com/Elenterius/Biomancy/commit/d5a746a6f7b62dbde703a55e01989ab89cea6b73))
* **flesh-mound:** improve storage and lookup of mound shapes ([25a4c20](https://github.com/Elenterius/Biomancy/commit/25a4c208ad343283606ec0ac160bd937c1c77f3a))
* **flesh-mound:** make flowers replaceable ([a0ab90c](https://github.com/Elenterius/Biomancy/commit/a0ab90c8febda655c189237340748b0400cf5e6c))
* **flesh-mound:** make Malignant Bloom spawn less likely inside the mound and more likely at the edges or outside ([c356855](https://github.com/Elenterius/Biomancy/commit/c3568555c41aa15c84f2333155237dec03b65f19))
* **flesh-mound:** make mound shapes persist across unloaded chunks and store mound shape seed in primordial cradle block/item ([e9fa826](https://github.com/Elenterius/Biomancy/commit/e9fa826ba94715f5655bfffefdf95551ff8d1cc8))
* **flesh-mound:** make mounds slightly smaller ([9b12b53](https://github.com/Elenterius/Biomancy/commit/9b12b539599f914507d9d1843a725c64f6e06788))
* **flesh-mound:** make Primal Membrane self-spreading inside flesh mounds ([c83b110](https://github.com/Elenterius/Biomancy/commit/c83b110ed18e9504edef6610de1c57e93fb3d95b))
* **flesh-mound:** prevent flesh veins from eating living flesh ([2f59457](https://github.com/Elenterius/Biomancy/commit/2f594575e23bdfef8d4454396fa770a4b07c86aa))
* **flesh-mound:** prevent natural spawning of mobs inside flesh mounds ([fad997c](https://github.com/Elenterius/Biomancy/commit/fad997cf717a4bba3064c00cb1905a9df13289a9))
* **flesh-mound:** tweak default mound gen settings ([86cca32](https://github.com/Elenterius/Biomancy/commit/86cca32756ccdde568297bf5f13a53d37cb84693))
* **flesh-mound:** tweak flesh mound size ([9872938](https://github.com/Elenterius/Biomancy/commit/987293885fbe0877c605d334b3681967eddeb28a))
* improve make malignant bloom shooting and check for blocks obstructing the aim ([0b05571](https://github.com/Elenterius/Biomancy/commit/0b0557126c273d24d38b9f3dae69d1177706733d))
* increase explosion resistance of Packed Flesh from 6 to 12 ([3c633aa](https://github.com/Elenterius/Biomancy/commit/3c633aa3c5df548441b0b0765b91329ac2882f09))
* increase life/primal energy value of sacrificed Nether Stars tenfold ([a1a578b](https://github.com/Elenterius/Biomancy/commit/a1a578b791e1217cf50daf51965a22997d919656))
* **item-tag:** add `cannot_be_eaten_by_cradle` tag for items that should not be eaten by the Cradle ([d10be91](https://github.com/Elenterius/Biomancy/commit/d10be91ba7923b88aad00ab9b748024d29de8c66))
* **item-tag:** add items to forge tags for doors, trapdoors and chests ([936074a](https://github.com/Elenterius/Biomancy/commit/936074a5cdb99dcb2881e64689514746f372490b))
* make acid fluid erode dirt ([3df7de0](https://github.com/Elenterius/Biomancy/commit/3df7de06bf8113112ebc2864787f7c2b8b4dd7b6))
* make Bio-Forge reduce fall damage ([9c5e370](https://github.com/Elenterius/Biomancy/commit/9c5e37016bf2eaf160e7e63c1b8bf58415ed60ac))
* make bony flesh blocks play flesh or bone sounds (with equal probability) ([7cb6065](https://github.com/Elenterius/Biomancy/commit/7cb606507c82b24c740a804b9659ff44878ef5c4))
* make it possible to add more cradle tributes via code ([0156ea3](https://github.com/Elenterius/Biomancy/commit/0156ea33cee8f9246183958fe3e3cc6f17edb8cb))
* make life energy affect the energy charge of the Primordial Cradle and allow life energy of sacrifices to exceed 100 ([d96db05](https://github.com/Elenterius/Biomancy/commit/d96db05a4a3600204ea64a38160ad2b4b38125a9))
* make malignant flesh veins replaceable ([02739d2](https://github.com/Elenterius/Biomancy/commit/02739d243f7ddd1073538ddd81f858de5785ae8a))
* make nether stars a valid sacrifice source ([b747814](https://github.com/Elenterius/Biomancy/commit/b7478145a4008deb17f9f737fec0e5cfbc430e66))
* make overworld logs replaceable by malignant veins ([c39d00e](https://github.com/Elenterius/Biomancy/commit/c39d00ee60eff5325954ad87e3dc7681dc14ac4e))
* make primal membrane consider golems as not alive ([98d17e1](https://github.com/Elenterius/Biomancy/commit/98d17e1647097862a9b7aa943b93e078c6a627f6))
* make primal orifices milk-able with empty buckets ([0c0a3c4](https://github.com/Elenterius/Biomancy/commit/0c0a3c4662b7e1ae952bc0b5b0a70ab236add5a9))
* make Primordial Cradle block items display their internal sacrifice information in the tooltip ([4229ed0](https://github.com/Elenterius/Biomancy/commit/4229ed03cc9562677358febe59c4cfc6d1506685))
* make wandering trader trades less expensive ([8c41c25](https://github.com/Elenterius/Biomancy/commit/8c41c25520853e165e364e4ec157c7753af52e5b))
* **nerf:** decrease Malignant Bloom spawn rate ([0f18e50](https://github.com/Elenterius/Biomancy/commit/0f18e50b03f7dedc1085cb36a41b423d166cfcd4))
* prevent low acid fluid levels from destroying flesh veins ([d88d4a4](https://github.com/Elenterius/Biomancy/commit/d88d4a4ac40af515934644ea6ea3d14d1d48bc7b))
* remove infinite flesh growth near cradles (distance < 8) ([1a432e6](https://github.com/Elenterius/Biomancy/commit/1a432e684d43a85ec6afc8c0e1428a351f7654e6))
* remove legacy item tags for biomass ([a1aa872](https://github.com/Elenterius/Biomancy/commit/a1aa872d64b59d8d1883e88e0c1c3e253f1302c7))
* remove neural interceptor block ([59994a2](https://github.com/Elenterius/Biomancy/commit/59994a2e5343e50238e7a2bf9abbac86e6ec154c))
* remove the ability of mob effects to influence the hostility of Flesh Blobs spawned via the cradle ([ca9ae97](https://github.com/Elenterius/Biomancy/commit/ca9ae971291c226f54fb2d99b0cf7ac37d9bde01))
* rename Bloom related stuff ([0dde7ea](https://github.com/Elenterius/Biomancy/commit/0dde7eafdbb741ecd9e618c6da265773dddf945b))
* replace acid fluid erosion of stone into cobblestone with cobblestone into gravel ([9abd080](https://github.com/Elenterius/Biomancy/commit/9abd08026880c78404318782a7f4e6f6f9cfcf72))
* **server-config:** remove doUnlimitedGrowth settings ([15807e2](https://github.com/Elenterius/Biomancy/commit/15807e21faf49f0873aa6804748226d8d658abe2))
* **tags:** rename primordial_ecosystem_replaceable tag to flesh_replaceable ([868c9c6](https://github.com/Elenterius/Biomancy/commit/868c9c6c74d581d9320f13e6a37309e3ed39e922))
* **tetra-compat:** make Dragon Sinew a valid cradle tribute ([497cf42](https://github.com/Elenterius/Biomancy/commit/497cf427b7352418564e4558ea48d4f1d27234c3))
* **tetra-compat:** make Dragon Sinew decomposable ([1e00435](https://github.com/Elenterius/Biomancy/commit/1e0043552d4b7dcbcc6fa14ad177785b996f5408))
* **tributes:** make totem of undying a valid cradle sacrifice and buff a few tributes ([19ebb14](https://github.com/Elenterius/Biomancy/commit/19ebb14af7fe2d18cc34f1a856eaadfbc38635c7))
* tweak decomposing recipe of bloomlight ([b8ac5bd](https://github.com/Elenterius/Biomancy/commit/b8ac5bdd92f5516948d57878e87e28ad62ebe681))
* tweak primal flesh block recipe ([3e70c1b](https://github.com/Elenterius/Biomancy/commit/3e70c1b8b9bdebab24d1bb0154c8b90a8353757b))


### Bug Fixes

* add workaround for NERB incompatibility ("unlocks" all recipes when NERB is detected) ([2b14b80](https://github.com/Elenterius/Biomancy/commit/2b14b802f39d6df49feff3592fbbe6793280258d))
* fix flesh veins inability to spread over dirt paths ([ec42826](https://github.com/Elenterius/Biomancy/commit/ec42826e13b77781f0520416fc8030710c72ae44))
* fix flesh veins not being being mineable with a hoe ([abdc85c](https://github.com/Elenterius/Biomancy/commit/abdc85ca6304e05619bd883f27df42d85f28a71e))
* fix machine menus displaying wrong crafting cost ([e7e2037](https://github.com/Elenterius/Biomancy/commit/e7e203774264402cf515c6bb74b50a4c3ec02e0d))
* fix Primordial Cradle not persisting internal sacrifice values when mined ([e944ef7](https://github.com/Elenterius/Biomancy/commit/e944ef7da9c52ed1dd0c78745998ce2c59ff058d))
* fix serum duplication bug with Injector ([1079c00](https://github.com/Elenterius/Biomancy/commit/1079c00da854f621a170379b3c00cdf00f36e9f1))
* fix Wide Flesh Doors incompatibility with Quark's double door feature ([1df6853](https://github.com/Elenterius/Biomancy/commit/1df6853fa3369e36772ef8a85072f40400f8e7c6)), closes [#108](https://github.com/Elenterius/Biomancy/issues/108)
* fix wrong file name for bloomberry item texture ([f3768a8](https://github.com/Elenterius/Biomancy/commit/f3768a87270ca4ec8b5c1e4f1aeaefae6ea78f4b))
* **flesh-mound:** fix flesh veins being unable to find the flesh mound when other types of shapes exist in the same area ([a97e7e4](https://github.com/Elenterius/Biomancy/commit/a97e7e4966c311ecabbf4dc5a0b4020bf740538a))
* **flesh-mound:** fix unintentional MoundShape removal when the Cradle BlockEntity is unloaded ([3aecd54](https://github.com/Elenterius/Biomancy/commit/3aecd5430253e910238fdb121ddb5eb6a47aac58))
* **flesh-mound:** prevent chamber intersections with the main cradle chamber ([ffbb53c](https://github.com/Elenterius/Biomancy/commit/ffbb53c141413ae945694a69e7a37e802c2f1aa3))
* **gradle:** fix build configuration not including the api sourceset in the primary build artifact ([d746574](https://github.com/Elenterius/Biomancy/commit/d746574ec45207374b5348fd8c2d23dca96f9d51))
* prevent storage sac from being eaten by the Cradle ([d5ca790](https://github.com/Elenterius/Biomancy/commit/d5ca7907118a3cd2381f4de9538283baed953b54))

