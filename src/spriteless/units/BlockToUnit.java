package spriteless.units;

import arc.graphics.Color;
import arc.math.*;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import spriteless.abilities.*;
import spriteless.entities.*;

public class BlockToUnit {
    public static void init(){
        for(var block : Vars.content.blocks()){
            if(block instanceof StackConveyor q)
                new BlockUnitType(block){{
                    
                    constructor = StackConveyorUnitEntity::new;
                    
                    boostMultiplier = 5f;
                    canBoost = true;
                    mechLandShake = 10f;
                    riseSpeed = 1f / 60f / 10f;
                    itemCapacity = q.itemCapacity;
                    mechSideSway = mechFrontSway = 0;
                    speed = q.speed * 8;
                    mechStepParticles = true;
            
                    abilities.add(new ForceFieldAbility(32, 1, q.health, q.health, 4, 45));
                    abilities.add(new RegenAbility(){{amount = 1; }});
            
                    weapons.add(new Weapon(q.name + "-weapon"){{
                        top = true;
                        shootX = 0;
                        shootY = 0;
                        reload = 10f;
                        mirror = false;
                        rotate = false;
                        x = 0;
                        y = 0;
                        recoil = 0;
                        reload = 60;
                        baseRotation = 90;
                        inaccuracy = 180;
                        ignoreRotation = true;
                        shootCone = 180;
            
                        bullet = new BasicBulletType(){{
                            sprite = "large-bomb";
                            width = height = 8;
            
                            maxRange = range = rangeOverride = splashDamageRadius = 80f;
            
                            backColor = q.glowColor;
                            frontColor = Color.white;
                            mixColorTo = Color.white;
            
                            hitSound = Sounds.plasmaboom;
            
                            shootCone = 180f;
                            ejectEffect = Fx.none;
                            shootEffect = Fx.none;
                            hitShake = 4f;
                            spin = 1;
            
                            lifetime = 60f;
            
                            despawnEffect = Fx.titanExplosion;
                            hitEffect = Fx.massiveExplosion;
                            keepVelocity = false;
            
                            shrinkX = shrinkY = 1f;
            
                            speed = 8f / lifetime;
            
                            splashDamage = 100f;
                        }};
                    }});
                
                    regionLoadRunnable = (StackConveyor sc) -> {
                        this.region = previewRegion = shadowRegion = baseRegion = sc.regions[1];
                        legRegion = sc.edgeRegion;
                        
                        var w = weapons.first();
                        w.region = sc.stackRegion;
                        var b = (BasicBulletType)w.bullet;
                        b.backRegion = b.frontRegion = sc.stackRegion;
                        armor = sc.health;
                    };
                }};
            else if(block instanceof Wall q)
                new BlockUnitType(block){{
                    constructor = TankUnitEntity::new;
                    omniMovement = false;
                    rotateSpeed = 1f;
                    speed = 1f;
                    abilities.add(new EnergyFieldAbility(100, 60, 64){{
                        status = StatusEffects.blasted;
                        statusDuration = 1f;
                        maxTargets = 100;
                        healPercent = 1;
                        targetAir = false;
                        var item = Items.blastCompound;
                        for(var i : Vars.content.items())
                            if(q.name.contains(i.name))
                                item = i;
                        if(q.name.contains("phase") || q == Blocks.shieldedWall)
                            item = Items.phaseFabric;
                        if(q.name.contains("surge"))
                            item = Items.surgeAlloy;
                        if(q.name.contains("door"))
                            item = Items.silicon;
                        if(q == Blocks.thruster){
                            maxTargets = 10;
                            item = Items.silicon;
                        }
                        color = item.color;
                    }});
                }};
            else if(block instanceof LiquidRouter q)
                new BlockUnitType(block){{
                    constructor = UnitEntity::new;
                    speed = 1f / block.size;
                    flying = true;
                    abilities.add(new LiquidExplodeAbility(){{
                        liquid = Liquids.neoplasm;
                        amount = q.liquidCapacity;
                    }});
                    weapons.add(new Weapon(){{
                        top = false;
                        rotate = false;
                        shootCone = 90;
                        shootX = x = shootY = 0;
                        y = hitSize / 2;
                        reload = 10f;
                        inaccuracy = 10f;
                        ejectEffect = Fx.none;
                        recoil = 0f;
                        shootSound = Sounds.flame;
        
                        bullet = new LiquidBulletType(Liquids.neoplasm){{
                            damage = 1;
                            speed = 5f;
                            drag = 0.1f;
                            shootEffect = Fx.shootSmall;
                            lifetime = 60f;
                            collidesAir = false;
                        }};
                    }});
                }};
            
            //TODO split
            else if(block instanceof Autotiler || block instanceof PayloadConveyor || block instanceof DirectionBridge || block instanceof ItemBridge || block instanceof OverflowDuct || block instanceof OverflowGate || block instanceof Sorter || block instanceof Junction || block instanceof LiquidJunction || block instanceof StackRouter || block instanceof DuctRouter || block instanceof DirectionalUnloader || block instanceof Unloader)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    speed = 1f;
                    legCount = 4;
                    legGroupSize = 1;
                    legLength = block.size * 12;

                    regionLoadRunnable = (Block block) -> {
                        this.region = previewRegion = shadowRegion = baseRegion = legRegion = footRegion = legBaseRegion = block.fullIcon;
                        legSplashDamage = 1;
                        legSplashRange = block.health;
                    };
                }};
            
