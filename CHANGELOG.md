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

## [2.0.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.13.0...1.19.2-v2.2.0.0) (2023-09-09)


### ⚠ BREAKING CHANGES

* move menu package from inventory package into main package
* rename item interfaces
* **recipes:** change json structure and key names of recipes

### Features

* add ability to "pet" Flesh Blobs ([377bff8](https://github.com/Elenterius/Biomancy/commit/377bff842406bc25c378ac202d82eb15e7050719))
* add Caro Invitica font by Tyfin ([ccd2845](https://github.com/Elenterius/Biomancy/commit/ccd2845f53b65151e0ec17911013afce7568aef0))
* add Chiseled Flesh block ([6597d23](https://github.com/Elenterius/Biomancy/commit/6597d234f1049dbb4f5940aa870fe02c61bc6f58))
* add claws & fangs item tags for crafting ([3aa4eac](https://github.com/Elenterius/Biomancy/commit/3aa4eac074c19fb2482f9192be11f667f177de73))
* add Corrosive Swipe Attack VFX to the Toxicus ([048c428](https://github.com/Elenterius/Biomancy/commit/048c4289ef5e874468598fa0a21a1be06bf1bfbe))
* add Drowsy Status Effect which prevents Phantoms from attacking ([2a2449d](https://github.com/Elenterius/Biomancy/commit/2a2449d4c4507bd1bf232be5b2ab5064fc3091ea))
* add Fibrous Flesh block ([fc0085e](https://github.com/Elenterius/Biomancy/commit/fc0085ea27157f6bdc6bdf2df535fddeb3815732))
* add Fleshy Membrane block ([f9bf0ef](https://github.com/Elenterius/Biomancy/commit/f9bf0ef413ee86707eaf7c0ae12494dca53478c6))
* add HUD for the serum amount inside an Injector ([62e17a2](https://github.com/Elenterius/Biomancy/commit/62e17a264ecc57e3ed12132678e39e15816784ca))
* add item gui icon for the Ravenous Claws ([c0ca2b9](https://github.com/Elenterius/Biomancy/commit/c0ca2b9342d0d3e9817401fe4088d0cd52a0f7ad))
* add item texture for the Flesh Spike ([666a5df](https://github.com/Elenterius/Biomancy/commit/666a5df3a6e5e46295eeecf093b024847cde23cb))
* add item texture for the Tendon Chain ([d0c1110](https://github.com/Elenterius/Biomancy/commit/d0c1110285dcf0e403598f5d2294a039a706ff5b))
* add malignant and primal flesh walls ([3c44be5](https://github.com/Elenterius/Biomancy/commit/3c44be5384846e25646ad173533c22941927557d))
* add Malignant Flesh Block recipes ([f088d72](https://github.com/Elenterius/Biomancy/commit/f088d72928d84529aecee9ded5f80424950ee5c7))
* add Ornate Flesh block ([82a222c](https://github.com/Elenterius/Biomancy/commit/82a222c43c6fdadfe7215796bbb40d63494543f7))
* add Primal Flesh Block recipes ([bd4c5ff](https://github.com/Elenterius/Biomancy/commit/bd4c5ff44d4f6f45cbfc3227a5e37c9ae4e0ec86))
* add primordial bio lantern ([c5ca95a](https://github.com/Elenterius/Biomancy/commit/c5ca95a5b3c5792eb6285a713332e74f28bcec48))
* add specialised membrane variants for items, babies and adult mobs ([c472856](https://github.com/Elenterius/Biomancy/commit/c472856fb97a4f791ace1a6097c063141d3b8fbd))
* add Toxicus ([ed0a733](https://github.com/Elenterius/Biomancy/commit/ed0a7333607301a48ec4e92d447c39c3ce93767b))
* add Tubular Flesh block ([26216e3](https://github.com/Elenterius/Biomancy/commit/26216e3bb7e5190caca141eb831e6a7223f8212d))
* buff Flesh Plunderer damage from 10 to 12 and durability from 60 to 250 ([a8b7219](https://github.com/Elenterius/Biomancy/commit/a8b721969427f17da3422fc75725aff8b1c47142))
* change injector item icon texture ([cddccb0](https://github.com/Elenterius/Biomancy/commit/cddccb0c5a4a9b2cc6b92c6580cb522d41665bc4))
* enable shading for tendon chain & hanging bio lantern model ([d68742e](https://github.com/Elenterius/Biomancy/commit/d68742eaef58710ddd00a5fe191a88e5e9c2f360))
* fix and improve malignant flesh spreading mechanics ([ba6d415](https://github.com/Elenterius/Biomancy/commit/ba6d41594ce6af3b25379d70a0df5256d34a1a53))
* fix Injector not reloading with max serum amount ([bf641d9](https://github.com/Elenterius/Biomancy/commit/bf641d9089d83094ab1c7ba3b46e92095f9c2396))
* fix primordial flesh blobs spawning with tumors when summoned via spawn eggs ([6ae6103](https://github.com/Elenterius/Biomancy/commit/6ae6103bfe6ee50c533618d990fe673a45a49909))
* give creative players the ability to see all Bio-Forge recipe regardless of if they have unlocked them ([1e3cfd0](https://github.com/Elenterius/Biomancy/commit/1e3cfd09baf076569569b961a1874bdd205d5976))
* increase Breeding Stimulant duration from 12 to 14 seconds ([1e5c5f0](https://github.com/Elenterius/Biomancy/commit/1e5c5f0dea6c43c2acb3c25c74907e01a1da5a5a))
* make all Flesh Blob sounds resource pack driven ([7d896ff](https://github.com/Elenterius/Biomancy/commit/7d896ffa9c51fc2ca52dd2d59cccdb38f4ec6710))
* make Cleansing Serum consume saturation ([2ec8754](https://github.com/Elenterius/Biomancy/commit/2ec8754b666439824757cb47b32f863fb215b1ae))
* make crafting cost (nutrients) data driven by including them in recipes ([6b38c12](https://github.com/Elenterius/Biomancy/commit/6b38c1299cb7235ab51b80fa63155167997122db))
* make flesh plunderer magic outline emissive ([a75f2b9](https://github.com/Elenterius/Biomancy/commit/a75f2b911b1e142d0c41f77590834222075c6bfa))
* make Flesh Spikes fall-on damage scale with the spike amount ([845e38e](https://github.com/Elenterius/Biomancy/commit/845e38eefcac1f4adfff61da01a11c3f159ad74a))
* make Insomnia Cure serum apply the Drowsy status effect ([9e894cd](https://github.com/Elenterius/Biomancy/commit/9e894cdd1f815c9535b33f76fe70082538dc8ceb))
* make Malignant Veins harvestable with silk touch tools (additionally to shears) ([2a0d2fc](https://github.com/Elenterius/Biomancy/commit/2a0d2fcc7c69c2922fa5267f7940642e82cf5a98))
* make membrane block deal reduced fall damage ([d595417](https://github.com/Elenterius/Biomancy/commit/d595417fbd678c726a9d5d662926fdb7c2c0bd72))
* make part of the Primordial Core item emissive ([2717963](https://github.com/Elenterius/Biomancy/commit/271796386426284c082c214e8e513c64fb621c3b))
* mirco-buff Ravenous Claws ([ae231b2](https://github.com/Elenterius/Biomancy/commit/ae231b2f3699041b099c1c7ec8834f9a13780542))
* **mod-compat:** add Create compacting recipes for flesh blocks ([47c2148](https://github.com/Elenterius/Biomancy/commit/47c21482dfce02d4d95c63d401d822b8e3494555))
* obfuscate primal and malignant flesh block tooltips ([7ccefc8](https://github.com/Elenterius/Biomancy/commit/7ccefc8f79c31710097fd4535298cb22a6a8e1ca))
* **recipes:** add Claw recipe to Bio-Forge ([c9912b6](https://github.com/Elenterius/Biomancy/commit/c9912b689f37c408e8705e677df011017c00332b))
* **recipes:** add Fang recipe to Bio-Forge ([0cb2708](https://github.com/Elenterius/Biomancy/commit/0cb2708397b889cf99fe4c89058bd3339af06d14))
* **recipes:** buff decomposing recipe of Golden Apples ([146a7ae](https://github.com/Elenterius/Biomancy/commit/146a7ae0af4481b669515cb639de1197072aba01))
* **recipes:** change json structure and key names of recipes ([f488a0f](https://github.com/Elenterius/Biomancy/commit/f488a0f4ba48413e0144922af70df01285d7cb96))
* **recipes:** nerf Flesh Spike recipe ([85489f9](https://github.com/Elenterius/Biomancy/commit/85489f9a5815942a28dae3f24bc91b1e870f49e8))
* **recipes:** nerf kelp decomposing recipe ([a773441](https://github.com/Elenterius/Biomancy/commit/a773441f543baf86bee03986f277a14aa5c53b43))
* **recipes:** nerf kelp digesting recipe ([85e843c](https://github.com/Elenterius/Biomancy/commit/85e843c9e1426390fbc5e3472f15e4c7e4ec9d80))
* **recipes:** nerf Ravenous Claws recipe ([6205b23](https://github.com/Elenterius/Biomancy/commit/6205b2398914f55744125034ebff47f3561978b2))
* **recipes:** remove Primordial Cradle recipe from Bio-Forge ([26ac3eb](https://github.com/Elenterius/Biomancy/commit/26ac3eb47f0d0773f03fbcc7de050ebb861e42af))
* remove experimental Item-Membrane block ([9d2e108](https://github.com/Elenterius/Biomancy/commit/9d2e10826fffe0f67a4752a12b96fffae7a88921))
* remove name tag for legacy flesh blob texture (legacy flesh blob is now a cradle easter egg) ([0750904](https://github.com/Elenterius/Biomancy/commit/075090440e940f883ccf0db5badf82841c9cae98))
* remove trash slot from Bio-Lab UI ([dd94f80](https://github.com/Elenterius/Biomancy/commit/dd94f80d5f0aec87965e8507e9fcfb042ba4e461))
* remove unused corrupted primal flesh block ([817861b](https://github.com/Elenterius/Biomancy/commit/817861b5f049d00918662015b335b31ab40b0597))
* replace Malignant Flesh Blob with Primordial Flesh Blob variants ([5dc5d3e](https://github.com/Elenterius/Biomancy/commit/5dc5d3e1b49de6ff4f36a8f6a6504700b34955e0))
* rework Cradle sacrifice system to prefer to spawn hostile mobs and occasionally anomalies ([cde0e36](https://github.com/Elenterius/Biomancy/commit/cde0e361a0461da44076488db0c7fcdfc585ef99))
* tweak Adult-Membrane color ([67383e4](https://github.com/Elenterius/Biomancy/commit/67383e4e458672a038bd93c31b1ced09055f0587))
* tweak flesh block recipes ([382948c](https://github.com/Elenterius/Biomancy/commit/382948c8d315eabfe4b2530dad1e402a53dc6acb))
* tweak tooltip colors ([7a47e29](https://github.com/Elenterius/Biomancy/commit/7a47e29537647c76f9c811a6747f1939f1ae1ef9))
* update flesh blob troll texture ([96152b6](https://github.com/Elenterius/Biomancy/commit/96152b64b42fc8891d1dd871d9c874c3506b739c))
* update Flesh Block variant textures ([34557eb](https://github.com/Elenterius/Biomancy/commit/34557eb48dc866c77b24d9ae71b6ec71bfaf481a))


### Bug Fixes

* fix alex's mobs kangaroo and moose ribs decomposer recipes (remove nutrients output) ([77e0baa](https://github.com/Elenterius/Biomancy/commit/77e0baa705baffa1c71807c915a4395a2dc5c736))
* fix buggy Vial Holder block breaking ([c190dc4](https://github.com/Elenterius/Biomancy/commit/c190dc49125193468caaa88b58f30778ab6a0ebc))
* fix critical hits not being detected ([0e89f24](https://github.com/Elenterius/Biomancy/commit/0e89f24f182f0771bae1f25ea65b8d6cf8653687))
* fix Flesh Spike block not dropping the correct amount of spikes ([f91c641](https://github.com/Elenterius/Biomancy/commit/f91c641d346f52da6a49d6738c48468b5970571c))
* fix Flesh Spikes destroying Items ([0a6f1f6](https://github.com/Elenterius/Biomancy/commit/0a6f1f661e7fba3f1ea7c22b62c06df4f8786a5c))
* fix inability to inject tamed mobs with Serums ([310af51](https://github.com/Elenterius/Biomancy/commit/310af512857c87dbd64df3626d37a817d6b715cd))
* fix incompatibility with outdated versions of Create by simply not setting up any compat for it (min. Create version is now 0.5.0) ([0c98e12](https://github.com/Elenterius/Biomancy/commit/0c98e123ad91dfc0dfbe295aa8399ffb4590e3ce))
* fix Insomnia Cure "not sleepy" check not working properly ([7c628e5](https://github.com/Elenterius/Biomancy/commit/7c628e565e611eb945eaff129e99d001d407a5d3))
* fix Primordial Cradle consuming Tetra tools ([9ee49e4](https://github.com/Elenterius/Biomancy/commit/9ee49e4853a6bf052e13c33e69040f3acd3356e9))
* fix Primordial Cradle not being marked as changed when the spread charge is consumed ([faffc4d](https://github.com/Elenterius/Biomancy/commit/faffc4d99c67e716964fbd3ee5f01e401dc6eeb5))
* fix SacrificeHandler incorrectly tracking if it has any modifiers applied ([674e703](https://github.com/Elenterius/Biomancy/commit/674e703a77637a965cc0d6043c13a2241efcfa85))
* fix white line on the bottom of the Flesh Door model ([2a774b2](https://github.com/Elenterius/Biomancy/commit/2a774b27089abf21f58955a1789d3f0a142faacc))


### Performance Improvements

* use cached animations for Flesh Blobs ([002def3](https://github.com/Elenterius/Biomancy/commit/002def3110d3913ba117eab56e56ab185d88b476))


### Miscellaneous Chores

* rename item interfaces ([19ea1db](https://github.com/Elenterius/Biomancy/commit/19ea1db2ba4e314e939f3eb5ef5bf62a72a4e699))


### Code Refactoring

* move menu package from inventory package into main package ([3b4796b](https://github.com/Elenterius/Biomancy/commit/3b4796b8d05d515117ce84f4ffccd476894160da))

## [1.13.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.12.0...1.19.2-v2.1.13.0) (2023-07-29)


### Features

* enhance tooltip clarity and consistency ([bdbbcb9](https://github.com/Elenterius/Biomancy/commit/bdbbcb96bea8c5b7b8bbb1f2c9772be8ab98ab31))

## [1.12.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.1.11.3...1.19.2-v2.1.12.0) (2023-07-18)


### Features

* add attack reach indicator for the Ravenous Claws ([ccc850f](https://github.com/Elenterius/Biomancy/commit/ccc850fce7a7d08aa86a97676eb2068f9167009b))
* add Spanish translation ([4c3c35b](https://github.com/Elenterius/Biomancy/commit/4c3c35ba18f09cf94b8c3a8581927ed809fada13))
* add status effect version of Despoil for use by datapacks etc ([fe2ae46](https://github.com/Elenterius/Biomancy/commit/fe2ae46e524d0b862f5a7ccae769878436589259))
* improve flesh spike tooltip ([4e4c042](https://github.com/Elenterius/Biomancy/commit/4e4c042654d80f0cc11dab20d3f18fbca87b6082))
* make bio-machines retain their fuel when destroyed ([8ec768d](https://github.com/Elenterius/Biomancy/commit/8ec768dcf6a5e92717a9be2ac2d6336b8098bb2c))
* make flesh block texture less repetitive (uses placeholder textures) ([20fb879](https://github.com/Elenterius/Biomancy/commit/20fb87945492eb2cef904b323487dd281bdb7ddc))


### Bug Fixes

* fix full block slabs not dropping two slab blocks ([54bb038](https://github.com/Elenterius/Biomancy/commit/54bb038109267982d1b5594df40cbfe589f3de19))
* fix inability to switch claw modes when holding down ctrl to sprint ([870a033](https://github.com/Elenterius/Biomancy/commit/870a03359169f68ef0d7cd20963b24182918e61f))
* fix misleading fleshkin chest tooltip ([47b2758](https://github.com/Elenterius/Biomancy/commit/47b2758b5eee547d2bbe7d9eec01402f5ce38cf2))

