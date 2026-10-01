<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace pocketmine\network\protocol;

use pocketmine\item\Item;
use pocketmine\network\compat\PacketHeader;
use pocketmine\network\compat\ProtocolCapabilities;
use pocketmine\network\compat\mappings\BlockMapping;
use pocketmine\network\compat\mappings\EntityMapping;
use pocketmine\network\compat\mappings\ItemMapping;
use pocketmine\network\compat\mappings\MetadataMapping;
use pocketmine\network\compat\mappings\RecipeMapping;

/**
 * 多协议兼容的对外门面 (facade)。
 *
 * 具体实现已拆分到 pocketmine\network\compat\ 下：
 *  - ProtocolVersion / ProtocolCapabilities : 版本事实源与能力判断
 *  - PacketHeader                            : 包头发读取
 *  - mappings\BlockMapping                   : 方块/区块
 *  - mappings\ItemMapping                    : 物品/替身/legacy 文本
 *  - mappings\EntityMapping                  : 实体类型/名称/药水
 *  - mappings\MetadataMapping                : 实体 metadata
 *  - mappings\RecipeMapping                  : 配方
 *
 * 本类仅做转发，保持既有调用方 (Server/Player/Network/DataPacketManager) 不变。
 */
final class ProtocolCompatibility{

	private function __construct(){
	}

	public static function readPacketHeader(string $buffer) : ?array{
		return PacketHeader::read($buffer);
	}

	public static function isProtocol013(int $protocol) : bool{
		return ProtocolCapabilities::isProtocol013($protocol);
	}

	public static function isProtocol012(int $protocol) : bool{
		return ProtocolCapabilities::isProtocol012($protocol);
	}

	public static function isProtocol011(int $protocol) : bool{
		return ProtocolCapabilities::isProtocol011($protocol);
	}

	public static function usesLegacySlotFormat(int $protocol) : bool{
		return ProtocolCapabilities::usesLegacySlotFormat($protocol);
	}

	public static function isLegacy011Protocol(int $protocol) : bool{
		return ProtocolCapabilities::isLegacy011Protocol($protocol);
	}

	public static function isProtocol014(int $protocol) : bool{
		return ProtocolCapabilities::isProtocol014($protocol);
	}

	public static function isProtocol015(int $protocol) : bool{
		return ProtocolCapabilities::isProtocol015($protocol);
	}

