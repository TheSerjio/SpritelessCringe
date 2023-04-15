package spriteless.units;

import arc.struct.ObjectMap;
import mindustry.type.UnitType;
import mindustry.world.Block;

public class BlockUnitType extends UnitType {

    public static final ObjectMap<Block, BlockUnitType> map = new ObjectMap<>(64);

    public final Block sourceBlock;

    public arc.func.Cons<Block> regionLoadRunnable;

    public BlockUnitType(Block block){
        super(block.name + "-unit");
        sourceBlock = block;
        localizedName = block.localizedName;
        hitSize = block.size * 8;
        health = block.health;
        armor = block.armor;
        envRequired = block.envRequired;
        envEnabled = block.envEnabled;
        envDisabled = block.envDisabled;
        engineOffset = hitSize / 2;
        squareShape = true;
        map.put(block, this);
    }

    @Override 
    public void load(){
        super.load();
        uiIcon = sourceBlock.uiIcon;
        fullIcon = sourceBlock.fullIcon;
        region = previewRegion = shadowRegion = sourceBlock.region;
        if(regionLoadRunnable != null)
            regionLoadRunnable.get(sourceBlock);
        if(baseRegion == arc.Core.atlas.find("error"))
            baseRegion = arc.Core.atlas.find("window-empty");
        if(treadRegion == arc.Core.atlas.find("error"))
           treadRegion = arc.Core.atlas.find("window-empty");
        if(health <= 0)
            health = sourceBlock.health;//blocks have automatic health calculation
    }
}