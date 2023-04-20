package spriteless.units;

import arc.struct.ObjectMap;
import mindustry.entities.bullet.*;
import mindustry.type.*;
import mindustry.world.Block;

public class BlockUnitType extends UnitType {

    public static final ObjectMap<Block, BlockUnitType> map = new ObjectMap<>(64);

    public final Block sourceBlock;

    public arc.func.Cons<? extends Block> regionLoadRunnable;

    public ItemStack[] cost;

    public BlockUnitType(Block block){
        super(block.name + "-unit");
        sourceBlock = block;
        cost = block.requirements;
        localizedName = "\"" + block.localizedName + "\"";
        hitSize = block.size * 8;
        health = block.health;
        armor = block.armor;
        envRequired = block.envRequired;
        envEnabled = block.envEnabled;
        envDisabled = block.envDisabled;
        engineOffset = hitSize / 2;
        engineSize = arc.math.Mathf.sqrt(block.size) * 2f;
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
            ((arc.func.Cons<Block>)regionLoadRunnable).get(sourceBlock);
        if(baseRegion == arc.Core.atlas.find("error"))
            baseRegion = arc.Core.atlas.find("window-empty");
        if(treadRegion == arc.Core.atlas.find("error"))
           treadRegion = arc.Core.atlas.find("window-empty");
        if(health <= 0)
            health = sourceBlock.health;//blocks have automatic health calculation
        itemCapacity = sourceBlock.itemCapacity;
    }

    public Weapon addWeapon(LiquidStack liquid, float Reload){
        var w = new Weapon(){{
            reload = Reload;
            mirror = false;
            inaccuracy = 10;
            shoot.shots = (int)liquid.amount;
            x = y = shootX = shootY = 0;
            bullet = new LiquidBulletType(liquid.liquid){{
                damage = 1;
            }};
        }};
        weapons.add(w);
        return w;
    }

    public void addWeapon(ItemStack item, float Reload){
        weapons.add(new Weapon(){{
            reload = Reload;
            mirror = false;
            inaccuracy = 10;
            shoot.shots = item.amount;
            x = y = shootX = shootY = 0;
            bullet = new ArtilleryBulletType(3, item.item.cost, item.item.name){{
                
            }};
        }});
    }
}