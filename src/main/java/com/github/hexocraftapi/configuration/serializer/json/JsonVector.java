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
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This file is part of GroundItem
 */
public class JsonVector
{
    private final double x;
    private final double y;
    private final double z;

    public JsonVector(final Vector vector)
    {
        Validate.notNull(vector, "vector cannot be null");

        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    public JsonVector(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final String toJson()
    {
        final JSONObject object = new JSONObject();
        object.put("x", this.x);
        object.put("y", this.y);
        object.put("z", this.z);
        return object.toJSONString();
    }

    public static final JsonVector fromJson(final String jsonLocation)
    {
        final JSONObject array = (JSONObject) JSONValue.parse(jsonLocation);
        return new JsonVector( (double) array.get("x"), (double) array.get("y"), (double) array.get("z"));
    }

    public final Vector toVector()
    {
        final Vector vector = new Vector(this.x, this.y, this.z);
        return vector;
    }
}


