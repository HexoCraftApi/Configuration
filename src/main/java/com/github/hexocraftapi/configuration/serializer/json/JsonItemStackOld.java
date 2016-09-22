package com.github.hexocraftapi.configuration.serializer.json;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of GroundItem
 */
public class JsonItemStackOld
{
    //https://github.com/KingFaris10/KingKits/blob/master/src/main/java/com/faris/kingkits/helper/util/ItemUtilities.java

    private final String type;
    private final short damage;
    private final String name;
    private final List<String> lore;
    private final HashMap<String, Long> enchantments = new HashMap<String, Long>();
    private final long amount;

    public JsonItemStackOld(final ItemStack itemStack)
    {
        this.type = itemStack.getType().name();
        this.damage = itemStack.getDurability()!=0?Short.valueOf(itemStack.getDurability()):0;
        final ItemMeta meta = itemStack.getItemMeta();
        this.name = meta.getDisplayName();
        this.lore = meta.getLore();
        for(final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            this.enchantments.put(entry.getKey().getName(), Long.valueOf(entry.getValue()));
        }
        this.amount = Long.valueOf(itemStack.getAmount());
    }

    public JsonItemStackOld(final String type, short damage, final String name, final List<String> lore, final HashMap<String, Long> enchantments, final Long amount)
    {
        this.type = type;
        this.damage = damage;
        this.name = name;
        this.lore = lore;
        if(enchantments != null) {
            this.enchantments.putAll(enchantments);
        }
        this.amount = amount == null ? 1L : amount;
    }

    public final String toJson()
    {
        final JSONObject object = new JSONObject();
        object.put("type", this.type);
        object.put("damage", this.damage);
        if(this.name!=null) object.put("name", this.name);
        if(this.lore!=null) object.put("lore", this.lore);
        if(this.enchantments!=null && this.enchantments.size()>0)  object.put("enchantments", this.enchantments);
        object.put("amount", this.amount);
        return object.toJSONString();
    }

    public static final JsonItemStack fromJson(final String jsonItemStack)
    {
        /*final JSONObject array = (JSONObject) JSONValue.parse(jsonItemStack);
        return new JsonItemStack((String)array.get("type"),
                (short)(long)array.get("damage"),
                (String)array.get("name"),
                (List<String>)array.get("lore"),
                (HashMap<String, Long>)array.get("enchantments"),
                (Long)array.get("amount"));*/

        return null;
    }

    public final ItemStack toItemStack()
    {
        final ItemStack itemStack = new ItemStack(type == null ? Material.GRASS : Material.valueOf(type), Ints.checkedCast(amount), Shorts.checkedCast(damage));
        final ItemMeta meta = itemStack.getItemMeta();

        if(this.name != null)
            meta.setDisplayName(this.name);

        if(this.lore != null)
            meta.setLore(this.lore);

        if(this.enchantments.size() != 0)
        {
            for(final Map.Entry<String, Long> entry : this.enchantments.entrySet()) {
                meta.addEnchant(Enchantment.getByName(entry.getKey()), Ints.checkedCast(entry.getValue()), true);
            }
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

}


