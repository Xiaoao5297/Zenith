<?php

namespace pocketmine\level\generator\normal\object;

/**
 * PowerNukkitX village jigsaw pools generated from local PowerNukkitX-master.
 */
final class PnxVillagePools{

	public static function get(string $type){
		$all = self::all();
		return $all[$type] ?? null;
	}

	public static function all() : array{
		return array (
	'plains' => 
	array (
		'entry' => 'village/plains/town_centers',
		'pools' => 
		array (
			'village/plains/town_centers' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/town_centers/plains_fountain_01',
					'weight' => 50,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/town_centers/plains_meeting_point_1',
					'weight' => 50,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/town_centers/plains_meeting_point_2',
					'weight' => 50,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/town_centers/plains_meeting_point_3',
					'weight' => 50,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/plains/zombie/town_centers/plains_fountain_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/plains/zombie/town_centers/plains_meeting_point_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/plains/zombie/town_centers/plains_meeting_point_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/plains/zombie/town_centers/plains_meeting_point_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/plains/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/plains/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/plains/streets/straight_03',
					'weight' => 7,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/plains/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/plains/streets/straight_05',
					'weight' => 3,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/plains/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/plains/streets/crossroad_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/plains/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/plains/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/plains/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/plains/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/plains/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/plains/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/plains/zombie/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/zombie/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/zombie/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/zombie/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_03',
					'weight' => 7,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_05',
					'weight' => 3,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/plains/zombie/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/plains/zombie/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/plains/zombie/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/plains/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_6',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/plains/houses/plains_small_house_8',
					'weight' => 3,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/plains/houses/plains_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/plains/houses/plains_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/plains/houses/plains_big_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/plains/houses/plains_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/plains/houses/plains_butcher_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/plains/houses/plains_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/plains/houses/plains_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/plains/houses/plains_shepherds_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/plains/houses/plains_armorer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/plains/houses/plains_fisher_cottage_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/plains/houses/plains_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/plains/houses/plains_cartographer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/plains/houses/plains_library_1',
					'weight' => 5,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/plains/houses/plains_library_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/plains/houses/plains_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/plains/houses/plains_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/plains/houses/plains_temple_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/plains/houses/plains_temple_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/plains/houses/plains_stable_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/plains/houses/plains_stable_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/plains/houses/plains_large_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/plains/houses/plains_small_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				30 => 
				array (
					'structure' => 'village/plains/houses/plains_animal_pen_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				31 => 
				array (
					'structure' => 'village/plains/houses/plains_animal_pen_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				32 => 
				array (
					'structure' => 'village/plains/houses/plains_animal_pen_3',
					'weight' => 5,
					'projection' => 'rigid',
				),
				33 => 
				array (
					'structure' => 'village/plains/houses/plains_accessory_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				34 => 
				array (
					'structure' => 'village/plains/houses/plains_meeting_point_4',
					'weight' => 3,
					'projection' => 'rigid',
				),
				35 => 
				array (
					'structure' => 'village/plains/houses/plains_meeting_point_5',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/plains/zombie/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_6',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_big_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/plains/houses/plains_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_butcher_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/plains/houses/plains_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_shepherds_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/plains/houses/plains_armorer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/plains/houses/plains_fisher_cottage_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/plains/houses/plains_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/plains/houses/plains_cartographer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/plains/houses/plains_library_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/plains/houses/plains_library_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/plains/houses/plains_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/plains/houses/plains_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/plains/houses/plains_temple_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/plains/houses/plains_temple_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_stable_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/plains/houses/plains_stable_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/plains/houses/plains_large_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/plains/houses/plains_small_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				30 => 
				array (
					'structure' => 'village/plains/houses/plains_animal_pen_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				31 => 
				array (
					'structure' => 'village/plains/houses/plains_animal_pen_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				32 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_animal_pen_3',
					'weight' => 5,
					'projection' => 'rigid',
				),
				33 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_meeting_point_4',
					'weight' => 3,
					'projection' => 'rigid',
				),
				34 => 
				array (
					'structure' => 'village/plains/zombie/houses/plains_meeting_point_5',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/plains/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/terminators/terminator_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/terminators/terminator_04',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/plains/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/plains_lamp_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/plains/zombie/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/plains_lamp_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/plains/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/villagers/baby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/plains/zombie/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/zombie/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/zombie/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/common/animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/horses_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/horses_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/horses_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/horses_4',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/horses_5',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/sheep' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/cats' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cat_black',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/cat_british',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/cat_calico',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/cat_persian',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/cat_ragdoll',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/cat_red',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/cat_siamese',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/cat_tabby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/cat_white',
					'weight' => 1,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/common/animals/cat_jellie',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/butcher_animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/iron_golem' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/iron_golem',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/well_bottoms' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/well_bottom',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
		),
	),
	'desert' => 
	array (
		'entry' => 'village/desert/town_centers',
		'pools' => 
		array (
			'village/desert/town_centers' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/town_centers/desert_meeting_point_1',
					'weight' => 98,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/town_centers/desert_meeting_point_2',
					'weight' => 98,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/town_centers/desert_meeting_point_3',
					'weight' => 49,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/desert/zombie/town_centers/desert_meeting_point_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/desert/zombie/town_centers/desert_meeting_point_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/desert/zombie/town_centers/desert_meeting_point_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/desert/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/streets/corner_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/streets/corner_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/desert/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/desert/streets/straight_03',
					'weight' => 3,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/desert/streets/crossroad_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/desert/streets/crossroad_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/desert/streets/crossroad_03',
					'weight' => 3,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/desert/streets/square_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/desert/streets/square_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/desert/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/desert/zombie/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/zombie/streets/corner_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/zombie/streets/corner_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/zombie/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/desert/zombie/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/desert/zombie/streets/straight_03',
					'weight' => 3,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/desert/zombie/streets/crossroad_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/desert/zombie/streets/crossroad_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/desert/zombie/streets/crossroad_03',
					'weight' => 3,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/desert/zombie/streets/square_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/desert/zombie/streets/square_02',
					'weight' => 3,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/desert/zombie/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/desert/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_6',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/desert/houses/desert_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/desert/houses/desert_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/desert/houses/desert_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/desert/houses/desert_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/desert/houses/desert_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/desert/houses/desert_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/desert/houses/desert_shepherd_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/desert/houses/desert_armorer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/desert/houses/desert_fisher_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/desert/houses/desert_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/desert/houses/desert_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/desert/houses/desert_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/desert/houses/desert_mason_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/desert/houses/desert_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/desert/houses/desert_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/desert/houses/desert_temple_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/desert/houses/desert_large_farm_1',
					'weight' => 11,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/desert/houses/desert_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/desert/houses/desert_farm_2',
					'weight' => 4,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/desert/houses/desert_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/desert/houses/desert_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/desert/zombie/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_6',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/desert/zombie/houses/desert_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/desert/houses/desert_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/desert/houses/desert_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/desert/houses/desert_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/desert/houses/desert_shepherd_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/desert/houses/desert_armorer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/desert/houses/desert_fisher_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/desert/houses/desert_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/desert/houses/desert_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/desert/houses/desert_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/desert/houses/desert_mason_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/desert/houses/desert_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/desert/houses/desert_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/desert/houses/desert_temple_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/desert/houses/desert_large_farm_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/desert/houses/desert_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/desert/houses/desert_farm_2',
					'weight' => 4,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/desert/houses/desert_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/desert/houses/desert_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/desert/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/desert/zombie/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/zombie/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/desert/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/desert_lamp_1',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/desert/zombie/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/desert_lamp_1',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/desert/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/villagers/baby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/desert/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/desert/camel' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/camel_spawn',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/desert/zombie/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/desert/zombie/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/desert/zombie/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
		),
	),
	'savanna' => 
	array (
		'entry' => 'village/savanna/town_centers',
		'pools' => 
		array (
			'village/savanna/town_centers' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/town_centers/savanna_meeting_point_1',
					'weight' => 100,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/town_centers/savanna_meeting_point_2',
					'weight' => 50,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/town_centers/savanna_meeting_point_3',
					'weight' => 150,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/savanna/town_centers/savanna_meeting_point_4',
					'weight' => 150,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/zombie/town_centers/savanna_meeting_point_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/savanna/zombie/town_centers/savanna_meeting_point_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/savanna/zombie/town_centers/savanna_meeting_point_3',
					'weight' => 3,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/savanna/zombie/town_centers/savanna_meeting_point_4',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/savanna/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/savanna/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/streets/straight_05',
					'weight' => 3,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/savanna/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/savanna/streets/straight_08',
					'weight' => 4,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/savanna/streets/straight_09',
					'weight' => 4,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/savanna/streets/straight_10',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/savanna/streets/straight_11',
					'weight' => 4,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/savanna/streets/crossroad_07',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/savanna/streets/split_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/savanna/streets/split_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/savanna/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/savanna/zombie/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/zombie/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/zombie/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_05',
					'weight' => 3,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_08',
					'weight' => 4,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_09',
					'weight' => 4,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_10',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/savanna/zombie/streets/straight_11',
					'weight' => 4,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/savanna/zombie/streets/crossroad_07',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/savanna/zombie/streets/split_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/savanna/zombie/streets/split_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/savanna/zombie/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/savanna/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_6',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/savanna/houses/savanna_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/savanna/houses/savanna_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/savanna/houses/savanna_butchers_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/savanna/houses/savanna_butchers_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/savanna/houses/savanna_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/savanna/houses/savanna_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/savanna/houses/savanna_shepherd_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/savanna/houses/savanna_armorer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/savanna/houses/savanna_fisher_cottage_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/savanna/houses/savanna_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/savanna/houses/savanna_cartographer_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/savanna/houses/savanna_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/savanna/houses/savanna_mason_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/savanna/houses/savanna_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/savanna/houses/savanna_weaponsmith_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/savanna/houses/savanna_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/savanna/houses/savanna_temple_2',
					'weight' => 3,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/savanna/houses/savanna_large_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/savanna/houses/savanna_large_farm_2',
					'weight' => 6,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_farm',
					'weight' => 4,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/savanna/houses/savanna_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/savanna/houses/savanna_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				30 => 
				array (
					'structure' => 'village/savanna/houses/savanna_animal_pen_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/savanna/zombie/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_6',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/savanna/houses/savanna_butchers_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/savanna/houses/savanna_butchers_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/savanna/houses/savanna_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/savanna/houses/savanna_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/savanna/houses/savanna_shepherd_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/savanna/houses/savanna_armorer_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/savanna/houses/savanna_fisher_cottage_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/savanna/houses/savanna_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/savanna/houses/savanna_cartographer_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/savanna/houses/savanna_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/savanna/houses/savanna_mason_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/savanna/houses/savanna_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/savanna/houses/savanna_weaponsmith_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/savanna/houses/savanna_temple_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/savanna/houses/savanna_temple_2',
					'weight' => 3,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/savanna/houses/savanna_large_farm_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_large_farm_2',
					'weight' => 4,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/savanna/houses/savanna_small_farm',
					'weight' => 4,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/savanna/houses/savanna_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				30 => 
				array (
					'structure' => 'village/savanna/zombie/houses/savanna_animal_pen_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/savanna/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/terminators/terminator_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/terminators/terminator_04',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/terminators/terminator_05',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/savanna/zombie/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/terminators/terminator_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/terminators/terminator_04',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/savanna/zombie/terminators/terminator_05',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/savanna/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/savanna_lamp_post_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
			),
			'village/savanna/zombie/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/savanna_lamp_post_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
			),
			'village/savanna/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/villagers/baby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/savanna/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/savanna/zombie/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/savanna/zombie/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/savanna/zombie/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/common/animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/horses_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/horses_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/horses_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/horses_4',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/horses_5',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/sheep' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/cats' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cat_black',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/cat_british',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/cat_calico',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/cat_persian',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/cat_ragdoll',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/cat_red',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/cat_siamese',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/cat_tabby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/cat_white',
					'weight' => 1,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/common/animals/cat_jellie',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/butcher_animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/iron_golem' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/iron_golem',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/well_bottoms' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/well_bottom',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
		),
	),
	'taiga' => 
	array (
		'entry' => 'village/taiga/town_centers',
		'pools' => 
		array (
			'village/taiga/town_centers' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/town_centers/taiga_meeting_point_1',
					'weight' => 49,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/town_centers/taiga_meeting_point_2',
					'weight' => 49,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/zombie/town_centers/taiga_meeting_point_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/zombie/town_centers/taiga_meeting_point_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/taiga/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/taiga/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/taiga/streets/straight_03',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/taiga/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/taiga/streets/straight_05',
					'weight' => 7,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/taiga/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/taiga/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/taiga/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/taiga/zombie/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/zombie/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/zombie/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/zombie/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_03',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_05',
					'weight' => 7,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/taiga/zombie/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/taiga/zombie/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/taiga/zombie/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/taiga/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_house_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_house_2',
					'weight' => 4,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_house_3',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_house_4',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_house_5',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/taiga/houses/taiga_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/taiga/houses/taiga_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/taiga/houses/taiga_medium_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/taiga/houses/taiga_medium_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/taiga/houses/taiga_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/taiga/houses/taiga_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/taiga/houses/taiga_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/taiga/houses/taiga_shepherds_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/taiga/houses/taiga_armorer_house_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/taiga/houses/taiga_armorer_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/taiga/houses/taiga_fisher_cottage_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/taiga/houses/taiga_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/taiga/houses/taiga_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/taiga/houses/taiga_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/taiga/houses/taiga_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/taiga/houses/taiga_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/taiga/houses/taiga_weaponsmith_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/taiga/houses/taiga_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/taiga/houses/taiga_large_farm_1',
					'weight' => 6,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/taiga/houses/taiga_large_farm_2',
					'weight' => 6,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_farm_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/taiga/houses/taiga_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/taiga/zombie/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_small_house_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_small_house_2',
					'weight' => 4,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_small_house_3',
					'weight' => 4,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_small_house_4',
					'weight' => 4,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_small_house_5',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_medium_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_medium_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/taiga/houses/taiga_butcher_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/taiga/houses/taiga_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_shepherds_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/taiga/houses/taiga_armorer_house_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_fisher_cottage_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/taiga/houses/taiga_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/taiga/houses/taiga_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/taiga/houses/taiga_weaponsmith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_weaponsmith_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/taiga/houses/taiga_large_farm_1',
					'weight' => 6,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/taiga/zombie/houses/taiga_large_farm_2',
					'weight' => 6,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/taiga/houses/taiga_small_farm_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/taiga/houses/taiga_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/taiga/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/terminators/terminator_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/terminators/terminator_04',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/taiga/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/taiga_lamp_post_1',
					'weight' => 10,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_4',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_6',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/taiga/zombie/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_1',
					'weight' => 4,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/taiga/taiga_decoration_4',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/taiga/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/villagers/baby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/taiga/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/taiga/zombie/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/taiga/zombie/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/taiga/zombie/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
		),
	),
	'snowy' => 
	array (
		'entry' => 'village/snowy/town_centers',
		'pools' => 
		array (
			'village/snowy/town_centers' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/town_centers/snowy_meeting_point_1',
					'weight' => 100,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/town_centers/snowy_meeting_point_2',
					'weight' => 50,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/town_centers/snowy_meeting_point_3',
					'weight' => 150,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/snowy/zombie/town_centers/snowy_meeting_point_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/snowy/zombie/town_centers/snowy_meeting_point_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/snowy/zombie/town_centers/snowy_meeting_point_3',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/snowy/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/snowy/streets/square_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/snowy/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/snowy/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/snowy/streets/straight_03',
					'weight' => 4,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/snowy/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/snowy/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/snowy/streets/straight_08',
					'weight' => 4,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/snowy/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/snowy/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/snowy/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/snowy/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/snowy/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/snowy/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/snowy/zombie/streets' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/zombie/streets/corner_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/zombie/streets/corner_02',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/zombie/streets/corner_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/snowy/zombie/streets/square_01',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_03',
					'weight' => 4,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_04',
					'weight' => 7,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_06',
					'weight' => 4,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/snowy/zombie/streets/straight_08',
					'weight' => 4,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/snowy/zombie/streets/crossroad_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/snowy/zombie/streets/crossroad_03',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/snowy/zombie/streets/crossroad_04',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/snowy/zombie/streets/crossroad_05',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/snowy/zombie/streets/crossroad_06',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/snowy/zombie/streets/turn_01',
					'weight' => 3,
					'projection' => 'rigid',
				),
			),
			'village/snowy/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_4',
					'weight' => 3,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_6',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/snowy/houses/snowy_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/snowy/houses/snowy_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/snowy/houses/snowy_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/snowy/houses/snowy_medium_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/snowy/houses/snowy_butchers_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/snowy/houses/snowy_butchers_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/snowy/houses/snowy_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/snowy/houses/snowy_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/snowy/houses/snowy_shepherds_house_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/snowy/houses/snowy_armorer_house_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/snowy/houses/snowy_armorer_house_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/snowy/houses/snowy_fisher_cottage',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/snowy/houses/snowy_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/snowy/houses/snowy_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/snowy/houses/snowy_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/snowy/houses/snowy_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/snowy/houses/snowy_masons_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/snowy/houses/snowy_weapon_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/snowy/houses/snowy_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/snowy/houses/snowy_farm_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/snowy/houses/snowy_farm_2',
					'weight' => 3,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/snowy/houses/snowy_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/snowy/houses/snowy_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/snowy/zombie/houses' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_3',
					'weight' => 2,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_4',
					'weight' => 2,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_5',
					'weight' => 2,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_6',
					'weight' => 2,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_7',
					'weight' => 2,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_small_house_8',
					'weight' => 2,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_medium_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_medium_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				10 => 
				array (
					'structure' => 'village/snowy/zombie/houses/snowy_medium_house_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				11 => 
				array (
					'structure' => 'village/snowy/houses/snowy_butchers_shop_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				12 => 
				array (
					'structure' => 'village/snowy/houses/snowy_butchers_shop_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				13 => 
				array (
					'structure' => 'village/snowy/houses/snowy_tool_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				14 => 
				array (
					'structure' => 'village/snowy/houses/snowy_fletcher_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				15 => 
				array (
					'structure' => 'village/snowy/houses/snowy_shepherds_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				16 => 
				array (
					'structure' => 'village/snowy/houses/snowy_armorer_house_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				17 => 
				array (
					'structure' => 'village/snowy/houses/snowy_armorer_house_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				18 => 
				array (
					'structure' => 'village/snowy/houses/snowy_fisher_cottage',
					'weight' => 2,
					'projection' => 'rigid',
				),
				19 => 
				array (
					'structure' => 'village/snowy/houses/snowy_tannery_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				20 => 
				array (
					'structure' => 'village/snowy/houses/snowy_cartographer_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				21 => 
				array (
					'structure' => 'village/snowy/houses/snowy_library_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				22 => 
				array (
					'structure' => 'village/snowy/houses/snowy_masons_house_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				23 => 
				array (
					'structure' => 'village/snowy/houses/snowy_masons_house_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
				24 => 
				array (
					'structure' => 'village/snowy/houses/snowy_weapon_smith_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				25 => 
				array (
					'structure' => 'village/snowy/houses/snowy_temple_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				26 => 
				array (
					'structure' => 'village/snowy/houses/snowy_farm_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				27 => 
				array (
					'structure' => 'village/snowy/houses/snowy_farm_2',
					'weight' => 3,
					'projection' => 'rigid',
				),
				28 => 
				array (
					'structure' => 'village/snowy/houses/snowy_animal_pen_1',
					'weight' => 2,
					'projection' => 'rigid',
				),
				29 => 
				array (
					'structure' => 'village/snowy/houses/snowy_animal_pen_2',
					'weight' => 2,
					'projection' => 'rigid',
				),
			),
			'village/snowy/terminators' => 
			array (
				0 => 
				array (
					'structure' => 'village/plains/terminators/terminator_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/plains/terminators/terminator_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/plains/terminators/terminator_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/plains/terminators/terminator_04',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/snowy/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_01',
					'weight' => 4,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_02',
					'weight' => 4,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/snowy/zombie/decor' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_01',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_02',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/snowy_lamp_post_03',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/snowy/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/villagers/baby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/snowy/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/snowy/zombie/villagers' => 
			array (
				0 => 
				array (
					'structure' => 'village/snowy/zombie/villagers/nitwit',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/snowy/zombie/villagers/unemployed',
					'weight' => 10,
					'projection' => 'rigid',
				),
			),
			'village/common/animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 7,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/horses_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/horses_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/horses_3',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/horses_4',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/horses_5',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/sheep' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/cats' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cat_black',
					'weight' => 1,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/cat_british',
					'weight' => 1,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/cat_calico',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/cat_persian',
					'weight' => 1,
					'projection' => 'rigid',
				),
				4 => 
				array (
					'structure' => 'village/common/animals/cat_ragdoll',
					'weight' => 1,
					'projection' => 'rigid',
				),
				5 => 
				array (
					'structure' => 'village/common/animals/cat_red',
					'weight' => 1,
					'projection' => 'rigid',
				),
				6 => 
				array (
					'structure' => 'village/common/animals/cat_siamese',
					'weight' => 1,
					'projection' => 'rigid',
				),
				7 => 
				array (
					'structure' => 'village/common/animals/cat_tabby',
					'weight' => 1,
					'projection' => 'rigid',
				),
				8 => 
				array (
					'structure' => 'village/common/animals/cat_white',
					'weight' => 1,
					'projection' => 'rigid',
				),
				9 => 
				array (
					'structure' => 'village/common/animals/cat_jellie',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/butcher_animals' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/animals/cows_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				1 => 
				array (
					'structure' => 'village/common/animals/pigs_1',
					'weight' => 3,
					'projection' => 'rigid',
				),
				2 => 
				array (
					'structure' => 'village/common/animals/sheep_1',
					'weight' => 1,
					'projection' => 'rigid',
				),
				3 => 
				array (
					'structure' => 'village/common/animals/sheep_2',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/iron_golem' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/iron_golem',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
			'village/common/well_bottoms' => 
			array (
				0 => 
				array (
					'structure' => 'village/common/well_bottom',
					'weight' => 1,
					'projection' => 'rigid',
				),
			),
		),
	),
);
	}
}
