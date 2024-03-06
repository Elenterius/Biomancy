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

## [4.3.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.2.0...1.20.1-v2.4.3.0) (2024-02-08)


### Features

* Add Pirate-speak lingo. ([010f186](https://github.com/Elenterius/Biomancy/commit/010f18649751b49d5f81dbf5b03f75bf551e3905))
* disable bio-forge recipe progression in the configs by default (temporary measure until progression rework happens) ([80fe7c1](https://github.com/Elenterius/Biomancy/commit/80fe7c14bf82a188eda6d81093b4e81092eb2da7))
* make 1.20 Items decomposable ([794a2b3](https://github.com/Elenterius/Biomancy/commit/794a2b3cde5b87d8acee1bf4b2ba08904f27ddd7))
* make Alex's Caves items decomposable ([9cf7c77](https://github.com/Elenterius/Biomancy/commit/9cf7c77f89eee6e1209b44b72bfab2f6ab5c1c88))
* make Alex's Caves items digestible ([de8d89d](https://github.com/Elenterius/Biomancy/commit/de8d89d7e5e36f334d798e8c8ffb095adb71b63c))
* make more Alex's Mobs Items decomposable ([af5c0c2](https://github.com/Elenterius/Biomancy/commit/af5c0c2aedeb4fa6893597ace855c528798e22f3))
* make moss, dripleaves and hanging roots digestible ([1178799](https://github.com/Elenterius/Biomancy/commit/11787999a30be484b56576b99bfd464fc25408cd))
* make turtle & sniffer egg digestible ([14b29c8](https://github.com/Elenterius/Biomancy/commit/14b29c80def904c63c211f37ccd0dc7feabad7a8))


### Bug Fixes

* exclude non-living entities from entity tags ([1584145](https://github.com/Elenterius/Biomancy/commit/1584145de547dc5eaef38018328e1ae819eff0bf))

## [4.2.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.1.0...1.20.1-v2.4.2.0) (2024-01-22)


### Features

* **injector:** change cancel and clear icons in the wheel menu ([78a2475](https://github.com/Elenterius/Biomancy/commit/78a2475d558ab35d8be40c568ef236f2797f3c40))
* **injector:** change item label color to white in the wheel menu ([1bc4a00](https://github.com/Elenterius/Biomancy/commit/1bc4a00816a65447469c7e04f775630f12925a26))
* **injector:** tweak serum colors ([56376a3](https://github.com/Elenterius/Biomancy/commit/56376a393ef39c70212b1abf724bebe013118c68))


### Bug Fixes

* **injector:** fix mismatching serum colors between item and injector model ([5bd8d96](https://github.com/Elenterius/Biomancy/commit/5bd8d9684d1db70771365ce0621508744ce43278))
* **jei:** fix bad text color in the biomancy recipe previews ([08dc86a](https://github.com/Elenterius/Biomancy/commit/08dc86a22a2b873ed3807d16aa94c7f9230601e9))

## [4.1.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.4.0.1...1.20.1-v2.4.1.0) (2024-01-21)


### Features

* **bio-forge:** improve tabs ([0d1b81d](https://github.com/Elenterius/Biomancy/commit/0d1b81d46b49a0ee2461b03baa20e673550d214a))
* **bio-forge:** render tag ingredients as slideshow (cycles through items) ([42360cd](https://github.com/Elenterius/Biomancy/commit/42360cdc1a6b07a1c059352273d4d96888d7e43d))
* **bio-forge:** reorganize tabs ([4fc9c33](https://github.com/Elenterius/Biomancy/commit/4fc9c33164864a86ca59d08d46689ae56d161796))


### Bug Fixes

* **bio-forge:** fix inability to (un-)focus the search box via mouse click ([b32a997](https://github.com/Elenterius/Biomancy/commit/b32a9978d3d7bee113e17921903909b63a235245))
* **bio-forge:** fix recipe crafting not working with tag ingredients ([1f7fa9a](https://github.com/Elenterius/Biomancy/commit/1f7fa9a1bc6da8e3b1c4b3d1bd9cd0b721fe58f0))
* **injector:** fix misaligned item labels rendering of the wheel gui ([89780ba](https://github.com/Elenterius/Biomancy/commit/89780ba11b55bcbf543fde09b7d847ffa0f2899f))

