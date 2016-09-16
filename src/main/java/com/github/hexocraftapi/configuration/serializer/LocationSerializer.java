package com.github.hexocraftapi.configuration.serializer;

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

import com.github.hexocraftapi.configuration.Configuration;
import com.github.hexocraftapi.configuration.serializer.json.JsonLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class LocationSerializer implements Serializer<Location>
{
	private LocationSerializer() {}
	private static LocationSerializer t = new LocationSerializer();
	public static LocationSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final Location location)
	{
		Validate.notNull(location, "location cannot be null");

		final JsonLocation jsonLocation = new JsonLocation(location);
		return jsonLocation.toJson();
	}

	@Override
	public Location deserialize(Configuration configuration, Class<? extends Location> oClass, final Class<?>[] pClass, final Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Validate.notNull(object, "object cannot be null");

		final JsonLocation jsonLocation = JsonLocation.fromJson(object.toString());
		return jsonLocation.toLocation();
	}
}