            else if(block instanceof Drill q)
                new BlockUnitType(block){{
                    constructor = UnitEntity::new;
                    flying = true;
                    speed = 1f;
                    defaultCommand = UnitCommand.mineCommand;
                    mineTier = q.tier;
                    mineSpeed = block.size * block.size * 60 / q.drillTime;
                    mineItems = Seq.with(Items.copper, Items.beryllium, Items.lead, Items.tungsten, Items.titanium, Items.thorium);
                    targetAir = false;

                    weapons.add(new Weapon(){{
                        shootX = x = shootY = y = 0;
                        reload = q.drillTime / block.size;
                        ejectEffect = Fx.none;
                        ejectEffect = Fx.casing3;
                        recoil = 0f;
                        shootSound = Sounds.artillery;
        
                        bullet = new ArtilleryBulletType(5f, 100, q.name + (q instanceof BurstDrill ? "-top" : "-rotator")){{
                            hitEffect = Fx.blastExplosion;
                            lifetime = 60f;
                            shrinkInterp = Interp.pow2Out;
                            width = height = block.size * 8;
                            collidesAir = false;
                            shrinkX = shrinkY = 0.5f;
                            splashDamageRadius = width;
                            spin = q.rotateSpeed * block.size;
                            splashDamage = 100;
                            drag = 0.01f;
                        }};
                    }});
                }};

            else if(block instanceof BeamDrill q)
                new BlockUnitType(block){{
                    constructor = UnitEntity::new;
                    flying = true;
                    speed = 1f;
                    drag = 0.01f;
                    defaultCommand = UnitCommand.mineCommand;
                    mineTier = q.tier;
                    mineSpeed = block.size * block.size * 60 / q.drillTime;
                    mineItems = Seq.with(Items.beryllium, Items.graphite, Items.tungsten, Items.thorium);
                    mineFloor = false;
                    mineWalls = true;
                }};

            else if(block instanceof OverdriveProjector q)
                new BlockUnitType(block){{
                    constructor = MechUnitEntity::new;
                    speed = q.speedBoost;
                    var effects = new StatusEffect[]{StatusEffects.overclock, StatusEffects.shielded};
                    var primes = new float[] {Mathf.E, Mathf.PI};
                    for(int i = 0; i < effects.length; i++)
                        abilities.add(new StatusFieldAbility(effects[i], 60 * primes[i] + 60, 60 * primes[i], q.range * q.speedBoost / (q.speedBoost + i)));
                    
                    
                    regionLoadRunnable = (OverdriveProjector op) -> {
                        legRegion = op.topRegion;
                    };
                }};
            
            else if(block instanceof BeamNode || block instanceof ShockMine || block instanceof Battery || block instanceof SolarGenerator)
                new BlockUnitType(block){{
                    constructor = UnitEntity::new;
                    speed = 0.5f;
                    flying = true;
                    rotateSpeed = 10f;
                    abilities.add(new BlockPlacerAbility(block, 300));
                }};

