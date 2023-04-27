package spriteless.blocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import spriteless.units.*;

import static mindustry.Vars.*;

//UnitCargoLoader & UnitFactory
public class Tetherer extends Block{//TODO kill unit if deconstruct

    public final Seq<UnitType> possibles = new Seq<UnitType>();

    public Tetherer(){
        super("tetherer");

        solid = false;
        solidifes = false;
        update = true;
        hasItems = true;
        hasLiquids = true;
        liquidCapacity = 30;
        configurable = true;
        size = 2;
        armor = 10;
        requirements(Category.units, ItemStack.with(Items.silicon, 1000, Items.graphite, 1000, Items.thorium, 500, Items.surgeAlloy, 100, Items.phaseFabric, 100));
        consumePower(1);
        consume(new ConsumeItemDynamic((TethererBuild e) -> WeirdUnitSystem.unitCost(e.unitType)));
        consume(new ConsumeLiquid(Liquids.water, 1f));

        Events.on(EventType.BlockDestroyEvent.class, (BlockDestroyEvent bde) -> {
            eradicate(bde.tile.build);
        });

        Events.on(EventType.BlockBuildBeginEvent.class, (BlockBuildBeginEvent bbbe) -> {
            var tile = bbbe.tile;
            var build = tile.build;
            if(build instanceof ConstructBlock.ConstructBuild cb)
                cb.prevBuild.each(b -> eradicate(b));
            eradicate(bbbe.tile.build);
        });
    }

    private static void eradicate(Building build){
        if(build == null || !(build instanceof TethererBuild t))
            return;
        if(t.unit != null)
            t.unit.kill();
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (TethererBuild tb) ->
            new Bar(
            () -> tb.unit == null ? Core.bundle.get("bar.progress") : Core.bundle.get("bar.health"),
            () -> Pal.power,
            () -> tb.unit == null ? tb.progress : tb.unit.health / tb.unit.maxHealth
        ));
    }

    @Override 
    public void load(){
        super.load();
        Utils.transfer(Blocks.unitCargoUnloadPoint, this);
        possibles.addAll(Vars.content.units());
        possibles.filter(u -> !(u instanceof BlockUnitType) && !u.internal);
    }

    public class TethererBuild extends Building{
        public int readUnitId = -1;
        public float progress;
        public @Nullable Unit unit;
        public UnitType unitType = possibles.get(0);

        @Override
        public void updateTile(){
            //unit was lost/destroyed
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            if(unit == null){
                progress += edelta();

                if(progress >= 60f){
                    if(!net.client()){
                        unit = unitType.create(team);
                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }
                        consume();
                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();
                        Fx.spawn.at(x, y);
                        progress = 0f;
                        readUnitId = unit.id;
                    }
                }
            }

            if(unit != null)
            {
                unit.heal(delta());
                var tx = x + unit.x;
                var ty = y + unit.y;
                unit.set(tx / 2, ty / 2);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            if(unit != null)
                return 0;
            for(var items : WeirdUnitSystem.unitCost(unitType))
                if(items.item == item)
                    return items.amount;
            return 0;
        }

        @Override
        public boolean shouldConsume(){
            return unit == null;
        }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);
            if(unit == null){
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(this, unitType.fullIcon, 0f, progress / 60f, efficiency, progress);
                });
            }
        }

        @Override
        public void buildConfiguration(Table table){
            var sqrt = (int)arc.math.Mathf.sqrt(possibles.size);
            ItemSelection.buildTable(Tetherer.this, table, possibles, () -> unitType, unit -> unitType = unit, sqrt, sqrt);
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(unit == null ? -1 : unit.id);
            write.i(unitType.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            readUnitId = read.i();
            unitType = Vars.content.unit(read.i());
        }
    }
}
