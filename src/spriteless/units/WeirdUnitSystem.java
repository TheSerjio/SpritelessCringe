package spriteless.units;

import arc.func.Prov;
import arc.struct.*;
import arc.struct.ObjectMap.Entry;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.*;
import spriteless.entities.*;
import spriteless.entities.UnitEntity;

public class WeirdUnitSystem {
	private static final Entry<Class<? extends Entityc>, Prov<? extends Entityc>>[] types = new Entry[] {
			prov(StackConveyorUnitEntity.class, StackConveyorUnitEntity::new),
			prov(MechUnitEntity.class, MechUnitEntity::new),
			prov(LeggedUnitEntity.class, LeggedUnitEntity::new),
			prov(TankUnitEntity.class, TankUnitEntity::new),
			prov(UnitEntity.class, UnitEntity::new),
			prov(NavalUnitEntity.class, NavalUnitEntity::new),
	};

	private static final ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();

	/**
	 * Internal function to flatmap {@code Class -> Prov} into an {@link Entry}.
	 * 
	 * @author GlennFolker
	 */
	private static <T extends Entityc> Entry<Class<T>, Prov<T>> prov(Class<T> type, Prov<T> prov) {
		Entry<Class<T>, Prov<T>> entry = new Entry<>();
		entry.key = type;
		entry.value = prov;
		return entry;
	}

	/**
	 * Setups all entity IDs and maps them into {@link EntityMapping}.
	 * <p>
	 * Put this inside load()
	 * </p>
	 * 
	 * @author GlennFolker
	 */
	public static void setupID() {
		for (int i = 0,
				j = 0,
				len = EntityMapping.idMap.length; i < len; i++) {
			if (EntityMapping.idMap[i] == null) {
				idMap.put(types[j].key, i);
				EntityMapping.idMap[i] = types[j].value;
				if (++j >= types.length)
					break;
			}
		}
	}

	/**
	 * Retrieves the class ID for a certain entity type.
	 * 
	 * @author GlennFolker
	 */
	public static <T extends Entityc> int classID(Class<T> type) {
		return idMap.get(type, -1);
	}

	public static Block parent(Block block) {
		for (var tree : trees) {
			for (int i = 1; i < tree.length; i++) {
				if (tree[i] == block)
					return tree[i - 1];
			}
		}
		return null;
	}

	private static final ObjectMap<UnitType, ItemStack[]> costs = new ObjectMap<UnitType, ItemStack[]>();

	public static ItemStack[] unitCost(UnitType type) {
		var d = costs.getNull(type);
		if (d == null) {
			d = type.getFirstRequirements();
			if(d == null)
				d = ItemStack.with(Items.silicon, 100, Items.graphite, 100);
			costs.put(type, d);
		}
		return d;
	}