            else if(block instanceof PowerGenerator q)
                new BlockUnitType(block){{
                    constructor = MechUnitEntity::new;
                    canBoost = true;
                    boostMultiplier = 0.5f;
                    riseSpeed = 1f / 60f;
                    speed = 1f;
                    
                    weapons.add(new Weapon(){{
                        shootOnDeath = true;
                        reload = 24f;
                        shootCone = 180f;
                        shootSound = Sounds.explosion;
                        x = shootY = 0f;
                        mirror = false;
                        bullet = new BulletType(){{
                            collidesTiles = false;
                            collides = false;
                            hitSound = q.explodeSound;
        
                            hitEffect = q.explodeEffect;
                            speed = 0f;
                            splashDamageRadius = range = rangeOverride = q.explosionRadius * 8f;
                            instantDisappear = true;
                            splashDamage = q.powerProduction * 30 + q.explosionDamage / 2;
                            killShooter = true;
                            hittable = false;
                            collidesAir = true;
                            makeFire = true;
                        }};
                    }});
                }};

            else if(block instanceof Prop || block instanceof Floor || block instanceof TallBlock || block instanceof TreeBlock)
            {
                if(block == Blocks.air)
                    continue;
                new MimicUnitType(block);
            }

            else if(block instanceof GenericCrafter q)
                new BlockUnitType(block){{
                    constructor = MechUnitEntity::new;
                    speed = 0.5f;
                    canBoost = true;
                    if(q.outputLiquids != null && q.outputLiquids.length == 2)
                    {
                        var w = addWeapon(q.outputLiquids[0], q.craftTime);
                        w.x = 8;
                        w = addWeapon(q.outputLiquids[1], q.craftTime);
                        w.x = -8;
                    }
                    if(q.outputLiquid != null)
                        addWeapon(q.outputLiquid, q.craftTime);
                    if(q.outputItem != null)
                        addWeapon(q.outputItem, q.craftTime);
                    if(block instanceof HeatProducer hp)
                        weapons.add(new Weapon(){{
                            reload = 10f;
                            inaccuracy = 45;
                            x = y = shootX = shootY = 0;
                            mirror = false;
                            bullet = new LightningBulletType(){{
                                lightningColor = hitColor = Pal.turretHeat;
                                damage = hp.heatOutput;
                                lightningLength = 15;
                                lightningLengthRand = 15;
                                status = StatusEffects.melting;
                                statusDuration = 1f;
            
                                lightningType = new BulletType(0.0001f, 0f){{
                                    lifetime = Fx.lightning.lifetime;
                                    hitEffect = Fx.hitLancer;
                                    despawnEffect = Fx.none;
                                    status = StatusEffects.melting;
                                    statusDuration = 1f;
                                    hittable = false;
                                }};
                            }};
                        }});
                }};

