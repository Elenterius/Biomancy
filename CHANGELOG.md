## [9.11.0-alpha.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.9.10.0-alpha.0...1.20.1-v2.9.11.0-alpha.0) (2026-08-16)


### Features

* add server config option to disable transliteration ([3bdb7bc](https://github.com/Elenterius/Biomancy/commit/3bdb7bc2b73efb973bcc2be37dfd9aeece5eb186))
* **crowdin:** update Chinese Simplified translation [skip ci] ([7cf942c](https://github.com/Elenterius/Biomancy/commit/7cf942c59690f9f0795b95cea59a35bd0158c048))
* **crowdin:** update Chinese Simplified translation [skip ci] ([1e65083](https://github.com/Elenterius/Biomancy/commit/1e65083502b401d6bf09973f8ab044d4ecba119a))
* **crowdin:** update Korean translation [skip ci] ([2bd35a6](https://github.com/Elenterius/Biomancy/commit/2bd35a68fc70b2e25e1f2a69a70b89aadb6907b9))
* **crowdin:** update Pirate English translation [skip ci] ([273f13a](https://github.com/Elenterius/Biomancy/commit/273f13aecf7f8133e2fc3478d432a43c45c1f9bc))
* **crowdin:** update Russian translation [skip ci] ([30983f1](https://github.com/Elenterius/Biomancy/commit/30983f196f0ee5a7032352051698c43a3405cb89))
* **crowdin:** update Russian translation [skip ci] ([0680139](https://github.com/Elenterius/Biomancy/commit/06801398957dd01813eefd7ac0de222d9a753f6e))
* **crowdin:** update Russian translation [skip ci] ([746bdfa](https://github.com/Elenterius/Biomancy/commit/746bdfa87624e1a2441316be267a37c39eaec52c))
* **crowdin:** update Spanish translation [skip ci] ([eff2380](https://github.com/Elenterius/Biomancy/commit/eff2380260309ff14979b3680e93f7f5c48deab1))
* make Parasitic Metabolism enchantment consume saturation first before consuming hunger ([055b781](https://github.com/Elenterius/Biomancy/commit/055b781db771722248342c1d3e7bff1a2ba1ec20)), closes [#203](https://github.com/Elenterius/Biomancy/issues/203)
* nerf damage and knockback of decay & volatile grenade explosions ([c36b2dd](https://github.com/Elenterius/Biomancy/commit/c36b2dd68b18dd815bcba01d3c03f568e38274e7))
* prevent Self-Feeding enchantment from wasting fuel (nutrient paste/bar, raw meat) ([3b35621](https://github.com/Elenterius/Biomancy/commit/3b3562108d99a87667e877f3f9ba2e8badaa36c4)), closes [#204](https://github.com/Elenterius/Biomancy/issues/204)
* remove curse from Parasitic Metabolism enchantment ([0c92e9e](https://github.com/Elenterius/Biomancy/commit/0c92e9ee1081ab8a47533292bea4c4c1babe935b))


### Bug Fixes

* add missing forge armor tags to items ([5380c01](https://github.com/Elenterius/Biomancy/commit/5380c01b31be4c84d5b022b9444e5974c4151144)), closes [#212](https://github.com/Elenterius/Biomancy/issues/212)
* fix crash when inserting fluids into the cradle while the milk fluid (from Forge) isn't enabled ([429fdb8](https://github.com/Elenterius/Biomancy/commit/429fdb83696cfe09e3cc1e46d4afb244bed63f0a)), closes [#214](https://github.com/Elenterius/Biomancy/issues/214)
* fix startup crash when other mods are present that use the H2-database library ([344c359](https://github.com/Elenterius/Biomancy/commit/344c359edc41905f1b3989c8a7a21b5a4c4385e7)), closes [#210](https://github.com/Elenterius/Biomancy/issues/210)

## [9.10.0-alpha.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.9.9.0-alpha.0...1.20.1-v2.9.10.0-alpha.0) (2026-03-23)


### Features

* **caustic-gunblade:** adjust acid reflux behavior and add visual indicator ([79efdfa](https://github.com/Elenterius/Biomancy/commit/79efdfa3f6be539895d157e012740bf46f996693))
* **caustic-gunblade:** rework behavior, attributes and projectile (decrease damage, increase fire rate, increase max ammo) ([5abc8e8](https://github.com/Elenterius/Biomancy/commit/5abc8e828e60d9ed3ba873a341d7ad460134d55c))
* **crowdin:** update translations [skip ci] ([e95b16d](https://github.com/Elenterius/Biomancy/commit/e95b16d6f0028ea83bb517552994590110ba01d6))
* **decay-grenade:** nerf explosion damage/knockback ([c15ddb6](https://github.com/Elenterius/Biomancy/commit/c15ddb614366a4cf665ef63034e45f3ea75c5f88))
* make pehkui size scaling affect melee damage, defense and knockback of mobs/player and fix unintended damage/size scaling from dropped items and grenade explosions ([87818bc](https://github.com/Elenterius/Biomancy/commit/87818bcd7bdb5a01418cacb1ebc1f75a3843363c))
* **projectile-weapons:** add multi-shot support, horizontal range estimation and refactor ammo reload ([dede3af](https://github.com/Elenterius/Biomancy/commit/dede3aff99de9b0eecd93e660e0a6a71101ef270))
* **projectile-weapons:** overhaul accuracy and shooting ([54e55a1](https://github.com/Elenterius/Biomancy/commit/54e55a1ab403ecb02964f52b356b121d96d89e8d))
* remove enchantment glint from living items ([f5eb61c](https://github.com/Elenterius/Biomancy/commit/f5eb61c986c4d945b23f70ddb6731499656f5d09))
* update acidic egg texture ([040b59e](https://github.com/Elenterius/Biomancy/commit/040b59eca0b6b2ea1ffa3745da10e785f492588e))
* update flowing acid fluid texture ([a2d5b4e](https://github.com/Elenterius/Biomancy/commit/a2d5b4e4a4a0b557191e535d1eeec83efb3bbec4))
* update gelling agent and water gel textures ([5a77bb5](https://github.com/Elenterius/Biomancy/commit/5a77bb582c1c6b15da2337c3c6c079a95d05833a))


### Bug Fixes

* fix crash when trying to render player arms ([6ebddd2](https://github.com/Elenterius/Biomancy/commit/6ebddd24497fc815d0d75c183f072b3ac791b925)), closes [#201](https://github.com/Elenterius/Biomancy/issues/201)
* fix projectile offset when shooting guns with the left hand ([cf193f7](https://github.com/Elenterius/Biomancy/commit/cf193f717975cb78198e19504bf804b6ecfd2fa5))
* improve alignment of gun reload indicator ([f889bba](https://github.com/Elenterius/Biomancy/commit/f889bba5d33cb2a448dc377d481bc667abc74f15))

