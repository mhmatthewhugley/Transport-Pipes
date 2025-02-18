package de.robotricker.transportpipes.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LangConf extends Conf {

    private static LangConf langConf;

    public LangConf(Plugin configPlugin, String language) {
        super(configPlugin, "lang_" + language.toLowerCase(Locale.ENGLISH) + ".yml", "lang.yml", true);
        langConf = this;
    }

    /**
     * Represents the language keys from the language config
     */
    public enum Key {
        COLORS_BLACK("colors.black"),
        COLORS_BLUE("colors.blue"),
        COLORS_GREEN("colors.green"),
        COLORS_RED("colors.red"),
        COLORS_WHITE("colors.white"),
        COLORS_YELLOW("colors.yellow"),
        CONNECTION_BLOCKED("block_connection.block"),
        CONNECTION_UNBLOCKED("block_connection.unblock"),
        DIRECTIONS_DOWN("directions.down"),
        DIRECTIONS_EAST("directions.east"),
        DIRECTIONS_NONE("directions.none"),
        DIRECTIONS_NORTH("directions.north"),
        DIRECTIONS_SOUTH("directions.south"),
        DIRECTIONS_UP("directions.up"),
        DIRECTIONS_WEST("directions.west"),
        DUCT_INVENTORY_CRAFTINGPIPE_OUTPUTDIRECTION("duct_inventory.craftingpipe.outputdirection"),
        DUCT_INVENTORY_CRAFTINGPIPE_RETRIEVECACHEDITEMS("duct_inventory.craftingpipe.retrievecacheditems"),
        DUCT_INVENTORY_CREATIVE_INVENTORY_TITLE("duct_inventory.creative_inventory_title"),
        DUCT_INVENTORY_EXTRACTIONPIPE_EXTRACTAMOUNT("duct_inventory.extractionpipe.extractamount"),
        DUCT_INVENTORY_EXTRACTIONPIPE_EXTRACTCONDITION("duct_inventory.extractionpipe.extractcondition"),
        DUCT_INVENTORY_EXTRACTIONPIPE_EXTRACTDIRECTION("duct_inventory.extractionpipe.extractdirection"),
        DUCT_INVENTORY_EXTRACTIONPIPE_EXTRACTMODE("duct_inventory.extractionpipe.extractmode"),
        DUCT_INVENTORY_EXTRACTIONPIPE_FILTERTITLE("duct_inventory.extractionpipe.filtertitle"),
        DUCT_INVENTORY_FILTER_MODE_AND_STRICTNESS("duct_inventory.filter_mode_and_strictness"),
        DUCT_INVENTORY_GOLDENPIPE_FILTERTITLE("duct_inventory.goldenpipe.filtertitle"),
        DUCT_INVENTORY_LEFTARROW("duct_inventory.leftarrow"),
        DUCT_INVENTORY_RIGHTARROW("duct_inventory.rightarrow"),
        DUCT_INVENTORY_TITLE("duct_inventory.title"),
        EXTRACT_AMOUNT_EXTRACT_1("extract_amount.extract_1"),
        EXTRACT_AMOUNT_EXTRACT_16("extract_amount.extract_16"),
        EXTRACT_CONDITION_ALWAYS_EXTRACT("extract_condition.always_extract"),
        EXTRACT_CONDITION_NEEDS_REDSTONE("extract_condition.needs_redstone"),
        EXTRACT_CONDITION_NEVER_EXTRACT("extract_condition.never_extract"),
        EXTRACT_MODE_DIRECT("extract_mode.direct"),
        EXTRACT_MODE_ROUND("extract_mode.round"),
        FILTER_MODE_BLOCKALL("filter_mode.blockall"),
        FILTER_MODE_INVERTED("filter_mode.inverted"),
        FILTER_MODE_NORMAL("filter_mode.normal"),
        FILTER_STRICTNESS_MATERIAL("filter_strictness.material"),
        FILTER_STRICTNESS_MATERIAL_METADATA("filter_strictness.material_metadata"),
        PIPES_BLACK("pipes.black"),
        PIPES_BLUE("pipes.blue"),
        PIPES_CRAFTING("pipes.crafting"),
        PIPES_EXTRACTION("pipes.extraction"),
        PIPES_GOLDEN("pipes.golden"),
        PIPES_GREEN("pipes.green"),
        PIPES_ICE("pipes.ice"),
        PIPES_IRON("pipes.iron"),
        PIPES_RED("pipes.red"),
        PIPES_VOID("pipes.void"),
        PIPES_WHITE("pipes.white"),
        PIPES_YELLOW("pipes.yellow"),
        PLAYER_SETTINGS_DECREASE_RENDERDISTANCE("player_settings.decrease_renderdistance"),
        PLAYER_SETTINGS_INCREASE_RENDERDISTANCE("player_settings.increase_renderdistance"),
        PLAYER_SETTINGS_ITEM_VISIBILITY_HIDE("player_settings.item_visibility_hide"),
        PLAYER_SETTINGS_ITEM_VISIBILITY_SHOW("player_settings.item_visibility_show"),
        PLAYER_SETTINGS_RENDERDISTANCE("player_settings.renderdistance"),
        PLAYER_SETTINGS_RENDERSYSTEM("player_settings.rendersystem"),
        PLAYER_SETTINGS_TITLE("player_settings.title"),
        PROTECTED_BLOCK("protected_block"),
        RENDERSYSTEM_BLOCK("rendersystem_block"),
        RENDERSYSTEM_NAME_MODELLED("rendersystem_name.modelled"),
        RENDERSYSTEM_NAME_VANILLA("rendersystem_name.vanilla"),
        RESOURCEPACK_FAIL("resourcepack_fail"),
        SHOW_HIDDEN_DUCTS("show_hidden_ducts"),
        WRENCH("wrench");

        private final String key;

        Key(String key) {
            this.key = key;
        }

        public String get(Object... replacements) {
            String value = (String) langConf.read(key);
            if (value == null) {
                value = "&cMissing Language Entry: &4" + key + "&r";
            }
            for (int i = 0; i < replacements.length; i++) {
                value = value.replaceAll("%" + (i + 1) + "%", replacements[i].toString());
            }
            return ChatColor.translateAlternateColorCodes('&', value);
        }

        public List<String> getLines(Object... replacements) {
            return Arrays.asList(get(replacements).split("\\\\n"));
        }

        public void sendMessage(Player p, Object... replacements) {
            for (String line : getLines(replacements)) {
                p.sendMessage(line);
            }
        }

    }

}
