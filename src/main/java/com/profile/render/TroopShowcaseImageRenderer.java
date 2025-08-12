package com.profile.render;

import com.profile.data.Profile;
import com.profile.data.Unit;
import com.profile.utils.ImageManager;
import com.profile.utils.RenderingUtility;
import com.profile.utils.BlurUtils;
import com.profile.utils.FontUtils;
import com.profile.utils.GradientManager;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroopShowcaseImageRenderer {

    static {
        FontUtils.loadCustomFonts();
    }

    private static final Logger logger = LoggerFactory.getLogger(TroopShowcaseImageRenderer.class);

    public static BufferedImage render(Profile profile) throws IOException {
        logger.info(String.format("Generating troop showcase image for %s", profile.tag));

        int width = 1033;
        int height = 718;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        RenderingUtility.addRenderingHints(g);

        // Background gradient
        GradientPaint background = new GradientPaint(
            0, 0, Color.decode("#8c96af"),
            width, height, Color.decode("#6b7899")
        );
        g.setPaint(background);
        g.fillRect(0, 0, width, height);

        drawHeroSection(g, 18, 18, profile.heroes);
        drawPetSection(g, 18, 236, profile.troops);
        drawTroopSection(g, 298, 18, profile.troops);
        drawSpellSection(g, 753, 18, profile.spells);
        drawSiegeMachineSection(g, 298, 586, profile.troops);
        ImageManager.drawSignature(g, 18, 613, 3);

        g.dispose();
        return image;
    }

    public static boolean isMaxed(Optional<Unit> troop) {
        return troop.get().level == troop.get().maxLevel;
    }

    public static boolean isUnlocked(Optional<Unit> troop) {
        return troop.isPresent();
    }

    public static int getLevel(Optional<Unit> troop) {
        return troop.get().level;
    }

    public static TroopData getTroopData(Unit[] list, String name, String village) {
        Optional<Unit> troop = Arrays.asList(list)
            .stream()
            .filter(t -> t.name.equals(name) && t.village.equals(village))
            .findFirst();

        if (!troop.isPresent()) return new TroopData(false, false, 0);
        
        return new TroopData(
            isMaxed(troop),
            isUnlocked(troop),
            getLevel(troop)
        );
    }

    public static class TroopData {
        public final boolean maxed;
        public final boolean unlocked;
        public final int level;

        public TroopData(boolean maxed, boolean unlocked, int level) {
            this.maxed = maxed;
            this.unlocked = unlocked;
            this.level = level;
        }
    }

    public static void drawHeroSection(Graphics2D g, int x, int y, Unit[] heroes) {
        int width = 263;
        int height = 201;
        int radius = 9;

        g.setColor(Color.decode("#636e8f"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        FontUtils.drawClashFont(g, "Heroes", x + 9, y + 7, 25, false, Color.WHITE, 2);

        TroopData barbarianKing = getTroopData(heroes, "Barbarian King", "home");
        TroopData archerQueen = getTroopData(heroes, "Archer Queen", "home");
        TroopData minionPrince = getTroopData(heroes, "Minion Prince", "home");
        TroopData grandWarden = getTroopData(heroes, "Grand Warden", "home");
        TroopData royalChampion = getTroopData(heroes, "Royal Champion", "home");

        drawTroopIcon(barbarianKing, g, "Icon_HV_Hero_Barbarian_King", x + 9, y + 35);
        drawTroopIcon(archerQueen, g, "Icon_HV_Hero_Archer_Queen", x + 96, y + 35);
        drawTroopIcon(minionPrince, g, "Icon_HV_Hero_Minion_Prince", x + 184, y + 35);

        drawTroopIcon(grandWarden, g, "Icon_HV_Hero_Grand_Warden", x + 9, y + 123);
        drawTroopIcon(royalChampion, g, "Icon_HV_Hero_Royal_Champion", x + 96, y + 123);
    }

    public static void drawPetSection(Graphics2D g, int x, int y, Unit[] pets) {
        int width = 263;
        int height = 376;
        int radius = 9;

        g.setColor(Color.decode("#636e8f"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        FontUtils.drawClashFont(g, "Pets", x + 9, y + 7, 25, false, Color.WHITE, 2);

        TroopData lassi = getTroopData(pets, "L.A.S.S.I", "home");
        TroopData mightyYak = getTroopData(pets, "Mighty Yak", "home");
        TroopData electroOwl = getTroopData(pets, "Electro Owl", "home");
        TroopData unicorn = getTroopData(pets, "Unicorn", "home");
        TroopData phoenix = getTroopData(pets, "Phoenix", "home");
        TroopData poisonLizard = getTroopData(pets, "Poison Lizard", "home");
        TroopData diggy = getTroopData(pets, "Diggy", "home");
        TroopData frosty = getTroopData(pets, "Frosty", "home");
        TroopData spiritFox = getTroopData(pets, "Spirit Fox", "home");
        TroopData angryJelly = getTroopData(pets, "Angry Jelly", "home");
        TroopData sneezy = getTroopData(pets, "Sneezy", "home");

        drawTroopIcon(lassi, g, "Icon_HV_Hero_Pets_LASSI", x + 9, y + 35);
        drawTroopIcon(electroOwl, g, "Icon_HV_Hero_Pets_Electro_Owl", x + 96, y + 35);
        drawTroopIcon(mightyYak, g, "Icon_HV_Hero_Pets_Mighty_Yak", x + 184, y + 35);
        drawTroopIcon(unicorn, g, "Icon_HV_Hero_Pets_Unicorn", x + 9, y + 123);
        drawTroopIcon(frosty, g, "Icon_HV_Hero_Pets_Frosty", x + 96, y + 123);
        drawTroopIcon(diggy, g, "Icon_HV_Hero_Pets_Diggy", x + 184, y + 123);
        drawTroopIcon(poisonLizard, g, "Icon_HV_Hero_Pets_Poison_Lizard", x + 9, y + 210);
        drawTroopIcon(phoenix, g, "Icon_HV_Hero_Pets_Phoenix", x + 96, y + 210);
        drawTroopIcon(spiritFox, g, "Icon_HV_Hero_Pets_Spirit_Fox", x + 184, y + 210);
        drawTroopIcon(angryJelly, g, "Icon_HV_Hero_Pets_Angry_Jelly", x + 9, y + 298);
        drawTroopIcon(sneezy, g, "Icon_HV_Hero_Pets_Sneezy", x + 96, y + 298);
    }

    public static void drawTroopSection(Graphics2D g, int x, int y, Unit[] troops) {
        int width = 438;
        int height = 551;
        int radius = 10;

        g.setColor(Color.decode("#636e8f"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        FontUtils.drawClashFont(g, "Troops", x + 9, y + 7, 25, false, Color.WHITE, 2);

        TroopData barbarian = getTroopData(troops, "Barbarian", "home");
        TroopData archer = getTroopData(troops, "Archer", "home");
        TroopData giant = getTroopData(troops, "Giant", "home");
        TroopData goblin = getTroopData(troops, "Goblin", "home");
        TroopData wallBreaker = getTroopData(troops, "Wall Breaker", "home");
        TroopData balloon = getTroopData(troops, "Balloon", "home");
        TroopData wizard = getTroopData(troops, "Wizard", "home");
        TroopData healer = getTroopData(troops, "Healer", "home");
        TroopData dragon = getTroopData(troops, "Dragon", "home");
        TroopData pekka = getTroopData(troops, "P.E.K.K.A", "home");
        TroopData babyDragon = getTroopData(troops, "Baby Dragon", "home");
        TroopData miner = getTroopData(troops, "Miner", "home");
        TroopData electroDragon = getTroopData(troops, "Electro Dragon", "home");
        TroopData yeti = getTroopData(troops, "Yeti", "home");
        TroopData dragonRider = getTroopData(troops, "Dragon Rider", "home");
        TroopData electroTitan = getTroopData(troops, "Electro Titan", "home");
        TroopData rootRider = getTroopData(troops, "Root Rider", "home");
        TroopData thrower = getTroopData(troops, "Thrower", "home");
        TroopData minion = getTroopData(troops, "Minion", "home");
        TroopData hogRider = getTroopData(troops, "Hog Rider", "home");
        TroopData valkyrie = getTroopData(troops, "Valkyrie", "home");
        TroopData golem = getTroopData(troops, "Golem", "home");
        TroopData witch = getTroopData(troops, "Witch", "home");
        TroopData lavaHound = getTroopData(troops, "Lava Hound", "home");
        TroopData bowler = getTroopData(troops, "Bowler", "home");
        TroopData iceGolem = getTroopData(troops, "Ice Golem", "home");
        TroopData headhunter = getTroopData(troops, "Headhunter", "home");
        TroopData apprenticeWarden = getTroopData(troops, "Apprentice Warden", "home");
        TroopData druid = getTroopData(troops, "Druid", "home");
        TroopData furnace = getTroopData(troops, "Furnace", "home");

        drawTroopIcon(barbarian, g, "Icon_HV_Barbarian", x + 9, y + 35);
        drawTroopIcon(archer, g, "Icon_HV_Archer", x + 96, y + 35);
        drawTroopIcon(giant, g, "Icon_HV_Giant", x + 184, y + 35);
        drawTroopIcon(goblin, g, "Icon_HV_Goblin", x + 271, y + 35);
        drawTroopIcon(wallBreaker, g, "Icon_HV_Wall_Breaker", x + 359, y + 35);

        drawTroopIcon(balloon, g, "Icon_HV_Balloon", x + 9, y + 123);
        drawTroopIcon(wizard, g, "Icon_HV_Wizard", x + 96, y + 123);
        drawTroopIcon(healer, g, "Icon_HV_Healer", x + 184, y + 123);
        drawTroopIcon(dragon, g, "Icon_HV_Dragon", x + 271, y + 123);
        drawTroopIcon(pekka, g, "Icon_HV_P.E.K.K.A", x + 359, y + 123);

        drawTroopIcon(babyDragon, g, "Icon_HV_Baby_Dragon", x + 9, y + 210);
        drawTroopIcon(miner, g, "Icon_HV_Miner", x + 96, y + 210);
        drawTroopIcon(electroDragon, g, "Icon_HV_Electro_Dragon", x + 184, y + 210);
        drawTroopIcon(yeti, g, "Icon_HV_Yeti", x + 271, y + 210);
        drawTroopIcon(dragonRider, g, "Icon_HV_Dragon_Rider", x + 359, y + 210);

        drawTroopIcon(electroTitan, g, "Icon_HV_Electro_Titan", x + 9, y + 298);
        drawTroopIcon(rootRider, g, "Icon_HV_Root_Rider", x + 96, y + 298);
        drawTroopIcon(thrower, g, "Icon_HV_Thrower", x + 184, y + 298);
        drawTroopIcon(minion, g, "Icon_HV_Minion", x + 271, y + 298);
        drawTroopIcon(hogRider, g, "Icon_HV_Hog_Rider", x + 359, y + 298);

        drawTroopIcon(valkyrie, g, "Icon_HV_Valkyrie", x + 9, y + 385);
        drawTroopIcon(golem, g, "Icon_HV_Golem", x + 96, y + 385);
        drawTroopIcon(witch, g, "Icon_HV_Witch", x + 184, y + 385);
        drawTroopIcon(lavaHound, g, "Icon_HV_Lava_Hound", x + 271, y + 385);
        drawTroopIcon(bowler, g, "Icon_HV_Bowler", x + 359, y + 385);

        drawTroopIcon(iceGolem, g, "Icon_HV_Ice_Golem", x + 9, y + 473);
        drawTroopIcon(headhunter, g, "Icon_HV_Headhunter", x + 96, y + 473);
        drawTroopIcon(apprenticeWarden, g, "Icon_HV_Apprentice_Warden", x + 184, y + 473);
        drawTroopIcon(druid, g, "Icon_HV_Druid", x + 271, y + 473);
        drawTroopIcon(furnace, g, "Icon_HV_Furnace", x + 359, y + 473);
    }

    public static void drawSpellSection(Graphics2D g, int x, int y, Unit[] spells) {
        int width = 263;
        int height = 464;
        int radius = 10;

        g.setColor(Color.decode("#636e8f"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        FontUtils.drawClashFont(g, "Spells", x + 9, y + 7, 25, false, Color.WHITE, 2);

        TroopData lightning = getTroopData(spells, "Lightning Spell", "home");
        TroopData heal = getTroopData(spells, "Healing Spell", "home");
        TroopData rage = getTroopData(spells, "Rage Spell", "home");
        TroopData jump = getTroopData(spells, "Jump Spell", "home");
        TroopData freeze = getTroopData(spells, "Freeze Spell", "home");
        TroopData clone = getTroopData(spells, "Clone Spell", "home");
        TroopData invisibility = getTroopData(spells, "Invisibility Spell", "home");
        TroopData recall = getTroopData(spells, "Recall Spell", "home");
        TroopData revive = getTroopData(spells, "Revive Spell", "home");
        TroopData poison = getTroopData(spells, "Poison Spell", "home");
        TroopData earthquake = getTroopData(spells, "Earthquake Spell", "home");
        TroopData haste = getTroopData(spells, "Haste Spell", "home");
        TroopData skeleton = getTroopData(spells, "Skeleton Spell", "home");
        TroopData bat = getTroopData(spells, "Bat Spell", "home");
        TroopData overgrowth = getTroopData(spells, "Overgrowth Spell", "home");

        drawTroopIcon(lightning, g, "Icon_HV_Spell_Lightning", x + 9, y + 35);
        drawTroopIcon(heal, g, "Icon_HV_Spell_Heal", x + 96, y + 35);
        drawTroopIcon(rage, g, "Icon_HV_Spell_Rage", x + 184, y + 35);

        drawTroopIcon(jump, g, "Icon_HV_Spell_Jump", x + 9, y + 123);
        drawTroopIcon(freeze, g, "Icon_HV_Spell_Freeze", x + 96, y + 123);
        drawTroopIcon(clone, g, "Icon_HV_Spell_Clone", x + 184, y + 123);

        drawTroopIcon(invisibility, g, "Icon_HV_Spell_Invisibility", x + 9, y + 210);
        drawTroopIcon(recall, g, "Icon_HV_Spell_Recall", x + 96, y + 210);
        drawTroopIcon(revive, g, "Icon_HV_Spell_Revive", x + 184, y + 210);

        drawTroopIcon(poison, g, "Icon_HV_Dark_Spell_Poison", x + 9, y + 298);
        drawTroopIcon(earthquake, g, "Icon_HV_Dark_Spell_Earthquake", x + 96, y + 298);
        drawTroopIcon(haste, g, "Icon_HV_Dark_Spell_Haste", x + 184, y + 298);

        drawTroopIcon(skeleton, g, "Icon_HV_Dark_Spell_Skeleton", x + 9, y + 385);
        drawTroopIcon(bat, g, "Icon_HV_Dark_Spell_Bat", x + 96, y + 385);
        drawTroopIcon(overgrowth, g, "Icon_HV_Dark_Spell_Overgrowth", x + 184, y + 385);
    }

    public static void drawSiegeMachineSection(Graphics2D g, int x, int y, Unit[] siegeMachines) {
        int width = 718;
        int height = 123;
        int radius = 11;

        g.setColor(Color.decode("#636e8f"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        FontUtils.drawClashFont(g, "Siege Machines", x + 9, y + 7, 25, false, Color.WHITE, 2);

        TroopData wallWrecker = getTroopData(siegeMachines, "Wall Wrecker", "home");
        TroopData battleBlimp = getTroopData(siegeMachines, "Battle Blimp", "home");
        TroopData stoneSlammer = getTroopData(siegeMachines, "Stone Slammer", "home");
        TroopData siegeBarracks = getTroopData(siegeMachines, "Siege Barracks", "home");
        TroopData logLauncher = getTroopData(siegeMachines, "Log Launcher", "home");
        TroopData flameFlinger = getTroopData(siegeMachines, "Flame Flinger", "home");
        TroopData battleDrill = getTroopData(siegeMachines, "Battle Drill", "home");
        TroopData troopLauncher = getTroopData(siegeMachines, "Troop Launcher", "home");
 
        drawTroopIcon(wallWrecker, g, "Icon_HV_Siege_Machine_Wall_Wrecker", x + 9, y + 35);
        drawTroopIcon(battleBlimp, g, "Icon_HV_Siege_Machine_Battle_Blimp", x + 96, y + 35);
        drawTroopIcon(stoneSlammer, g, "Icon_HV_Siege_Machine_Stone_Slammer", x + 184, y + 35);
        drawTroopIcon(siegeBarracks, g, "Icon_HV_Siege_Machine_Siege_Barracks", x + 271, y + 35);
        drawTroopIcon(logLauncher, g, "Icon_HV_Siege_Machine_Log_Launcher", x + 359, y + 35);
        drawTroopIcon(flameFlinger, g, "Icon_HV_Siege_Machine_Flame_Flinger", x + 446, y + 35);
        drawTroopIcon(battleDrill, g, "Icon_HV_Siege_Machine_Battle_Drill", x + 534, y + 35);
        drawTroopIcon(troopLauncher, g, "Icon_HV_Siege_Machine_Troop_Launcher", x + 621, y + 35);
    }

    public static void drawTroopIcon(
        TroopData troopData,
        Graphics2D g,
        String troopName,
        int x,
        int y
    ) {
        BufferedImage image = ImageManager.getCachedImage(troopName);
        drawTroopIconDisplay(g, troopData, image, x, y);
    }

    public static void drawTroopIconDisplay(
        Graphics2D g,
        TroopData troopData,
        BufferedImage image,
        int x,
        int y
    ) {
        int radius = 10;
        int width = 70;
        int height = 70;
        int borderWidth = 1;

        BlurUtils.drawDropShadow(g, x, y, width, height, radius, 0.5f);

        RoundRectangle2D clipShape = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        Shape originalClip = g.getClip();
        g.setClip(clipShape);

        int paddingTop = 1;
        int paddingSides = 1;
        int innerX = x + paddingSides;
        int innerY = y + paddingTop;
        int innerWidth = width - paddingSides * 2;
        int innerHeight = height - paddingTop - paddingSides;
        int innerRadius = radius / 2;

        g.setColor(new Color(152, 152, 205)); // Inner box color
        RenderingUtility.drawRoundedRect(g, innerX, innerY, innerWidth, innerHeight, innerRadius);

        BufferedImage drawnImage = troopData.unlocked ? image : toGrayscale(image);
        g.drawImage(drawnImage, x, y, width, height, null);

        int levelBoxWidth = 21;
        int levelBoxHeight = 21;
        int levelBoxPadding = 2;

        if (troopData.unlocked) {
            drawLevelBox(
                g,
                troopData.level,
                x + levelBoxPadding,
                y + height - levelBoxHeight - levelBoxPadding,
                levelBoxWidth,
                levelBoxHeight,
                2,
                troopData.maxed
            );
        }

        g.setClip(originalClip);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(borderWidth));
        RenderingUtility.drawRoundedRectOutline(g, x, y, width, height, radius);
    }

    public static BufferedImage toGrayscale(BufferedImage original) {
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(original, grayscale);

        return grayscale;
    }

    public static void drawLevelBox(Graphics2D g, int number, int x, int y, int width, int height, int radius, boolean maxed) {
        // Draw base box
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);
        g.setColor(maxed ? new Color(0xE4A23F) : new Color(0x393939));
        g.fill(new RoundRectangle2D.Float(x, y, width, height, radius, radius));

        // Inner shadow bevel effect
        int bevelInset = 2;
        Paint bevelGradient = GradientManager.createOptimizedGradient(
                "bevel", x, y, width, height,
                new Color[]{
                        new Color(0, 0, 0, 64),  // 0.25 alpha
                        new Color(0, 0, 0, 0)
                },
                new float[]{0f, 0.5f},
                false
        );

        Shape bevelRect = new RoundRectangle2D.Float(
                x + bevelInset, y + bevelInset,
                width - bevelInset * 2, height - bevelInset * 2,
                radius - 1, radius - 1
        );

        g.setPaint(bevelGradient);
        g.setClip(bevelRect);
        g.fill(bevelRect);
        g.setClip(null);

        // Outer border glow
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.draw(new RoundRectangle2D.Float(x, y, width, height, radius, radius));

        // Draw level number
        int fontSize = (int)(height * 0.6);
        Font font = new Font("ClashFont", Font.PLAIN, fontSize);
        g.setFont(font);

        int textX = x + width / 2;
        int textY = y + height / 2;

        // Shadow stroke
        FontUtils.drawClashFont(g, String.valueOf(number), textX, textY, fontSize, true, Color.WHITE, 2);
    }
}
