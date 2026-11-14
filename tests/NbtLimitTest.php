<?php

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\StringTag;

/**
 * NBT tag-count limit tests (SEC-007 fix).
 *
 * Dependency note: NBT and the tag classes depend only on pocketmine\utils\Binary
 * (which itself only needs constants at class level from Entity, never loaded
 * here), so the whole NBT stack is included directly and exercised for real.
 *
 * Payload construction matches NBT's little-endian wire format:
 *   - list header: TAG_List id byte + 2-byte name length (0) + element type byte + 4-byte size (LITTLE: pack("V"))
 *   - a single list is hard-capped at 16384 elements by ListTag::read(),
 *     so the >100000-tag payload uses nested lists to accumulate tags.
 *
 * Limitation: this exercises the parser path only (NBT::read() -> readTag() /
 * ListTag::read() -> countTag()); the network entry points that feed it are
 * not covered here.
 */
class NbtLimitTest{

	public static function run() : array{
		$root = \dirname(__DIR__);
		foreach(["Tag", "NamedTag", "EndTag", "ByteTag", "ShortTag", "IntTag", "LongTag", "FloatTag", "DoubleTag", "ByteArrayTag", "StringTag", "IntArrayTag", "ListTag", "CompoundTag"] as $tag){
			require_once "$root/src/pocketmine/nbt/tag/$tag.php";
		}
		require_once "$root/src/pocketmine/nbt/NBT.php";
		require_once "$root/src/pocketmine/utils/Binary.php";

		return \array_merge(
			self::testConstant(),
			self::testSmallCompound(),
			self::testOversizedThrows()
		);
	}

	private static function testConstant() : array{
		return [
			["max_total_tags_constant", NBT::MAX_TOTAL_TAGS === 100000, "NBT::MAX_TOTAL_TAGS == 100000"],
		];
	}

	private static function testSmallCompound() : array{
		$compound = new CompoundTag("test", [
			new IntTag("foo", 1),
			new StringTag("bar", "hello"),
			new ByteTag("baz", 7),
		]);

		$writer = new NBT(NBT::LITTLE_ENDIAN);
		$writer->setData($compound);
		$packed = $writer->write();

		$reader = new NBT(NBT::LITTLE_ENDIAN);
		$reader->read($packed);
		$parsed = $reader->getData();

		return [
			["small_compound_type", $parsed instanceof CompoundTag, "small payload roundtrips to a CompoundTag"],
			["small_compound_int", $parsed !== null and $parsed->foo instanceof IntTag and $parsed->foo->getValue() === 1, "int tag survived write/read"],
			["small_compound_string", $parsed !== null and $parsed->bar instanceof StringTag and $parsed->bar->getValue() === "hello", "string tag survived write/read"],
			["small_compound_byte", $parsed !== null and $parsed->baz instanceof ByteTag and $parsed->baz->getValue() === 7, "byte tag survived write/read"],
		];
	}

	/**
	 * Builds a nested-list payload whose total tag count exceeds
	 * NBT::MAX_TOTAL_TAGS (100000). One outer list holds 7 inner lists of
	 * 16384 bytes each: root(1) + outer elements(7) + inner lists(7) +
	 * 7*16384 bytes = ~114703 counted tags, well over the cap. NBT must
	 * abort parsing with RuntimeException "NBT contains too many tags"
	 * instead of exhausting memory.
	 *
	 * Wire format note: a list nested inside another list is stored WITHOUT
	 * its own name length (only the root named tag carries one), as
	 * tagType byte + size + elements. The root carries TAG_List id + 2-byte
	 * name length (0) before ListTag::read() runs.
	 */
	private static function testOversizedThrows() : array{
		$inner = \chr(NBT::TAG_Byte) . \pack("V", 16384) . \str_repeat("\x01", 16384);
		$payload = \chr(NBT::TAG_List) . \pack("v", 0) . \chr(NBT::TAG_List) . \pack("V", 7) . \str_repeat($inner, 7);

		$threw = false;
		$message = "";
		try{
			$reader = new NBT(NBT::LITTLE_ENDIAN);
			$reader->read($payload);
		}catch(\RuntimeException $e){
			$threw = true;
			$message = $e->getMessage();
		}

		$expectedTags = 7 * 16384;
		return [
			["oversized_throws", $threw, "payload with $expectedTags tags (> " . NBT::MAX_TOTAL_TAGS . ") was rejected"],
			["oversized_message", $message === "NBT contains too many tags", "exception message matches SEC-007 fix"],
		];
	}
}
