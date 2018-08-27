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

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This file is part of GroundItem
 */
public class JsonItemStack
{
    //https://github.com/Gnat008/PerWorldInventory/blob/master/src/main/java/me/gnat008/perworldinventory/data/serializers/ItemSerializer.java

    private final JavaPlugin plugin;
    private final ItemStack itemStack;

    public JsonItemStack(final JavaPlugin plugin, final ItemStack itemStack)
    {
        Validate.notNull(itemStack, "itemStack cannot be null");

        this.plugin = plugin;
        this.itemStack = itemStack;
    }

    public final String toJson()
    {
        final JSONObject json = new JSONObject();

        ///*
        // * Check to see if the item is a skull with a null owner.
        // * This is because some people are getting skulls with null owners, which causes Spigot to throw an error
        // * when it tries to serialize the item. If this ever gets fixed in Spigot, this will be removed.
        // */
        //if (this.itemStack.getType() == Material.SKULL_ITEM) {
        //    SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
        //    if (meta.hasOwner() && (meta.getOwner() == null || meta.getOwner().isEmpty())) {
        //        this.itemStack.setItemMeta(this.plugin.getServer().getItemFactory().getItemMeta(Material.SKULL_ITEM));
        //    }
        //}

        ByteArrayOutputStream outputStream;
        BukkitObjectOutputStream dataObject;
        try {
            outputStream = new ByteArrayOutputStream();
            dataObject = new BukkitObjectOutputStream(outputStream);
            dataObject.writeObject(this.itemStack);
            dataObject.close();

            json.put("item", Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException ex) {
            plugin.getLogger().severe("Error saving an item:");
            plugin.getLogger().severe("Item: " + this.itemStack.getType().toString());
            plugin.getLogger().severe("Reason: " + ex.getMessage());
            return null;
        }

        return json.toJSONString();
    }

    public static final JsonItemStack fromJson(final JavaPlugin plugin, final String jsonItemStack)
    {
        final JSONObject data = (JSONObject) JSONValue.parse(jsonItemStack);

        try
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String)data.get("item")));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            return new JsonItemStack(plugin, (ItemStack) dataInput.readObject());
        }
        catch(ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }

        return null;


        /*catch(IOException | ClassNotFoundException ex){
            plugin.getLogger().severe("Error loading an item:" + ex.getMessage());
            return new ItemStack(Material.AIR);
        }*/

        /*final JSONObject array = (JSONObject) JSONValue.parse(jsonItemStack);
        return new JsonItemStack((String)array.get("type"),
                (short)(long)array.get("damage"),
                (String)array.get("name"),
                (List<String>)array.get("lore"),
                (HashMap<String, Long>)array.get("enchantments"),
                (Long)array.get("amount"));*/
    }

    public final ItemStack toItemStack()
    {

        /*
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

        itemStack.setItemMeta(meta);*/

        return itemStack;
    }

}


