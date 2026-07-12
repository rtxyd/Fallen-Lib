# Changelog

## 1.4
---
### 1.4.2

### Runtime.forgemod
- Introduce ILazyPacketBoundRegistry and LazyPacketPayLoad to solve client desync issue
- ExtraGemBonus implementation is changed to above
- Use two marks to indicate the GemRegistry loading sequence on delegated server
- Fix the unbound holder issue when client try to join a server

### 1.4.1
- Bump 1.4.0.4 to 1.4.1 as release version

#### Core
- Introduce LazySupplier for some unknown loading sequence
- Fix StandardInserter reentrance issue at return type

#### Runtime.forgemod
- Tweak registry event priority for better compatibility
- Tweak codec of DefaultRegistryBoundPacketPayload to be thread-safe
- ExtraGemBonus loading message
- For recursion forItem() fallback Sword as LootCategory

### 1.4.0.4
#### Runtime.forgemod
- Add LivingEntityCakyHandler
- Complement AbstractPacketBoundRegistry to have holder
- Add BoundHolder
- Remove default attribute system
- Implement DefaultPacketBoundRegistry and DefaultRegistryBoundPacketPayload

### 1.4.0.3
#### Core
- ObjectModifierFactory, introduce maxFilterMatches.
- ClassResourceProcessor, debug log to trace log, reducing debug log spam
- Introduce ItemAttributeModifierFactory, which can operate modifiers easier
- Backward compatibility for lib version 1.3.2

### 1.4.0.2
- BeforeModifyArgInserterHelper, fix a bug causing if there's no "modifyArg", null point when get store opcode
- AInsAttribute, now reusing the instance to output 2 times won't be double-applied
- AInsAttribute, now allow to access some new fields like "hasSetBase".
- Inserters, "hookAffixApply" now catches correct arg.
- Mark "ItemStackCakyHandler.resolve" as Deprecated

### 1.4.0
#### Core
- Rewrite most standard inserter logic.
- Hide explicit ClassNode into patch context, use getter instead.
- Introduce `InserterType.BEFORE_MODIFY_ARG`
- Scanner engine overhaul
- API changes `IFallenPatch` 
- Massive bug fixes
- Refactor PatchUtil methods
- Refactor `@FallenInserter` annotation
- `IInsAttributeSystem` add and remove modifier
- Implement `ISingeUseAsyncWorker`
- Implement `FinalObjectCaky` \ `ManualObjectCaky`
- Add `InsAttributeModifier` two flags indicate "setFinal" and "setBase" exist
- Expand the `InsAttributeModifier` type with `ADD_MULTIPLIED_BASE` and `ADD_MULTIPLIED_FINAL`
- API change, `FallenInserter` @type -> value / @options -> value / @targets -> value

#### Runtime.forgemod
- Add unsupported operation checks and fix bugs in `AbstractRegistryBoundPacketPayload` and `AbstractPacketBoundRegistry`
- Add `PatchAffixHelper` using FallenPatch and Inserter
- Introduce `AffixApplyEvent`, `AffixCacheRefreshEvent`, `AffixGetFinishedEvent`, `RegisterAffixHasherEvent`
- Bugs fix `extra_gem_bonus`

## 1.3
---
### 1.3.4
#### Core
- 1.3.3.3 Introduce `IInsAttributeSystem`
- 1.3.3.3 Introduce `InserterType.BEFORE_MODIFY_ARG`, APi change `IFallenPatch` remove explicit ClassNode from apply method
#### Runtime.forgemod
- 1.3.3.1 -> ~~Crude SyncableRegistry, with network handler (now it's poor version of `DynamicRegistry` in Placebo)~~
- 1.3.3.1 -> Simple 1.21.1 like Connection for network
- 1.3.3.1/1.3.3.2 -> Implement `AbstractPacketBoundRegistry`, `AbstractRegistryBoundPacketPayload`, `AbstractSingleEntryPacketPayLoad`

### 1.3.3
#### Core
- Mod locator tweak
#### Runtime.forgemod
- Special check for FGA version compat
- Attrlib fix port
- Lootcategory `forItem()` fix

### 1.3.2
- Implement a simple delegating mixin connector registry
- Add empty checks for fallen config json file

### 1.3.1
- Distribute runtime environment
- Add experimental build task (for future use)
- Fix `BuildInfo`

### 1.3.0
- Experimental simple class merge engine (very low quality)
- Implement fallen lib forge runtime mod
- Port extra_gem_bonus from Fallen Gems & Affixes
- Implement fallen lib forge mod lifecycle hook

## 1.2
---
### 1.2.0
- Hotfix, fix buildInfo with wrong version.
- Introduce an each-class cache to cache the `MethodInsnNode` state, avoiding recalculation of local variables.
- Update `ObjectModifierFactory`, make the ignored field filter a `Predicate<String>` to support more flexible control.
- Add `inserter` field to `@FallenPatch` define the class contains methods with `@FallenInserter`
- Introduce `FallenInserter` annotation and parsing.
- Package structure change, `fallen_lib` -> `fallen.lib`
- Introduce standard method inserter.
- Introduce patch util.

## 1.1
---
### 1.1.2
- Fixed entry targets don't include nest members.
- Improved logger behaviors, prevent spamming in log.
- API change, `IFallenPatchContext#getAppliedPatches` -> `IFallenPatchContext#currentClassAppliedPatches`
- Fixed a null point exception when resource is not found.
### 1.1.1
- Added debug infos in patch processing procedure.
- Changed `copyOf()` to unmodifiable version.
- Added context for ctor and patch method `apply()`
- Fixed entries sorting.
- Added an empty check before traversing classes.
- Filtered `minecraft` and `forge` class targets for now.
### 1.1.0
- Reimplemented fallen patch as a configurable API.

## 1.0
---
### 1.0.2
- Aligned mod id and mod main class.
### 1.0.1
- Fixed a `String index out of range` problem when applying transformer.
### 1.0.0
- Initialized.