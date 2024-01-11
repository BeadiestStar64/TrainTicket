package com.github.beadieststar64.plugins.trainticket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainItems {

    private static ItemStack ticket;
    private static ItemStack ic;

    public static ItemStack getTicket() {return ticket;}
    public static ItemStack getIc() {return ic;}

    public void setTicket(String trainCompany, String startStation, String mcid) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "鉄道切符");
        meta.setLore(new ArrayList<>(Arrays.asList(
                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "鉄道会社: " + ChatColor.WHITE + trainCompany,
                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "乗り始め駅: " + ChatColor.WHITE + startStation,
                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "持ち主: " + ChatColor.WHITE + mcid)
        ));
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        item.setItemMeta(meta);
        ticket = item;
    }

    public void setIc(int balance, String mcid) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "交通系ICカード");
        meta.setLore(new ArrayList<>(Arrays.asList(
                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "残高: " + ChatColor.WHITE + balance,
                ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "持ち主: " + ChatColor.WHITE + mcid
        )));
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        item.setItemMeta(meta);
        ic = item;
    }
}
