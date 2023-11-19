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

## [2.3.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.2.0...1.19.2-v2.2.3.0) (2023-11-03)


### Features

* add config for enabling unlimited flesh growth globally ([e28dc68](https://github.com/Elenterius/Biomancy/commit/e28dc68b22fe2d0d0cea75f81659a3b5063a965d))
* add config for enabling unlimited flesh growth near cradles ([826c988](https://github.com/Elenterius/Biomancy/commit/826c9881aeeb9edfd77d0678fca95fa68a448eb7))
* add server config for BioForge recipe unlocking and villager trades ([47a8b12](https://github.com/Elenterius/Biomancy/commit/47a8b120adcf69ed83ebd239cd467d371b35fb6c))
* increase life/primal energy value of sacrificed Nether Stars tenfold ([320cc67](https://github.com/Elenterius/Biomancy/commit/320cc67f0a1a3c399c976e695cd9e01460e4092f))
* make malignant flesh veins replaceable ([c574f3d](https://github.com/Elenterius/Biomancy/commit/c574f3d0fc337b1f87cffe5c603862f15a0bb295))
* make wandering trader trades less expensive ([0eb8f80](https://github.com/Elenterius/Biomancy/commit/0eb8f808b43f4d13cfe6f8ccd755a80808577e2f))
* remove infinite flesh growth near cradles (distance < 8) ([50df048](https://github.com/Elenterius/Biomancy/commit/50df048376bb4cec33912cc760e4ba834c71e3bc))
* remove legacy item tags for biomass ([dd31596](https://github.com/Elenterius/Biomancy/commit/dd31596e1634a9cfa5fff61855c02e9b246933c9))

## [2.2.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.1.0...1.19.2-v2.2.2.0) (2023-10-28)


### Features

* improve make malignant bloom shooting and check for blocks obstructing the aim ([d483871](https://github.com/Elenterius/Biomancy/commit/d483871160088e36c43cd0f0eca2d551e19421ef))
* make it possible to add more cradle tributes via code ([9ab0c36](https://github.com/Elenterius/Biomancy/commit/9ab0c3692757e1261d695774123166bd697f37d0))
* make life energy affect the energy charge of the Primordial Cradle and allow life energy of sacrifices to exceed 100 ([ca75b23](https://github.com/Elenterius/Biomancy/commit/ca75b235e05beaa495a9c14280b671481fba0be9))
* make nether stars a valid sacrifice source ([aa92c3a](https://github.com/Elenterius/Biomancy/commit/aa92c3a3122af34f4c53e35a4bb440935d25004c))
* remove the ability of mob effects to influence the hostility of Flesh Blobs spawned via the cradle ([11fcd4d](https://github.com/Elenterius/Biomancy/commit/11fcd4d33821cd487fcb4c16d760a4c501e8b40e))
* **tetra-compat:** make Dragon Sinew a valid cradle tribute ([0abd696](https://github.com/Elenterius/Biomancy/commit/0abd69612ff29c58b63b843d8698becabcf8a267))
* **tetra-compat:** make Dragon Sinew decomposable ([6f4732b](https://github.com/Elenterius/Biomancy/commit/6f4732b8fe718c0092879047f56f26be95506c66))


### Bug Fixes

* add workaround for NERB incompatibility ("unlocks" all recipes when NERB is detected) ([9dd10ee](https://github.com/Elenterius/Biomancy/commit/9dd10eeeaae04d21804b7d1e325d44836e48df70))
* fix serum duplication bug with Injector ([1137763](https://github.com/Elenterius/Biomancy/commit/1137763a4b051e5d6eb8c18f00d38a127f755e63))
* fix Wide Flesh Doors incompatibility with Quark's double door feature ([e9f4eb5](https://github.com/Elenterius/Biomancy/commit/e9f4eb57041e564c486677462d68c85e68cb5217)), closes [#108](https://github.com/Elenterius/Biomancy/issues/108)

## [2.1.0](https://github.com/Elenterius/Biomancy/compare/1.19.2-v2.2.0.0...1.19.2-v2.2.1.0) (2023-09-21)


### Features

* add Malignant Bloom and Sapberry ([6d575d2](https://github.com/Elenterius/Biomancy/commit/6d575d2200399b14d28af7793892d92fb54d447a))
* tweak primal flesh block recipe ([10e80c7](https://github.com/Elenterius/Biomancy/commit/10e80c7236554a422945815f3b4aba76a931653f))


### Bug Fixes

* fix machine menus displaying wrong crafting cost ([430d54e](https://github.com/Elenterius/Biomancy/commit/430d54e21f0851b51b68d56df330afb01ca7a8c5))

