package spriteless.blocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import spriteless.units.*;

import static mindustry.Vars.*;

/* Copied and modified from Reconstructor */
public class Upgrader extends UnitBlock {
    public Seq<BlockUnitType[]> upgrades = new Seq<>();

    public Upgrader() {
        super("upgrader");
        size = 9;
        liquidCapacity = 60;
        itemCapacity = 1;
        consumeLiquid(Liquids.water, 1f / 60f);
        consumeLiquid(Liquids.slag, 1f / 60f);
        requirements(Category.units, ItemStack.with(Items.silicon, 100, Items.graphite, 100));
        consumePower(1f);
        var no = new ItemStack[0];
        consume(new ConsumeItemDynamic((UpgraderBuild build) -> {
            var r = build.currentRecipe();
            if (r == null)
                return no;
            return r.requirements;
        }));
        regionRotated1 = 1;
        regionRotated2 = 2;
        commandable = true;
        ambientSound = Sounds.respawning;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[] { region, inRegion, outRegion, topRegion };
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("progress", (UpgraderBuild entity) -> new Bar("bar.progress", Pal.ammo, entity::fraction));
        addBar("units", (UpgraderBuild e) -> new Bar(
                () -> e.unit() == null ? "[lightgray]" + Iconc.cancel
                        : Core.bundle.format("bar.unitcap",
                                Fonts.getUnicodeStr(e.unit().name),
                                e.team.data().countType(e.unit()),
                                Units.getCap(e.team)),
                () -> Pal.power,
                () -> e.unit() == null ? 0f : (float) e.team.data().countType(e.unit()) / Units.getCap(e.team)));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, table -> {
            table.row();
            for (var tree : WeirdUnitSystem.trees) {
                for (int i = 0; i < tree.length; i++) {
                    
                    var I = i;
                    if(i != 0)
                        table.table(Styles.grayPanel, t -> {
                            t.image(Icon.right).color(Pal.darkishGray).size(40).pad(10f);
                        }).fill().padTop(5).padBottom(5);

                    table.table(Styles.grayPanel, t -> {
                        t.left();

                        t.image(tree[I].uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        t.table(info -> {
                            info.add(tree[I].localizedName).left();
                            info.row();
                        }).pad(10).left();
                    }).fill().padTop(5).padBottom(5);

                }
                table.row();
            }
        });
    }

    @Override
    public void init() {
        for (var tree : WeirdUnitSystem.trees)
            for (var i = 1; i < tree.length; i++)
                upgrades.add(new BlockUnitType[] {
                        BlockUnitType.map.get(tree[i - 1], BlockUnitType.map.get(Blocks.scrapWallGigantic)),
                        BlockUnitType.map.get(tree[i], BlockUnitType.map.get(Blocks.scrapWall)) });

        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
    }

    @Override
    public void load() {
        super.load();
        Utils.transfer(Blocks.tetrativeReconstructor, this);
    }

    public class UpgraderBuild extends UnitBuild {
        public @Nullable Vec2 commandPos;
        public boolean movingOut;

        Block currentRecipe() {
            var u = unit();
            if (u == null)
                return null;
            return ((BlockUnitType) u).sourceBlock;
        }

        public float fraction() {
            var r = currentRecipe();
            if (r == null)
                return 0;
            return progress / r.buildCost;
        }

        @Override
        public boolean shouldActiveSound() {
            return shouldConsume();
        }

        @Override
        public Vec2 getCommandPosition() {
            return commandPos;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return getMaximumAccepted(item) > items.get(item);
        }

        @Override
        public void onCommand(Vec2 target) {
            commandPos = target;
        }

