package spriteless;

import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.mod.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import spriteless.blocks.*;
import spriteless.units.*;

public class SpritelessCringeMod extends Mod{

    public SpritelessCringeMod(){

    }

    @Override
    public void loadContent(){
        Log.info("load content");
        WeirdUnitSystem.setupID();
        spriteless.units.BlockToUnit.init();
        new MimicFactory();
        new UpgradableFactory();
        new Upgrader();
    }

    @Override
    public void init() {
        Log.info("init");
    }
}