	public static function requiresLegacyRedstoneMapping(int $protocol) : bool{
		return ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol);
	}

	public static function getRakLibPacketPrefix(int $protocol) : string{
		return ProtocolCapabilities::getRakLibPacketPrefix($protocol);
	}

	public static function isRestrictedBlockIdFor013(int $blockId) : bool{
		return BlockMapping::isRestrictedBlockIdFor013($blockId);
	}

	public static function isRestrictedBlockIdForProtocol(int $protocol, int $blockId) : bool{
		return BlockMapping::isRestrictedBlockIdForProtocol($protocol, $blockId);
	}

	public static function isRestrictedBlockStateFor013(int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::isRestrictedBlockStateFor013($blockId, $blockMeta);
	}

	public static function isRestrictedBlockStateForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::isRestrictedBlockStateForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function isHiddenBlockIdFor013(int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::isHiddenBlockIdFor013($blockId, $blockMeta);
	}

	public static function isHiddenBlockIdForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::isHiddenBlockIdForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function canUseSlimeBlockPhysics(int $protocol) : bool{
		return ProtocolCapabilities::canUseSlimeBlockPhysics($protocol);
	}

	public static function shouldBlockBreakFor013(int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::shouldBlockBreakFor013($blockId, $blockMeta);
	}

	public static function shouldBlockBreakForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return BlockMapping::shouldBlockBreakForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function mapBlockFor013(int $blockId, int $blockMeta) : array{
		return BlockMapping::mapBlockFor013($blockId, $blockMeta);
	}

	public static function mapBlockForProtocol(int $protocol, int $blockId, int $blockMeta) : array{
		return BlockMapping::mapBlockForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function shouldUseLegacyMappedWoodenDoorAnimation(int $protocol, int $blockId) : bool{
		return BlockMapping::shouldUseLegacyMappedWoodenDoorAnimation($protocol, $blockId);
	}

	public static function shouldAllowLegacyMappedBlockActivation(int $protocol, int $blockId) : bool{
		return BlockMapping::shouldAllowLegacyMappedBlockActivation($protocol, $blockId);
	}

	public static function mapLevelEventDataForProtocol(int $protocol, int $eventId, int $data) : int{
		return BlockMapping::mapLevelEventDataForProtocol($protocol, $eventId, $data);
	}

	public static function mapEntityTypeFor013(int $entityType) : int{
		return EntityMapping::mapEntityTypeFor013($entityType);
	}

	public static function mapEntityTypeForProtocol(int $protocol, int $entityType) : int{
		return EntityMapping::mapEntityTypeForProtocol($protocol, $entityType);
	}

	public static function applyLegacyMappedEntityNameForProtocol(int $protocol, int $entityType, array $metadata) : array{
		return EntityMapping::applyLegacyMappedEntityNameForProtocol($protocol, $entityType, $metadata);
	}

	public static function isRestrictedItemIdFor013(int $itemId) : bool{
		return ItemMapping::isRestrictedItemIdFor013($itemId);
	}

	public static function isRestrictedItemIdForProtocol(int $protocol, int $itemId) : bool{
		return ItemMapping::isRestrictedItemIdForProtocol($protocol, $itemId);
	}

	public static function isMappedItemIdFor013(int $itemId) : bool{
		return ItemMapping::isMappedItemIdFor013($itemId);
	}

	public static function isHiddenItemIdFor013(int $itemId) : bool{
		return ItemMapping::isHiddenItemIdFor013($itemId);
	}

	public static function isHiddenItemIdForProtocol(int $protocol, int $itemId) : bool{
		return ItemMapping::isHiddenItemIdForProtocol($protocol, $itemId);
	}

	public static function isHiddenItemFor013(int $itemId, int $itemMeta = 0) : bool{
		return ItemMapping::isHiddenItemFor013($itemId, $itemMeta);
	}

	public static function isHiddenItemForProtocol(int $protocol, int $itemId, int $itemMeta = 0) : bool{
		return ItemMapping::isHiddenItemForProtocol($protocol, $itemId, $itemMeta);
	}

	public static function isHiddenTileIdFor013(string $tileId) : bool{
		return BlockMapping::isHiddenTileIdFor013($tileId);
	}

	public static function isHiddenTileIdForProtocol(int $protocol, string $tileId) : bool{
		return BlockMapping::isHiddenTileIdForProtocol($protocol, $tileId);
	}

	public static function filterMetadataFor013(array $metadata) : array{
		return MetadataMapping::filterMetadataFor013($metadata);
	}

	public static function filterMetadataForProtocol(int $protocol, array $metadata) : array{
		return MetadataMapping::filterMetadataForProtocol($protocol, $metadata);
	}

	public static function filterEntityMetadataForProtocol(int $protocol, int $entityType, array $metadata) : array{
		return MetadataMapping::filterEntityMetadataForProtocol($protocol, $entityType, $metadata);
	}

	public static function canSendMobEffectIdForProtocol(int $protocol, int $effectId) : bool{
		return EntityMapping::canSendMobEffectIdForProtocol($protocol, $effectId);
	}

	public static function hasLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		return ItemMapping::hasLegacyItemSurrogateForProtocol($protocol, $item);
	}

	public static function isLegacyItemSurrogateForProtocol(int $protocol, Item $item) : bool{
		return ItemMapping::isLegacyItemSurrogateForProtocol($protocol, $item);
	}

	public static function mapCreativeInventoryItemForProtocol(int $protocol, Item $item) : Item{
		return ItemMapping::mapCreativeInventoryItemForProtocol($protocol, $item);
	}

	public static function normalizeCreativeItemForProtocol(int $protocol, Item $item) : Item{
		return ItemMapping::normalizeCreativeItemForProtocol($protocol, $item);
	}

	public static function getLegacySimulatedBlockNameById(int $blockId, int $blockMeta = 0) : ?string{
		return ItemMapping::getLegacySimulatedBlockNameById($blockId, $blockMeta);
	}

	public static function getLegacySimulatedBlockNameForItem(int $protocol, Item $item) : ?string{
		return ItemMapping::getLegacySimulatedBlockNameForItem($protocol, $item);
	}

	public static function canLegacyPlayerBreakMappedBlock(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return ItemMapping::canLegacyPlayerBreakMappedBlock($protocol, $blockId, $blockMeta);
	}

	public static function sanitizeItemFor013(int $itemId, int $itemMeta, int $count) : array{
		return ItemMapping::sanitizeItemFor013($itemId, $itemMeta, $count);
	}

	public static function sanitizeItemForProtocol(int $protocol, int $itemId, int $itemMeta, int $count) : array{
		return ItemMapping::sanitizeItemForProtocol($protocol, $itemId, $itemMeta, $count);
	}

	public static function mapItemForProtocol(int $protocol, Item $item, bool $container = false) : Item{
		return ItemMapping::mapItemForProtocol($protocol, $item, $container);
	}

	public static function isProtocol012RegisteredRecipeItem(int $itemId, int $itemMeta = 0) : bool{
		return RecipeMapping::isProtocol012RegisteredRecipeItem($itemId, $itemMeta);
	}

	public static function mapRecipeItemForProtocol(int $protocol, Item $item) : ?Item{
		return RecipeMapping::mapRecipeItemForProtocol($protocol, $item);
	}

	public static function mapCraftingRecipeForProtocol(int $protocol, $recipe){
		return RecipeMapping::mapCraftingRecipeForProtocol($protocol, $recipe);
	}

	public static function normalizeRecipeClientItemForProtocol(int $protocol, Item $sourceItem, Item $clientItem) : Item{
		return RecipeMapping::normalizeRecipeClientItemForProtocol($protocol, $sourceItem, $clientItem);
	}

	public static function normalizeClientItemForProtocol(int $protocol, Item $item) : Item{
		return ItemMapping::normalizeClientItemForProtocol($protocol, $item);
	}

	public static function itemsMatchAfterClientNormalization(int $protocol, Item $serverItem, Item $clientItem, bool $checkCount = false) : bool{
		return ItemMapping::itemsMatchAfterClientNormalization($protocol, $serverItem, $clientItem, $checkCount);
	}

	public static function normalizeAnvilClientResultForProtocol(int $protocol, Item $target, Item $clientResult) : Item{
		return ItemMapping::normalizeAnvilClientResultForProtocol($protocol, $target, $clientResult);
	}

	public static function sanitizeContainerItemFor013(int $itemId, int $itemMeta, int $count) : array{
		return ItemMapping::sanitizeContainerItemFor013($itemId, $itemMeta, $count);
	}

	public static function sanitizeContainerItemForProtocol(int $protocol, int $itemId, int $itemMeta, int $count) : array{
		return ItemMapping::sanitizeContainerItemForProtocol($protocol, $itemId, $itemMeta, $count);
	}

	public static function itemsMatchAfterProtocolMapping(int $protocol, int $serverItemId, int $serverItemMeta, int $serverCount, int $clientItemId, int $clientItemMeta, int $clientCount) : bool{
		return ItemMapping::itemsMatchAfterProtocolMapping($protocol, $serverItemId, $serverItemMeta, $serverCount, $clientItemId, $clientItemMeta, $clientCount);
	}

	public static function itemsMatchAfter013Mapping(int $serverItemId, int $serverItemMeta, int $serverCount, int $clientItemId, int $clientItemMeta, int $clientCount) : bool{
		return ItemMapping::itemsMatchAfter013Mapping($serverItemId, $serverItemMeta, $serverCount, $clientItemId, $clientItemMeta, $clientCount);
	}

	public static function formatLegacy011StatusTip(float $food, int $level, float $progress) : string{
		return ItemMapping::formatLegacy011StatusTip($food, $level, $progress);
	}

	public static function formatLegacy011EnchantmentsMessage(Item $item) : ?string{
		return ItemMapping::formatLegacy011EnchantmentsMessage($item);
	}

	public static function formatLegacy011ItemDetailsMessage(Item $item) : ?string{
		return ItemMapping::formatLegacy011ItemDetailsMessage($item);
	}

	public static function remapChunkPayloadFor013(string $payload) : string{
		return BlockMapping::remapChunkPayloadFor013($payload);
	}

	public static function remapChunkPayloadForProtocol(int $protocol, string $payload) : string{
		return BlockMapping::remapChunkPayloadForProtocol($protocol, $payload);
	}

	public static function stripHiddenTileEntitiesFor013(string $payload) : string{
		return BlockMapping::stripHiddenTileEntitiesFor013($payload);
	}

	public static function remapPacketBufferFor013(string $buffer) : string{
		return BlockMapping::remapPacketBufferFor013($buffer);
	}

	public static function remapPacketBufferForProtocol(int $protocol, string $buffer) : string{
		return BlockMapping::remapPacketBufferForProtocol($protocol, $buffer);
	}

	public static function remapBatchRawPayloadFor013(string $rawPayload) : string{
		return BlockMapping::remapBatchRawPayloadFor013($rawPayload);
	}

	public static function remapBatchRawPayloadForProtocol(int $protocol, string $rawPayload) : string{
		return BlockMapping::remapBatchRawPayloadForProtocol($protocol, $rawPayload);
	}

	public static function remapBatchPayloadFor013(string $payload, int $compressionLevel = -1) : string{
		return BlockMapping::remapBatchPayloadFor013($payload, $compressionLevel);
	}

	public static function remapBatchPayloadForProtocol(int $protocol, string $payload, int $compressionLevel = -1) : string{
		return BlockMapping::remapBatchPayloadForProtocol($protocol, $payload, $compressionLevel);
	}
}
