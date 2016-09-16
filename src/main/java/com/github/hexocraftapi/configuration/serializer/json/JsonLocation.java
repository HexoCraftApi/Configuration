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

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This file is part of GroundItem
 */
public class JsonLocation
{

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public JsonLocation(final Location location)
    {
        Validate.notNull(location, "location cannot be null");

        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public JsonLocation(final String world, final double x, final double y, final double z)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
    }

    public JsonLocation(final String world, final double x, final double y, final double z, final float pitch, final float yaw)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public final String toJson()
    {
        final JSONObject object = new JSONObject();
        object.put("world", this.world);
        object.put("x", this.x);
        object.put("y", this.y);
        object.put("z", this.z);
        object.put("pitch", this.pitch);
        object.put("yaw", this.yaw);
        return object.toJSONString();
    }

    public static final JsonLocation fromJson(final String jsonLocation)
    {
        final JSONObject array = (JSONObject) JSONValue.parse(jsonLocation);
        return new JsonLocation((String) array.get("world"),
                (double) array.get("x"),
                (double) array.get("y"),
                (double) array.get("z"),
                (float)(double) array.get("pitch"),
                (float)(double) array.get("yaw"));
    }

    public final Location toLocation()
    {
        final World world = Bukkit.getWorld(this.world);
        if(world==null)
            throw new IllegalStateException("World not loaded");

        final Location location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
        return location;
    }
}


