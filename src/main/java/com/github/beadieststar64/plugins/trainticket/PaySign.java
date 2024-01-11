package com.github.beadieststar64.plugins.trainticket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaySign implements Listener {

    private final Plugin plugin;

    public PaySign(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(SignChangeEvent event) {
        Player player = event.getPlayer();
        String line1 = event.getLine(0);
        String line2 = event.getLine(1);
        String line3 = event.getLine(2);

        if(line1 == null) {
            return;
        }
        if(line1.equals("TrainTicket")) {
            if(line2 == null || line2.isEmpty()) {
                player.sendMessage(ChatColor.RED + "2行目に駅名又はICを記入してください!");
                event.setCancelled(true);
                return;
            }else{
                if(line2.equals("IC")) {
                    //ICカード購入モード
                    placeIC(event);
                }else{
                    event.setLine(1, line2 + "駅");
                }
            }
            if(line3 == null || line3.isEmpty()) {
                player.sendMessage(ChatColor.RED + "3行目にbuyかsell <金額>を記入してください!");
                event.setCancelled(true);
                return;
            }else if(line3.equals("buy")) {
                event.setLine(2, ChatColor.AQUA + String.valueOf(ChatColor.UNDERLINE) + String.valueOf(ChatColor.BOLD) + "-切符購入-");
            }else if(line3.contains("sell")) {
                String[] split = line3.split(" ");
                if(split.length == 1 || split.length > 2) {
                    player.sendMessage(ChatColor.RED + "sell <金額>と入力してください!");
                    event.setCancelled(true);
                    return;
                }
                event.setLine(2, ChatColor.RED + String.valueOf(ChatColor.UNDERLINE) + String.valueOf(ChatColor.BOLD) + "-料金 " +
                        split[1] +
                        "-");
            }

            event.setLine(3, player.getName());
        }
    }

    private void buyIC(String line2, Player player) {
        try {
            line2 = line2.replaceAll("§f", "");
            int balance = Integer.parseInt(line2);
            TrainItems items = new TrainItems();
            items.setIc(balance, player.getName());
            player.getInventory().addItem(TrainItems.getIc());
            player.sendMessage(ChatColor.BOLD + "[" +
                    ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                    ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                    ChatColor.WHITE + "ICに" + balance + "チャージしました");
        }catch(Exception e) {
            player.sendMessage(ChatColor.RED + "エラーが発生しました!");
        }
    }

    private void placeIC(SignChangeEvent event) {
        Player player = event.getPlayer();
        String line3 = event.getLine(2);

        if(line3 == null || line3.isEmpty()) {
            player.sendMessage(ChatColor.RED + "チャージ金額を記入してください!");
            return;
        }
        try {
            int check = Integer.parseInt(line3);
        }catch(Exception e) {
            player.sendMessage(ChatColor.RED + "チャージ金額は半角数字にしてください!");
            return;
        }
        event.setLine(1, ChatColor.GREEN + "チャージ金額: " + ChatColor.WHITE + line3);
        event.setLine(2, ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + String.valueOf(ChatColor.UNDERLINE) + "-ICカードを購入-");
        event.setLine(3, player.getName());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        PlayerFlag pf = new PlayerFlag(player, plugin);
        if(pf.get()) {
            return;
        }
        pf.set();
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(event.getClickedBlock().getState() instanceof Sign) {
                Sign signboard = (Sign) event.getClickedBlock().getState();
                if(!signboard.getLine(0).equals("TrainTicket")) {
                    return;
                }

                plugin.getLogger().info(Arrays.toString(signboard.getLines()));

                if(signboard.getLine(2).equals("§b§l§n-切符購入-")) {
                    TrainItems items = new TrainItems();
                    items.setTicket("JR", signboard.getLine(1), player.getName());
                    player.getInventory().addItem(TrainItems.getTicket());
                    player.sendMessage(ChatColor.BOLD + "[" +
                            ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                            ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                            ChatColor.WHITE + "切符を購入しました。なくした場合、出口の料金の2倍の額が請求されます。");

                }else if(signboard.getLine(2).contains("§c§l§n-料金")) {
                    if(player.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                        if(TrainItems.getTicket() == null) {
                            return;
                        }
                        if(!player.getInventory().getItemInMainHand().equals(TrainItems.getTicket())) {
                            player.sendMessage(ChatColor.BOLD + "[" +
                                    ChatColor.RED + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                                    ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                                    ChatColor.WHITE + "チケットをメインハンドに持ってください!!");
                            return;
                        }
                        player.getInventory().remove(TrainItems.getTicket());
                        player.sendMessage(ChatColor.BOLD + "[" +
                                ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                                ChatColor.WHITE + "料金は" + signboard.getLine(2).split(" ")[1].replaceAll("-", "") + "Gです。");
                    }else if(player.getInventory().getItemInMainHand().getType().equals(Material.BOOK)) {
                        if(TrainItems.getIc() == null) {
                            return;
                        }
                        if(!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§b§l交通系ICカード")) {
                            return;
                        }
                        if(player.getInventory().getItemInMainHand().getItemMeta().getLore() == null) {
                            return;
                        }
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if(meta.getLore() == null) {
                            return;
                        }
                        String pay = meta.getLore().get(0).split(" ")[1].replaceAll("§f", "");

                        plugin.getLogger().info(pay);

                        int balance = Integer.parseInt(pay);
                        if((balance - Integer.parseInt(signboard.getLine(2).split(" ")[1].replaceAll("-", ""))) < 0) {
                            player.sendMessage(ChatColor.BOLD + "[" +
                                    ChatColor.RED + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                                    ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                                    ChatColor.WHITE + "残高が足りません!");
                            return;
                        }
                        List<String> newLore = new ArrayList<>(Arrays.asList(
                                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "残高: " + ChatColor.WHITE + (balance - Integer.parseInt(signboard.getLine(2).split(" ")[1].replaceAll("-", ""))),
                                meta.getLore().get(1)
                        ));
                        meta.setLore(newLore);
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                        player.updateInventory();
                        player.sendMessage(ChatColor.BOLD + "[" +
                                ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                                ChatColor.WHITE + "料金は" + signboard.getLine(2).split(" ")[1].replaceAll("-", "") + "Gです。");
                    }else{
                        player.sendMessage(ChatColor.BOLD + "[" +
                                ChatColor.RED + String.valueOf(ChatColor.BOLD) + "TrainTicket" +
                                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "]" +
                                ChatColor.WHITE + "チケット又はICカードをメインハンドに持ってください!!");
                    }
                }else if(signboard.getLine(2).equals("§6§l§n-ICカードを購入-")) {
                    buyIC(signboard.getLine(1).split(" ")[1], player);
                }
            }
        }
    }

    @EventHandler
    public void onOpenSign(PlayerSignOpenEvent event) {
        if(event.getSign().getLine(0).equals("TrainTicket")) {
            String writtenMCID = event.getSign().getLine(3);
            if(!writtenMCID.equals(event.getPlayer().getName())) {
                event.setCancelled(true);
            }else{
                if(!event.getPlayer().isSneaking()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