	public static final Block[][] trees = new Block[][] {
			new Block[] { Blocks.copperWall, Blocks.copperWallLarge },
			new Block[] { Blocks.titaniumWall, Blocks.titaniumWallLarge },
			new Block[] { Blocks.thoriumWall, Blocks.thoriumWallLarge },
			new Block[] { Blocks.surgeWall, Blocks.surgeWallLarge },
			new Block[] { Blocks.phaseWall, Blocks.phaseWallLarge },
			new Block[] { Blocks.plastaniumWall, Blocks.plastaniumWallLarge },
			new Block[] { Blocks.berylliumWall, Blocks.berylliumWallLarge },
			new Block[] { Blocks.tungstenWall, Blocks.tungstenWallLarge },
			new Block[] { Blocks.carbideWall, Blocks.carbideWallLarge },
			new Block[] { Blocks.door, Blocks.doorLarge },
			new Block[] { Blocks.reinforcedSurgeWall, Blocks.reinforcedSurgeWallLarge },
			new Block[] { Blocks.scrapWall, Blocks.scrapWallLarge, Blocks.scrapWallHuge, Blocks.scrapWallGigantic },
			new Block[] { Blocks.thruster, Blocks.scrapWallGigantic },
			// distribution //TODO mass driver & cargo points?
			new Block[] { Blocks.conveyor, Blocks.titaniumConveyor, Blocks.armoredConveyor, Blocks.plastaniumConveyor },
			new Block[] { Blocks.router, Blocks.distributor },
			new Block[] { Blocks.itemBridge, Blocks.phaseConveyor },
			new Block[] { Blocks.duct, Blocks.armoredDuct, Blocks.surgeConveyor },
			//
			new Block[] { Blocks.mender, Blocks.mendProjector },
			new Block[] { Blocks.overdriveProjector, Blocks.overdriveDome },
			// liquids
			new Block[] { Blocks.conduit, Blocks.pulseConduit, Blocks.platedConduit },
			new Block[] { Blocks.liquidRouter, Blocks.liquidContainer, Blocks.liquidTank },
			new Block[] { Blocks.reinforcedLiquidRouter, Blocks.reinforcedConduit, Blocks.reinforcedLiquidContainer, Blocks.reinforcedLiquidTank },
			// drills
			new Block[] { Blocks.mechanicalDrill, Blocks.pneumaticDrill, Blocks.laserDrill, Blocks.blastDrill },
			new Block[] { Blocks.waterExtractor, Blocks.oilExtractor },
			new Block[] { Blocks.plasmaBore, Blocks.impactDrill, Blocks.largePlasmaBore, Blocks.eruptionDrill },
			// units
			new Block[] { Blocks.groundFactory, Blocks.additiveReconstructor },
			new Block[] { Blocks.airFactory, Blocks.additiveReconstructor },
			new Block[] { Blocks.navalFactory, Blocks.additiveReconstructor },
			new Block[] { Blocks.additiveReconstructor, Blocks.multiplicativeReconstructor,
						  Blocks.exponentialReconstructor, Blocks.tetrativeReconstructor },
			new Block[] { Blocks.tankFabricator, Blocks.tankRefabricator, Blocks.tankAssembler },
			new Block[] { Blocks.mechFabricator, Blocks.mechRefabricator, Blocks.shipAssembler },
			new Block[] { Blocks.shipFabricator, Blocks.shipRefabricator, Blocks.mechAssembler },
			new Block[] { Blocks.constructor, Blocks.largeConstructor },
			// production
			new Block[] { Blocks.graphitePress, Blocks.multiPress },
			new Block[] { Blocks.siliconSmelter, Blocks.siliconCrucible },
			new Block[] { Blocks.separator, Blocks.disassembler },
			new Block[] { Blocks.pulverizer, Blocks.melter },
			new Block[] { Blocks.pyratiteMixer, Blocks.blastMixer },
			new Block[] { Blocks.coalCentrifuge, Blocks.plastaniumCompressor },
			new Block[] { Blocks.electricHeater, Blocks.slagHeater, Blocks.phaseHeater },
			// storage & cores
			new Block[] { Blocks.container, Blocks.vault, Blocks.coreShard, Blocks.coreFoundation, Blocks.coreNucleus },
			new Block[] { Blocks.reinforcedContainer, Blocks.reinforcedVault, Blocks.coreBastion, Blocks.coreCitadel, Blocks.coreAcropolis },
			// logic
			new Block[] { Blocks.logicDisplay, Blocks.largeLogicDisplay },
			new Block[] { Blocks.microProcessor, Blocks.logicProcessor, Blocks.hyperProcessor },
			new Block[] { Blocks.switchBlock, Blocks.memoryCell, Blocks.memoryBank },
			new Block[] { Blocks.reinforcedMessage, Blocks.canvas },
			// energy
			new Block[] { Blocks.solarPanel, Blocks.largeSolarPanel },
			new Block[] { Blocks.powerNode, Blocks.powerNodeLarge, Blocks.surgeTower, Blocks.beamLink },
			new Block[] { Blocks.battery, Blocks.batteryLarge },
			new Block[] { Blocks.beamNode, Blocks.beamTower, Blocks.beamLink },
			new Block[] { Blocks.chemicalCombustionChamber, Blocks.pyrolysisGenerator, Blocks.fluxReactor, Blocks.neoplasiaReactor },
			new Block[] { Blocks.combustionGenerator, Blocks.steamGenerator, Blocks.differentialGenerator, Blocks.impactReactor },
	};
}