            else if(block instanceof UnitFactory q)
                new BlockUnitType(block){{
                    if(block == Blocks.navalFactory){
                        constructor = NavalUnitEntity::new;
                    }
                    else{
                        constructor = LeggedUnitEntity::new;
                        legCount = 4;
                        legGroupSize = 1;
                        legLength = 32;
                    }
                    speed = 0.25f;
                    rotateSpeed = 6;
                    var units = new UnitType[q.plans.size];
                    var times = new float[q.plans.size];
                    int i=0;
                    for (var pair : q.plans) {
                        units[i] = pair.unit;
                        times[i] = pair.time;
                        i++;
                    }
                    abilities.add(new MultiSpawnAbility(units, times, 0, 0));
                    regionLoadRunnable = (Block b) -> {
                        var leg = Blocks.reinforcedPayloadConveyor;
                        if(b == Blocks.groundFactory || b == Blocks.airFactory)
                            leg = Blocks.payloadConveyor;
                        baseRegion = legRegion = footRegion = legBaseRegion = leg.fullIcon;
                    };
                }};
            else if(block instanceof Reconstructor q)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    legCount = 4;
                    legGroupSize = 1;
                    legLength = q.size * 8;
                    speed = 0.25f;
                    rotateSpeed = 6;
                    var units = new UnitType[q.upgrades.size];
                    var times = new float[q.upgrades.size];
                    int i=0;
                    for (var pair : q.upgrades) {
                        units[i] = pair[1];
                        times[i] = q.constructTime;
                        i++;
                    }
                    abilities.add(new MultiSpawnAbility(units, times, 0, 0));
                    regionLoadRunnable = (Block b) -> {
                        var leg = Blocks.reinforcedPayloadConveyor;
                        if(b.name.contains("reconstructor"))
                            leg = Blocks.payloadConveyor;
                        baseRegion = legRegion = footRegion = legBaseRegion = leg.fullIcon;
                    };
                }};
            else if(block instanceof UnitAssembler q)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    legCount = 6;
                    legGroupSize = 1;
                    legLength = 32;
                    speed = 0.125f;
                    rotateSpeed = 12;
                    var units = new UnitType[q.plans.size];
                    var times = new float[q.plans.size];
                    int i = 0;
                    for (var plan : q.plans) {
                        units[i] = plan.unit;
                        times[i] = plan.time;
                        i++;
                    }
                    abilities.add(new MultiSpawnAbility(units, times, 0, 0));
                    regionLoadRunnable = (Block b) -> {
                        var leg = Blocks.reinforcedPayloadConveyor;
                        baseRegion = legRegion = footRegion = legBaseRegion = leg.fullIcon;
                    };
                }};
            else if(block.category == Category.logic){
                if(!block.name.contains("world"))
                    new BlockUnitType(block){{
                        constructor = LeggedUnitEntity::new;
                        legCount = 4;
                        legGroupSize = 1;
                        speed = 1f;
                        rotateSpeed = 6;
                        abilities.add(new UnitSpawnAbility(this, 600f, 0, 0));
                        regionLoadRunnable = (Block q) -> {
                            var base = Blocks.tetrativeReconstructor;
                            Block leg = Blocks.tetrativeReconstructor;
                            Block legBase = Blocks.tetrativeReconstructor;
                            Block foot = Blocks.tetrativeReconstructor;
                            if(q == Blocks.message){
                                base = Blocks.reinforcedMessage;
                                leg = legBase = q;
                                foot = Blocks.reinforcedMessage;
                            }
                            if(q == Blocks.switchBlock){
                                base = Blocks.microProcessor;
                                leg = legBase = foot = q;
                            }
                            if(q == Blocks.microProcessor){
                                base = Blocks.worldProcessor;
                                leg = Blocks.message;
                                legBase = Blocks.memoryCell;
                                foot = Blocks.switchBlock;
                            }
                            if(q == Blocks.logicProcessor){
                                base = Blocks.memoryBank;
                                leg = Blocks.memoryCell;
                                legBase = Blocks.memoryCell;
                                foot = Blocks.microProcessor;
                            }
                            if(q == Blocks.hyperProcessor){
                                base = Blocks.hyperProcessor;
                                leg = Blocks.logicProcessor;
                                legBase = Blocks.memoryBank;
                                foot = Blocks.logicProcessor;
                            }
                            if(q == Blocks.memoryCell){
                                base = Blocks.microProcessor;
                                leg = Blocks.message;
                                legBase = Blocks.message;
                                foot = Blocks.switchBlock;
                            }
                            if(q == Blocks.memoryBank){
                                base = Blocks.logicProcessor;
                                leg = Blocks.memoryCell;
                                legBase = Blocks.memoryCell;
                                foot = Blocks.memoryBank;
                            }
                            if(q == Blocks.logicDisplay){
                                base = Blocks.logicDisplay;
                                leg = Blocks.logicDisplay;
                                legBase = Blocks.logicDisplay;
                                foot = Blocks.hyperProcessor;
                            }
                            if(q == Blocks.largeLogicDisplay){
                                base = q;
                                leg = q;
                                legBase = q;
                                foot = Blocks.logicDisplay;
                            }

                            if(q == Blocks.canvas){
                                base = leg = legBase = foot = q;
                            }
                            if(q == Blocks.reinforcedMessage){
                                base = leg = legBase = foot = q;
                            }

                            legLength = (leg.size + legBase.size) * 4;

                            baseRegion = base.region;
                            legRegion = leg.region;
                            footRegion = foot.region;
                            legBaseRegion = legBase.region;
                        };
                    }};
            }
            else if(block instanceof StorageBlock q)
                new BlockUnitType(block){{
                    speed = 2f / block.size;
                    rotateSpeed = 2f / block.size;
                    if(block instanceof CoreBlock c){
                        abilities.add(new UnitSpawnAbility(c.unitType, 60, 0, 0));
                        regionLoadRunnable = (CoreBlock cb) -> {
                            legRegion = cb.thruster1;
                            cellRegion = cb.teamRegion;
                        };
                        canBoost = true;
                        riseSpeed = 1f / 60f;
                        boostMultiplier = arc.math.Mathf.sqrt(block.size);
                        constructor = MechUnitEntity::new;
                    }
                    else{
                        regionLoadRunnable = (StorageBlock sb) -> {
                            cellRegion = sb.teamRegion;
                        };
                        constructor = TankUnitEntity::new;
                    }
                    weapons.add(new Weapon(){{

                        x = 0f;
                        y = -block.size;
                        mirror = false;
                
                        shootY = 0;
                        reload = 60f;
                        velocityRnd = 0.4f;
                        inaccuracy = block.size;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.missile;
                        shoot.shots = block.size;
                        inaccuracy = block.size;
                        shootCone = 180;
        
                        bullet = new MissileBulletType(2f, 100){{
                            homingPower = 0f;
                            width = 8f;
                            height = 8f;
                            shrinkX = shrinkY = 0f;
                            drag = -0.003f;
                            keepVelocity = true;
                            splashDamageRadius = 32f;
                            splashDamage = 50f;
                            lifetime = 60f * block.size;
                            trailColor = Pal.bulletYellowBack;
                            backColor = Pal.bulletYellowBack;
                            frontColor = Pal.bulletYellow;
                            hitEffect = Fx.blastExplosion;
                            despawnEffect = Fx.blastExplosion;
                            weaveScale = 8f;
                            weaveMag = 2f;
                        }};
                    }});
                }};
            else if(block instanceof Router q)
            {
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    legCount = 4;
                    legGroupSize = 1;
                    legLength = block.size * 12;
                    speed = 2;
                    rotateSpeed = 6;
                    regionLoadRunnable = (Router r) -> {
                        baseRegion = legRegion = legBaseRegion = footRegion = r.region;
                        if(r == Blocks.router)
                            legRegion = legBaseRegion = ((Conveyor)Blocks.conveyor).regions[0][0];
                        ((EnergyFieldAbility)abilities.get(0)).damage = r.health;
                    };
                    abilities.add(new EnergyFieldAbility(100, 60, 80){{
                        healPercent = 0;
                        maxTargets = 4;
                        color = Pal.health;
                    }});
                    if(q.size == 1)
                        abilities.add(new RouterHellAbility(block));
                }};
            }
            else if(block instanceof Constructor q)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    legCount = 4;
                    legGroupSize = 1;
                    legLength = block.size * 16;
                    speed = 0.25f;
                    rotateSpeed = 6;
                    var possibles = new UnitType[]{ this };
                    if(block == Blocks.constructor){
                        possibles = new UnitType[q.filter.size];
                        for(int i = 0; i < possibles.length; i++)
                            possibles[i] = BlockUnitType.map.get(q.filter.get(i));
                    }
                    else if(block == Blocks.largeConstructor)
                        possibles = new UnitType[]{ BlockUnitType.map.get(Blocks.beamTower), BlockUnitType.map.get(Blocks.reinforcedLiquidTank), BlockUnitType.map.get(Blocks.constructor)};
                    var times = new float[possibles.length];
                    for(int i = 0; i < possibles.length; i++)
                        times[i] = ((BlockUnitType)possibles[i]).sourceBlock.buildCost;
                    abilities.add(new MultiSpawnAbility(possibles, times, 0, 0));
                    regionLoadRunnable = (Block b) -> {
                        var leg = Blocks.reinforcedPayloadConveyor;
                        baseRegion = legRegion = footRegion = legBaseRegion = leg.fullIcon;
                    };
                }};
            /*else if(block instanceof Turret q)//TODO damn
                new BlockUnitType(block){{
                    constructor = MechUnitEntity::new;
                    speed = 0.125f;
                    regionLoadRunnable = (Turret q) -> {
                        baseRegion = q.region;
                    };
                }};*/
            else if(block instanceof PowerNode q)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    legCount = q.maxNodes;
                    legLength = q.laserRange * 8;
                    if(block instanceof LongPowerNode){
                        legLength = q.laserRange;
                        cost = ItemStack.with(Items.silicon, 1000, Items.graphite, 1000, Items.thorium, 1000, Items.phaseFabric, 1000, Items.surgeAlloy, 1000);
                    }
                    
                    legGroupSize = 1;
                    regionLoadRunnable = (PowerNode pn) -> {
                        baseRegion = jointRegion = footRegion = pn.laserEnd;
                        legRegion = legBaseRegion = pn.laser;
                        if(pn instanceof LongPowerNode lpn)
                            cellRegion = lpn.glow;
                    };
                    weapons.add(new Weapon(){{
                        mirror = false;
                        x = y = shootX = shootY = 0;
                        reload = 30 / q.maxNodes;
                        inaccuracy = 270;
                        var color = q.laserColor2;
                        bullet = new BasicBulletType(1f, legLength, "missile"){{            
                            lifetime = legLength;
                            width = 12f;
                            height = 22f;
            
                            hitSize = 7f;
                            ammoMultiplier = 1;
                            hitColor = backColor = trailColor = lightningColor = color;
                            frontColor = Color.white;
                            trailWidth = 3f;
                            trailLength = (int)q.laserRange;
                            hitEffect = despawnEffect = Fx.hitBulletColor;
            
                            homingPower = 5f / lifetime;
                        }};
                    }});
                }};
                
            else if(block instanceof Pump q)
                new BlockUnitType(block){{
                    constructor = TankUnitEntity::new;
                    speed = 1f / block.size;
                    abilities.add(new LiquidExplodeAbility(){{
                        liquid = Liquids.slag;
                        amount = q.liquidCapacity;
                    }});
                    immunities.add(StatusEffects.melting);
                    immunities.add(StatusEffects.burning);
                    weapons.add(new Weapon(){{
                        top = false;
                        rotate = false;
                        shootCone = 90;
                        shootX = x = shootY = 0;
                        y = hitSize / 2;
                        reload = 5f / q.pumpAmount / q.size / q.size;
                        inaccuracy = 10f;
                        ejectEffect = Fx.none;
                        recoil = 0f;
                        shootSound = Sounds.flame;
        
                        bullet = new LiquidBulletType(Liquids.slag){{
                            damage = 1;
                            speed = 5f;
                            drag = 0.1f;
                            shootEffect = Fx.shootSmall;
                            lifetime = 60f;
                            collidesAir = false;
                        }};
                    }});
                }};
            else if(block instanceof MendProjector q)
                new BlockUnitType(block){{
                    constructor = MechUnitEntity::new;
                    speed = 0.75f;
                    abilities.add(new EnergyFieldAbility(0, q.reload, q.range){{
                        healPercent = q.healPercent;
                        hitUnits = false;
                    }});
                }};
            else if(block instanceof PayloadSource)
                new BlockUnitType(block){{
                    sandbox = true;
                    constructor = LeggedUnitEntity::new;
                    legCount = 4;
                    legGroupSize = 1;
                    legLength = 32;
                    speed = 1f;
                    rotateSpeed = 3;
                    abilities.add(new MultiSpawnAbility(new UnitType[0], new float[0], 0, 0));
                    regionLoadRunnable = (Block b) -> {
                        var leg = Blocks.payloadConveyor;
                        baseRegion = legRegion = footRegion = legBaseRegion = leg.fullIcon;
                        var unitseq = new Seq<UnitType>();
                        for(var u : Vars.content.units())
                            if(!u.internal)
                                unitseq.add(u);
                        var units = new UnitType[unitseq.size];
                        var floats = new float[unitseq.size];
                        for(int i=0;i<unitseq.size;i++){
                            units[i] = unitseq.get(i);
                            floats[i] = 60f;
                        }
                        var ability = (MultiSpawnAbility)abilities.first();
                        ability.units = units;
                        ability.spawnTimes = floats;
                    };
                }};
                

            else arc.util.Log.info("Unconverted block: " + block.name);
        }
    }
}