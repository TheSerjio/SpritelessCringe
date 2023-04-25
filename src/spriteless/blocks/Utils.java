package spriteless.blocks;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadBlock;

public class Utils {
    public static void transferPayload(Block source, Block target){
        for(var f : PayloadBlock.class.getFields())
            if(f.getType()==TextureRegion.class)
                try {
                    f.set(target, f.get(source));
                } catch (Throwable e) {}
    }
    public static void transfer(Block source, Block target){
        for(var f : Block.class.getFields())
            if(f.getType()==TextureRegion.class)
                try {
                    f.set(target, f.get(source));
                } catch (Throwable e) {}
    }
}
