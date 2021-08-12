package com.willfp.reforges.gui;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.reforges.ReforgesPlugin;
import com.willfp.reforges.reforges.meta.ReforgeTarget;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class ReforgeGUI {
    /**
     * The reforge GUI.
     */
    @Getter
    private static Menu menu;

    /**
     * Update the GUI.
     *
     * @param plugin The plugin.
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    @ConfigUpdater
    public static void update(@NotNull final EcoPlugin plugin) {
        menu = Menu.builder(5)
                .setTitle("Reforge Item")
                .setMask(
                        new FillerMask(
                                Material.BLACK_STAINED_GLASS_PANE,
                                "011111110",
                                "011101110",
                                "011101110",
                                "011111110",
                                "011101110"
                        )
                ).modfiy(builder -> {
                    Slot slot = Slot.builder(
                            new ItemStackBuilder(Material.RED_STAINED_GLASS_PANE)
                                    .setDisplayName("&r")
                                    .build()
                    ).setModifier((player, menu, previous) -> {
                        if (menu.getCaptiveItems(player).isEmpty()) {
                            previous.setType(Material.RED_STAINED_GLASS_PANE);
                        } else {
                            previous.setType(Material.LIME_STAINED_GLASS_PANE);
                        }
                    }).build();

                    for (int i = 1; i <= 5; i++) {
                        builder.setSlot(i, 1, slot);
                        builder.setSlot(i, 9, slot);
                    }
                }).setSlot(2, 5,
                        Slot.builder()
                                .setCaptive()
                                .build()
                ).setSlot(3, 5,
                        Slot.builder(new ItemStack(Material.ANVIL))
                                .setModifier((player, menu, previous) -> {
                                    ItemMeta meta = previous.getItemMeta();
                                    if (meta == null) {
                                        return;
                                    }

                                    ItemStack toReforge = menu.getCaptiveItems(player).isEmpty() ? null : menu.getCaptiveItems(player).get(0);
                                    ReforgeStatus status = null;

                                    ReforgeTarget target = null;

                                    if (toReforge == null) {
                                        status = ReforgeStatus.NO_ITEM;
                                    } else {
                                        target = ReforgeTarget.getForMaterial(toReforge.getType());
                                        if (target == null) {
                                            status = ReforgeStatus.INVALID_ITEM;
                                        }
                                    }

                                    if (target != null) {
                                        status = ReforgeStatus.ALLOW;
                                    }

                                    switch (status) {
                                        case INVALID_ITEM -> {
                                            previous.setType(Objects.requireNonNull(Material.getMaterial(plugin.getConfigYml().getString("gui.invalid-item.material").toUpperCase())));
                                            meta.setDisplayName(plugin.getConfigYml().getString("gui.invalid-item.name"));
                                            meta.setLore(plugin.getConfigYml().getStrings("gui.invalid-item.lore"));
                                        }
                                        case ALLOW -> {
                                            previous.setType(Objects.requireNonNull(Material.getMaterial(plugin.getConfigYml().getString("gui.allow.material").toUpperCase())));
                                            meta.setDisplayName(plugin.getConfigYml().getString("gui.allow.name"));
                                            meta.setLore(plugin.getConfigYml().getStrings("gui.allow.lore"));
                                        }
                                        default -> {
                                            previous.setType(Objects.requireNonNull(Material.getMaterial(plugin.getConfigYml().getString("gui.no-item.material").toUpperCase())));
                                            meta.setDisplayName(plugin.getConfigYml().getString("gui.no-item.name"));
                                            meta.setLore(plugin.getConfigYml().getStrings("gui.no-item.lore"));
                                        }
                                    }

                                    previous.setItemMeta(meta);
                                })
                                .onLeftClick((event, slot, menu) -> {
                                    Player player = (Player) event.getWhoClicked();
                                    ItemStack toReforge = menu.getCaptiveItems(player).isEmpty() ? null : menu.getCaptiveItems(player).get(0);
                                    if (toReforge == null) {
                                        return;
                                    }



                                    player.sendMessage("reforged");
                                }).build()
                )
                .setSlot(5, 5,
                        Slot.builder(
                                new ItemStackBuilder(Material.BARRIER)
                                        .setDisplayName("&cClose")
                                        .build()
                        ).onLeftClick((event, slot) -> {
                            event.getWhoClicked().closeInventory();
                        }).build()
                )
                .build();
    }

    static {
        update(ReforgesPlugin.getInstance());
    }
}
