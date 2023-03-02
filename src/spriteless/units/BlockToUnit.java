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
import mindustry.type.Weapon;
import mindustry.world.Block;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.production.*;
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
                
                    regionLoadRunnable = (Block block) -> {
                        var sc = (StackConveyor)block;
                        this.region = previewRegion = shadowRegion = baseRegion = sc.regions[1];
                        legRegion = sc.edgeRegion;
                        
                        var w = weapons.first();
                        w.region = sc.stackRegion;
                        var b = (BasicBulletType)w.bullet;
                        b.backRegion = b.frontRegion = sc.stackRegion;
                        armor = sc.health;
                    };
                }};
            else if(block instanceof Wall q && block != Blocks.thruster)
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
                        color = Items.blastCompound.color;
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
                            speed = 3f;
                            drag = 0.1f;
                            shootEffect = Fx.shootSmall;
                            lifetime = 60f;
                            collidesAir = false;
                        }};
                    }});
                }};
            
            else if(block instanceof Autotiler q)
                new BlockUnitType(block){{
                    constructor = LeggedUnitEntity::new;
                    speed = 1f;
                    legGroupSize = 1;
                    legLength = 40;
                    legSplashDamage = 1;

                    regionLoadRunnable = (Block block) -> {
                        this.region = previewRegion = shadowRegion = baseRegion = legRegion = footRegion = legBaseRegion = block.fullIcon;
                        legSplashRange = block.health;
                        armor = block.health;
                    };
                }};
            
            else if(block instanceof Drill q)
                new BlockUnitType(block){{
                    constructor = UnitEntity::new;
                    flying = true;
                    speed = 1f;
                    drag = 0.01f;
                    defaultCommand = UnitCommand.mineCommand;
                    mineTier = q.tier;
                    mineSpeed = block.size * block.size * 60 / q.drillTime;
                    mineItems = Seq.with(Items.copper, Items.beryllium, Items.lead, Items.tungsten, Items.titanium, Items.thorium);

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
        }
    }
}