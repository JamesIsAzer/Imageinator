package com.profile.render;

import com.profile.data.Profile;
import com.profile.utils.ImageManager;
import com.profile.utils.RenderingUtility;
import com.profile.utils.DateUtils;
import com.profile.utils.FontUtils;
import com.profile.utils.GradientManager;
import com.profile.data.Clan;
import com.profile.data.League;
import com.profile.data.LegendSeason;
import com.profile.data.LegendStatistics;
import com.profile.data.Achievement;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileImageRenderer {

    static {
        FontUtils.loadCustomFonts();
    }

    private static final Logger logger = LoggerFactory.getLogger(ProfileImageRenderer.class);

    public static BufferedImage render(Profile profile) throws IOException {
        logger.info(String.format("Generating profile image for %s", profile.tag));
        boolean hasLegendStats = profile.legendStatistics != null && profile.legendStatistics.bestSeason != null;

        int width = 1225;
        int height = hasLegendStats ? 893 : 744;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
                
        RenderingUtility.addRenderingHints(g);

        g.setColor(Color.decode("#E8E8E0"));
        g.fillRect(0, 0, width, height);

        drawNameCardSection(g, profile, 9, 9);
        drawAchievementsSection(g, profile.achievements, 26, hasLegendStats ? 500 : 350);

        if (hasLegendStats) {
            drawLegendLeagueSection(g, profile.legendStatistics, 9, 350);
        }

        g.dispose();
        return image;
    }

    private static void drawNameCardSection(Graphics2D g, Profile profile, int x, int y) {
        int width = 1208;
        int height = 333;
        int radius = 3;
        int paddingTop = 26;
        int paddingLeft = 26;

        Paint gradient = GradientManager.createOptimizedGradient("namecard", x, y, width, height,  
            new Color[] {
                Color.decode("#8c96af"),
                Color.decode("#6b7899")
            },
            new float[] { 0f, 1f },
            false
        );

        g.setPaint(gradient);
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        g.setStroke(new BasicStroke(4));
        g.setColor(new Color(0x6A7798));
        RenderingUtility.drawRoundedRectOutline(g, x, y, width, height, radius);

        drawDividerLine(g, x + paddingLeft + 490, x + paddingLeft + 490, y + paddingTop, y + paddingTop + 245, Color.decode("#5b5f80"), Color.decode("#abaec1"));
        drawDividerLine(g, x + paddingLeft + 805, x + paddingLeft + 805, y + paddingTop, y + paddingTop + 245, Color.decode("#5b5f80"), Color.decode("#abaec1"));

        drawNameSection(g, profile, x + paddingLeft, y + paddingTop + 18);
        drawClanSection(g, profile, x + paddingLeft + 560, y + paddingTop + 35);
        drawTownhallSection(g, profile, x + paddingLeft + 770, y + paddingTop);

        drawSeasonalSection(g, profile, x, y, width, height, radius);
    }

    

    private static void drawDividerLine(Graphics2D g, int x1, int x2, int y1, int y2, Color colour1, Color colour2) {
        g.setStroke(new BasicStroke(3));
        g.setColor(colour1);
        g.drawLine(x1, y1 - 1, x2, y2 - 1);

        g.setStroke(new BasicStroke(1));
        g.setColor(colour2);
        g.drawLine(x1 - 1, y1, x2 - 1, y2);
    }

    public static void drawNameSection(Graphics2D g, Profile profile, int x, int y) {
        String username = profile.name;
        String playerTag = profile.tag;
        String clanRole = profile.role;
        var league = profile.league;
        int trophies = profile.trophies;
        int expLevel = profile.expLevel;

        // Load XP icon image
        BufferedImage xpImage = ImageManager.getCachedImage("xp");
        g.drawImage(xpImage, x, y - 4, 70, 70, null);

        // Draw XP level
        FontUtils.drawClashFont(g, String.valueOf(expLevel), x + 35, y + 32, 32, true, Color.WHITE, 2);

        // Draw username
        FontUtils.drawClashFont(g, username, x + 88, y - 11, 35, false, Color.WHITE, 2);

        // Draw tag
        FontUtils.drawClashFont(g, playerTag, x + 88, y + 33, 26, false, Color.decode("#CCCCCC"), 2);

        // Draw clan role if exists
        if (clanRole != null && !clanRole.isEmpty()) {
            FontUtils.drawClashFont(g, mapClanRole(clanRole), x + 88, y + 67, 26, false, Color.WHITE, 2);
        }

        // Extract rank from legend statistics if present
        Integer rank = null;

        if (profile.legendStatistics != null && profile.legendStatistics.currentSeason != null) {
            rank = profile.legendStatistics.currentSeason.rank;
        }

        // Draw league + trophy info
        drawLeagueTrophyBanner(g, x + 35, y + 105, 123, 123, trophies, league, rank);
    }

    public static String mapClanRole(String clanRole) {
        if (clanRole == null) return "";

        switch (clanRole) {
            case "member":
                return "Member";
            case "admin":
                return "Elder";
            case "coLeader":
                return "Co-leader";
            case "leader":
                return "Leader";
            default:
                return clanRole;
        }
    }

    public static void drawLeagueTrophyBanner(
        Graphics2D g, 
        int x, 
        int y, 
        int emblemWidth, 
        int emblemHeight,
        int trophies, 
        League league, 
        Integer rank
    ) {
        int lineStartFromEmblemX = x + (emblemWidth / 2);
        int lineEndX = x + emblemWidth + 263;
        int emblemCenterY = y + (emblemHeight / 2);
        int line1Y = emblemCenterY - 19;
        int line2Y = emblemCenterY + 18;

        int gradientWidth = 315 + (emblemWidth / 2);
        int gradientHeight = 245;

        // Gradient line 1 (dark fade)
        Paint gradient1 = GradientManager.createOptimizedGradient("leaguetrophybanner", x, y, gradientWidth, gradientHeight,
                new Color[]{new Color(0, 0, 0, 204), new Color(0, 0, 0, 0)}, // 0.8 alpha
                new float[]{0f, 1f},
                true);
        g.setPaint(gradient1);
        g.setStroke(new BasicStroke(32));
        g.drawLine(lineStartFromEmblemX, line1Y, lineEndX, line1Y);

        // Gradient line 2 (purple fade)
        Paint gradient2 = GradientManager.createOptimizedGradient("leaguetrophybanner2", x, y, gradientWidth, gradientHeight,
                new Color[]{
                        new Color(118, 82, 178, 255),
                        new Color(101, 82, 166, 255),
                        new Color(101, 82, 166, 0)
                },
                new float[]{0f, 0.5f, 1f},
                true);
        g.setPaint(gradient2);
        g.setStroke(new BasicStroke(39));
        g.drawLine(lineStartFromEmblemX, line2Y, lineEndX, line2Y);

        // League name text
        String leagueName = getLeagueName(league);

        FontUtils.drawClashFont(g, leagueName, lineStartFromEmblemX + 70, line1Y - 10, 19, false, Color.WHITE, 2);

        // Trophy icon
        BufferedImage trophyIcon = ImageManager.getCachedImage("trophy");
        if (trophyIcon != null) {
            g.drawImage(trophyIcon, lineStartFromEmblemX + 70, line2Y - 16, 32, 32, null);
        }

        // Emblem image
        BufferedImage emblemImage;
        if (league != null && league.iconUrls != null && league.iconUrls.medium != null) {
            emblemImage = ImageManager.loadImageFromURL(league.iconUrls.medium);
        } else {
            emblemImage = ImageManager.getCachedImage("Icon_HV_League_None");
        }

        if (emblemImage != null) {
            g.drawImage(emblemImage, x, y, emblemWidth, emblemHeight, null);
        }

        // Optional Rank Rendering (disabled for now)
        //if (false && rank != null) {
        //    int rankX = x + (emblemWidth / 2);
        //    int rankY = y + (emblemHeight / 2) + 10;
        //    FontUtils.clashFontScaled(g, leagueName, lineEndX, line2Y, gradientWidth, gradientHeight, false);
        //}

        // Trophy count
        FontUtils.drawClashFont(g, String.valueOf(trophies), lineStartFromEmblemX + 109, line2Y - 12, 30, false, Color.WHITE, 2);
    }

    public static String getLeagueName(League league) {
        if (league == null) return "Unranked";
        return league.name;
    }

    private static void drawClanSection(Graphics2D g, Profile profile, int x, int y) {
        Clan clan = profile.clan;
        int clanEmblemWidth = 175;
        int clanEmblemHeight = 175;

        if (clan != null) {
            FontUtils.drawClashFont(g, clan.name, x + (clanEmblemWidth / 2), y, 26, true, Color.WHITE, 2);
            BufferedImage clanEmblemImage = ImageManager.loadImageFromURL(clan.badgeUrls.medium);
            g.drawImage(clanEmblemImage, x, y + 12, clanEmblemWidth, clanEmblemHeight, null);
        } else {
            FontUtils.drawClashFont(g, "No Clan", x + (clanEmblemWidth / 2), y, 26, true, Color.WHITE, 2);
        }
    }

    private static void drawTownhallSection(Graphics2D g, Profile profile, int x, int y) {
        int townhallImageWidth = 214;
        int townhallImageHeight = 214;

        BufferedImage shineImage = ImageManager.getCachedImage("shine");
        BufferedImage townhallImage = ImageManager.getTownhallImage(profile.townHallLevel);

        g.drawImage(shineImage, x + 46, y - 53, townhallImageWidth + 140, townhallImageHeight + 140, null);
        g.drawImage(townhallImage, x + 116, y + 18, townhallImageWidth, townhallImageHeight, null);
    }

    public static void drawSeasonalSection(Graphics2D g, Profile profile, int x, int y, int width, int height, int radius) {
        int purpleHeight = 44;
        int purpleY = y + height - purpleHeight;

        // Draw purple background with rounded bottom corners
        Path2D path = new Path2D.Double();
        path.moveTo(x, purpleY);
        path.lineTo(x + width, purpleY);
        path.lineTo(x + width, purpleY + purpleHeight - radius);
        path.quadTo(x + width, purpleY + purpleHeight, x + width - radius, purpleY + purpleHeight);
        path.lineTo(x + radius, purpleY + purpleHeight);
        path.quadTo(x, purpleY + purpleHeight, x, purpleY + purpleHeight - radius);
        path.lineTo(x, purpleY);
        path.closePath();

        g.setColor(Color.decode("#4e4d79"));
        g.fill(path);

        // Draw top highlight line
        g.setColor(Color.decode("#7964a5"));
        g.fillRect(x, purpleY + 1   , width, 2);

        // Draw seasonal stats
        int troopsDonated = profile.donations;
        int troopsReceived = profile.donationsReceived;
        int attacksWon = profile.attackWins;
        int defensesWon = profile.defenseWins;

        drawSeasonalPixelLine(g, 35, purpleY + 35, 175);
        FontUtils.drawClashFont(g, "Troops donated:", 35, purpleY + 18, 18, false, Color.WHITE, 2);
        seasonalStatBox(g, 219, purpleY + 7, String.valueOf(troopsDonated));

        drawSeasonalPixelLine(g, 315, purpleY + 35, 177);
        FontUtils.drawClashFont(g, "Troops received:", 315, purpleY + 18, 18, false, Color.WHITE, 2);
        seasonalStatBox(g, 499, purpleY + 7, String.valueOf(troopsReceived));

        drawSeasonalPixelLine(g, 635, purpleY + 35, 137);
        FontUtils.drawClashFont(g, "Attacks won:", 635, purpleY + 18, 18, false, Color.WHITE, 2);
        seasonalStatBox(g, 775, purpleY + 7, String.valueOf(attacksWon));

        drawSeasonalPixelLine(g, 915, purpleY + 35, 154);
        FontUtils.drawClashFont(g, "Defenses won:", 915, purpleY + 18, 18, false, Color.WHITE, 2);
        seasonalStatBox(g, 1073, purpleY + 7, String.valueOf(defensesWon));
    }

    private static void drawSeasonalPixelLine(Graphics2D g, int x, int y, int width) {
        // Top line (#2e2e48)
        g.setColor(Color.decode("#2e2e48"));
        g.fillRect(x, y, width, 1);

        // Bottom line (#7a6296)
        g.setColor(Color.decode("#7a6296"));
        g.fillRect(x, y + 1, width, 1);
    }

    private static void seasonalStatBox(Graphics2D g, int x, int y, String message) {
        int width = 88;
        int height = 32;

        g.setColor(Color.decode("#2e2c62"));
        RenderingUtility.drawRoundedRect(g, x, y, width, height, 18);

        FontUtils.drawClashFont(g, message, x + (width / 2), y + (height / 2), 18, true, Color.WHITE, 2);
    }

    public static void drawLegendLeagueSection(Graphics2D g, LegendStatistics legendStats, int x, int y) {
        LegendSeason bestSeason = legendStats.bestSeason;
        LegendSeason previousSeason = legendStats.previousSeason;
        int legendTrophies = legendStats.legendTrophies;

        int width = 1208;
        int height = 140;
        int radius = 3;
        int paddingTop = 18;
        int paddingLeft = 70;

        // Background gradient fill
        Paint gradient = GradientManager.createOptimizedGradient(
            "legendleaguesection", x, y, width, height,
            new Color[] {
                Color.decode("#4d4379"),
                Color.decode("#6f659b")
            },
            new float[] { 0f, 1f },
            false
        );
        g.setPaint(gradient);
        Shape roundedRect = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        g.fill(roundedRect);

        // Inner horizontal stroke
        Paint gradient1 = GradientManager.createOptimizedGradient(
                "legendleaguesection1", x, y, width, height,
                new Color[]{
                    new Color(148, 113, 210, 0),
                    new Color(148, 113, 210, 255),
                    new Color(148, 113, 210, 0)
                },
                new float[]{0f, 0.5f, 1f},
                true
        );
        g.setStroke(new BasicStroke(32));
        g.setPaint(gradient1);
        g.drawLine(x, y + 18, x + width, y + 18);

        // Outer border
        g.setStroke(new BasicStroke(4));
        g.setColor(Color.decode("#493f75"));
        g.draw(roundedRect);

        // Title
        FontUtils.drawClashFont(g, "Legend League Tournament", x + (width / 2), y + 18, 25, true, Color.WHITE, 2);

        // Vertical divider lines
        drawDividerLine(g, x + paddingLeft + 350, x + paddingLeft + 350, y + paddingTop + 26, y + paddingTop + 114, Color.decode("#35304e"), Color.decode("#796fa5"));
        drawDividerLine(g, x + paddingLeft + 761, x + paddingLeft + 761, y + paddingTop + 26, y + paddingTop + 114, Color.decode("#35304e"), Color.decode("#796fa5"));

        // Sections
        drawTrophyLegendarySection(g, bestSeason, x + paddingLeft, y + (paddingTop / 2), "Best");
        drawTrophyLegendarySection(g, previousSeason, x + paddingLeft + 385, y + (paddingTop / 2), "Previous");
        drawLegendTrophySection(g, legendTrophies, x + paddingLeft + 840, y + (paddingTop / 2));
    }

    private static void drawTrophyLegendarySection(Graphics2D g, LegendSeason season, int x, int y, String type) {
        String rank = (season != null && season.rank != null) ? String.valueOf(season.rank) : null;
        Integer trophies = (season != null) ? season.trophies : null;
        String date = (season != null) ? season.id : null;

        if (season != null) {
            BufferedImage legendImage = ImageManager.getCachedImage("Icon_HV_League_Legend");
            if (legendImage != null) {
                g.drawImage(legendImage, x, y + 35, 88, 88, null);
            }

            if (rank != null) {
                FontUtils.clashFontScaled(g, rank, x + 44, y + 77, 60, 63, true);
            }

            FontUtils.drawClashFont(g,type + ": " + DateUtils.formatYearMonth(date), x + 96, y + 44, 18, false, Color.WHITE, 2);

            drawStatBanner(g, x + 96, y + 70, 53, 53, "trophy", trophies != null ? trophies : 0, Color.decode("#242135"));
        } else {
            BufferedImage unrankedImage = ImageManager.getCachedImage("Icon_HV_League_None");
            if (unrankedImage != null) {
                g.drawImage(unrankedImage, x, y + 35, 88, 88, null);
            }

            FontUtils.drawClashFont(g, "Did not place", x + 105, y + 88, 18, false, Color.decode("#dde2ff"), 2);

            FontUtils.drawClashFont(g, type + ": " + DateUtils.formatYearMonth(DateUtils.getLastYearMonth()), x + 96, y + 44, 18, false, Color.WHITE, 2);
        }
    }

    public static void drawLegendTrophySection(Graphics2D g, int legendTrophies, int x, int y) {
        FontUtils.drawClashFont(g, "Legend trophies:", x, y + 44, 18, false, Color.WHITE, 2);
        drawStatBanner(g, x, y + 70, 53, 53, "legendtrophy", legendTrophies, Color.decode("#242135"));
    }

    private static void drawStatBanner(Graphics2D g, int x, int y, int emblemWidth, int emblemHeight, String imageName, int stat, Color statBgColor) {
        int emblemCenterY = y + (emblemHeight / 2);
        int barHeight = 35;
        int barRadius = barHeight / 6;
        int barPadding = 20 + (emblemWidth / 2);
        int iconSize = 21;
        int spacingBetween = 7;

        BufferedImage statImage = ImageManager.getCachedImage(imageName);
        if (statImage == null) return;

        String statText = String.valueOf(stat);

        // Estimate text width using metrics
        
        Font font = new Font("Clash", Font.PLAIN, 25);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(statText);

        int barX = x + (emblemWidth / 2);
        int barY = emblemCenterY - (barHeight / 2);
        int barWidth = barPadding + iconSize + spacingBetween + textWidth + 28;

        // Draw the rounded rectangle background
        g.setColor(statBgColor != null ? statBgColor : Color.decode("#38385c"));
        drawRightRoundedRect(g, barX, barY, barWidth, barHeight, barRadius);


        // Draw emblem image
        g.drawImage(statImage, x, y, emblemWidth, emblemHeight, null);

        int iconX = barX + barPadding;

        // Draw the stat text
        int textX = iconX + iconSize + spacingBetween;
        int textY = emblemCenterY - 11;
        FontUtils.drawClashFont(g, statText, textX, textY, 25, false, Color.WHITE, 2);
    }

    private static void drawRightRoundedRect(Graphics2D g, int x, int y, int width, int height, int radius) {
        int arc = radius * 2;
        Path2D path = new Path2D.Double();
        path.moveTo(x, y);
        path.lineTo(x + width - arc, y);
        path.quadTo(x + width, y, x + width, y + radius);
        path.lineTo(x + width, y + height - radius);
        path.quadTo(x + width, y + height, x + width - arc, y + height);
        path.lineTo(x, y + height);
        path.closePath();
        g.fill(path);
    }

    public static void drawAchievementsSection(Graphics2D g, Achievement[] achievements, int x, int y) {
        Achievement goldLooted = achievements[5];
        Achievement troopDonations = achievements[14];
        Achievement obstaclesRemoved = achievements[3];
        Achievement clanGamePoints = achievements[31];

        Achievement elixirLooted = achievements[6];
        Achievement spellDonations = achievements[23];
        Achievement seasonChallengePts = achievements[35];
        Achievement warStars = achievements[20];
        Achievement successfulAttacks = achievements[12];

        Achievement darkElixirLooted = achievements[16];
        Achievement siegeDonations = achievements[40];
        Achievement campaignMapStars = achievements[1];
        Achievement clanWarLeagueStars = achievements[33];
        Achievement successfulDefenses = achievements[13];

        // First column
        drawAchievementCell(g, x, y, "Gold looted", "gold", goldLooted);
        drawAchievementCell(g, x, y + 79, "Troop donations", "troopdonation", troopDonations);
        drawAchievementCell(g, x, y + 158, "Obstacles removed", "obstaclesremoved", obstaclesRemoved);
        drawAchievementCell(g, x, y + 236, "Clan games points", "clangames", clanGamePoints);

        // Second column
        drawAchievementCell(g, x + 394, y, "Elixir looted", "elixir", elixirLooted);
        drawAchievementCell(g, x + 394, y + 79, "Spell donations", "spelldonation", spellDonations);
        drawAchievementCell(g, x + 394, y + 158, "Season challenge pts", "goldpass", seasonChallengePts);
        drawAchievementCell(g, x + 394, y + 236, "War stars", "warstar", warStars);
        drawAchievementCell(g, x + 394, y + 315, "Successful attacks", "multiplayerattack", successfulAttacks);

        // Third column
        drawAchievementCell(g, x + 788, y, "Dark elixir looted", "darkelixir", darkElixirLooted);
        drawAchievementCell(g, x + 788, y + 79, "Siege donations", "siegemachinedonation", siegeDonations);
        drawAchievementCell(g, x + 788, y + 158, "Campaign map stars", "campaigner", campaignMapStars);
        drawAchievementCell(g, x + 788, y + 236, "Clan war league stars", "cwlstar", clanWarLeagueStars);
        drawAchievementCell(g, x + 788, y + 315, "Successful defenses", "shield", successfulDefenses);

        ImageManager.drawSignature(g, x + 35, y + 298, 3);
    }

    public static void drawAchievementCell(Graphics2D g, int x, int y, String achievementTitle, String achievementIcon, Achievement achievement) {
        int width = 385;
        int height = 70;
        int radius = 18;

        // Draw rounded rectangle path
        RenderingUtility.drawRoundedRect(g, x, y, width, height, radius);

        // Create gradient fill
        Paint gradient = GradientManager.createOptimizedGradient(
            "achievementcell", x, y, width, height,
            new Color[] {
                Color.decode("#a8adb0"),
                Color.decode("#9ca5b0")
            },
            new float[] {0f, 1f},
            false 
        );

        g.setPaint(gradient);
        g.fill(new RoundRectangle2D.Float(x, y, width, height, radius, radius));

        // White border
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.WHITE);
        g.draw(new RoundRectangle2D.Float(x, y, width, height, radius, radius));

        // Reflection effect
        drawReflection(g, x + 9, y + 9, width - 18, (height / 2) - 9);

        // Draw the achievement banner inside the cell
        drawAchievementBanner(g, x, y, height, width, achievementTitle, achievementIcon, achievement);
    }

    public static void drawReflection(Graphics2D g, int x, int y, int width, int height) {
        int radius = 14;

        // Draw rounded rect path
        Shape roundedRect = new RoundRectangle2D.Float(x, y, width, height, radius, radius);

        // Create gradient fill
        Paint gradient = GradientManager.createOptimizedGradient(
            "reflection", x, y, width, height,
            new Color[]{
                new Color(255, 255, 255, (int)(0.25 * 255)),
                new Color(255, 255, 255, (int)(0.10 * 255)) 
            },
            new float[]{0f, 1f},
            false
        );

        g.setPaint(gradient);
        g.fill(roundedRect);
    }

    public static void drawAchievementBanner(
        Graphics2D g, int x, int y, int cellHeight, int cellWidth,
        String achievementTitle, String achievementIcon, Achievement achievement
    ) {
        int achievementStars = achievement.stars;
        int achievementValue = achievement.value;

        int achievementIconWidth = 53;
        int achievementIconHeight = 53;
        int barHeight = 28;
        int barRadius = barHeight / 5;
        int barPadding = (achievementIconWidth / 2) + 7;
        int spacingBetween = 7;

        int starsWidth = 101;
        int starsHeight = 40;
        int starsX = x + 11;
        int starsY = y + (cellHeight / 2) - (starsHeight / 2);

        int xFromStatIcon = x + starsWidth + 25;

        BufferedImage achievementIconImage = ImageManager.getCachedImage(achievementIcon);
        BufferedImage starsImage = ImageManager.getAchievementStarsImage(achievementStars);

        int iconY = y + (cellHeight / 2) - (achievementIconHeight / 2) + 4;
        int iconCenterY = iconY + (achievementIconHeight / 2) + 4;

        int barX = xFromStatIcon + (achievementIconWidth / 2);
        int barY = iconCenterY - (barHeight / 2);

        String text = FontUtils.formatNumberWithSpaces(achievementValue);
        Font font = new Font("Clash", Font.PLAIN, 21);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);

        int barWidth = Math.max(
                barPadding + spacingBetween + textWidth + 7,
                cellWidth - starsWidth - (achievementIconWidth / 2) - 105
        );

        g.setColor(Color.decode("#38385c"));
        Shape roundedRect = new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, barRadius * 2, barRadius * 2);
        g.fill(roundedRect);

        int iconX = barX + barPadding;
        g.drawImage(achievementIconImage, xFromStatIcon, iconY, achievementIconWidth, achievementIconHeight, null);
        g.drawImage(starsImage, starsX, starsY, starsWidth, starsHeight, null);

        int textX = iconX + spacingBetween;
        int textY = iconCenterY - 11;
        FontUtils.drawClashFont(g, achievementTitle, iconX, textY - 19, 14, false, Color.WHITE, 2);
        FontUtils.drawClashFont(g, text, textX, textY, 21, false, Color.WHITE, 2);
    }
}