        @Override
        public boolean acceptUnitPayload(Unit unit) {
            return hasUpgrade(unit.type) && !upgrade(unit.type).isBanned();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            if (movingOut)
                return false;
            if (!(this.payload == null
                    && (this.enabled || source == this)
                    && relativeTo(source) != rotation
                    && payload instanceof UnitPayload pay)) {
                return false;
            }

            var upgrade = upgrade(pay.unit.type);

            if (upgrade != null) {
                if (!upgrade.unlockedNowHost() && !team.isAI()) {
                    // flash "not researched"
                    pay.showOverlay(Icon.tree);
                }

                if (upgrade.isBanned()) {
                    // flash an X, meaning 'banned'
                    pay.showOverlay(Icon.cancel);
                }
            }

            return upgrade != null && (team.isAI() || upgrade.unlockedNowHost()) && !upgrade.isBanned();
        }

        @Override
        public int getMaximumAccepted(Item item) {
            var r = currentRecipe();
            if (r == null)
                return 100;
            for (var stack : r.requirements)
                if (stack.item == item)
                    return stack.amount * 2;
            return 100;
        }

        @Override
        public void overwrote(Seq<Building> builds) {
            if (builds.first().block == block) {
                items.add(builds.first().items);
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);

            // draw input
            boolean fallback = true;
            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }
            if (fallback)
                Draw.rect(inRegion, x, y, rotation * 90);

            Draw.rect(outRegion, x, y, rotdeg());

            if (constructing() && hasArrived()) {
                Draw.draw(Layer.blockOver, () -> {
                    var f = fraction();
                    Draw.alpha(1f - f);
                    Draw.rect(payload.unit.type.fullIcon, x, y, payload.rotation() - 90);
                    Draw.reset();
                    Drawf.construct(this, upgrade(payload.unit.type), payload.rotation() - 90f,
                            f, speedScl, time);
                });
            } else {
                Draw.z(Layer.blockOver);

                drawPayload();
            }

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public Object senseObject(LAccess sensor) {
            if (sensor == LAccess.config)
                return unit();
            return super.senseObject(sensor);
        }

        @Override
        public void updateTile() {
            boolean valid = false;

            if (payload != null) {
                // check if offloading
                if (movingOut || !hasUpgrade(payload.unit.type)) {
                    moveOutPayload();
                } else { // update progress
                    if (moveInPayload()) {
                        if (efficiency > 0) {
                            valid = true;
                            progress += edelta() * state.rules.unitBuildSpeed(team);
                        }

                        // upgrade the unit
                        if (progress >= currentRecipe().buildCost) {
                            payload.unit = upgrade(payload.unit.type).create(payload.unit.team());
                            if (commandPos != null && payload.unit.isCommandable()) {
                                payload.unit.command().commandPosition(commandPos);
                            }
                            progress %= 1f;
                            Effect.shake(2f, 3f, this);
                            Fx.producesmoke.at(this);
                            consume();
                            Events.fire(new UnitCreateEvent(payload.unit, this));
                            movingOut = true;
                        }
                    }
                }
            } else if (movingOut)
                movingOut = false;

            speedScl = Mathf.lerpDelta(speedScl, Mathf.num(valid), 0.05f);
            time += edelta() * speedScl * state.rules.unitBuildSpeed(team);
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress)
                return Mathf.clamp(fraction());
            return super.sense(sensor);
        }

        @Override
        public boolean shouldConsume() {
            return constructing() && enabled;
        }

        public UnitType unit() {
            if (payload == null)
                return null;

            UnitType t = upgrade(payload.unit.type);
            return t != null && (t.unlockedNowHost() || team.isAI()) ? t : null;
        }

        public boolean constructing() {
            return payload != null && hasUpgrade(payload.unit.type);
        }

        public boolean hasUpgrade(UnitType type) {
            UnitType t = upgrade(type);
            return t != null && (t.unlockedNowHost() || team.isAI()) && !type.isBanned();
        }

        public UnitType upgrade(UnitType type) {
            UnitType[] r = upgrades.find(u -> u[0] == type);
            return r == null ? null : r[1];
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(progress);
            TypeIO.writeVecNullable(write, commandPos);
            write.bool(movingOut);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if (revision >= 1) {
                progress = read.f();
            }

            if (revision >= 2) {
                commandPos = TypeIO.readVecNullable(read);
            }
            movingOut = read.bool();
        }

    }
}