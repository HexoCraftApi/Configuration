package com.github.hexocraftapi.configuration.serializer.json;

/*
 * Copyright 2015 hexosse
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This file is part of GroundItem
 */
public class JsonChunck
{

    private final String world;
    private final int x;
    private final int z;

    public JsonChunck(final Chunk chunk)
    {
        this.world = chunk.getWorld().getName();
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public JsonChunck(final String world, final int x, final int z)
    {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public final String toJson()
    {
        final JSONObject object = new JSONObject();
        object.put("world", this.world);
        object.put("x", this.x);
        object.put("z", this.z);
        return object.toJSONString();
    }

    public static final JsonChunck fromJson(final String jsonLocation)
    {
        final JSONObject array = (JSONObject) JSONValue.parse(jsonLocation);
        return new JsonChunck((String) array.get("world"), (int) array.get("x"), (int) array.get("z"));
    }

    public final Chunk toChunck()
    {
        final World world = Bukkit.getWorld(this.world);
        if(world==null)
            throw new IllegalStateException("World not loaded");

        return world.getChunkAt(this.x, this.z);
    }
}


