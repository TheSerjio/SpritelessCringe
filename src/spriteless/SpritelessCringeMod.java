package spriteless;

import arc.util.Log;
import mindustry.mod.*;
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
        new UpgradableFactory();
        new MimicFactory();
        for(int i = 1; i < 4; i++){
            new Upgrader(i);
        }
    }

    @Override
    public void init() {
        Log.info("init");
    }
}