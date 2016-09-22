package com.github.hexocraftapi.configuration.serializer;

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

import com.github.hexocraftapi.configuration.Configuration;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class MapSerializer implements Serializer<Map<?, ?>>
{
	private MapSerializer() {}
	private static MapSerializer t = new MapSerializer();
	public static MapSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final Map<?, ?> object)
	{
		Validate.notNull(object, "object cannot be null");

		String tempSection = "temp_section";
		final ConfigurationSection section = configuration.getYamlConfiguration().createSection(tempSection);
		for(final Map.Entry<?, ?> entry : object.entrySet())
			section.set((String)configuration.serialize(configuration, entry.getKey().toString()), configuration.serialize(configuration, entry.getValue()));
		configuration.getYamlConfiguration().set(tempSection, null);
		return section;
	}

	@Override
	public Map<?,?> deserialize(Configuration configuration, Class<? extends Map<?,?>> oClass, final Class<?>[] pClass, final Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Validate.notNull(object, "object cannot be null");

		final ConfigurationSection section = (ConfigurationSection)object;
		final Map<Object, Object> deserializedMap = new HashMap<Object, Object>();

		for(final String key : section.getKeys(false))
		{
			final Object value = section.get(key);
			deserializedMap.put(
			configuration.deserialize(configuration, pClass[0], null, key)
				, configuration.deserialize(configuration, pClass[1], null, value));
		}
		final Map<?,?> map = oClass.newInstance();
		oClass.getMethod("putAll", Map.class).invoke(map, deserializedMap);
		return map;
	}
}
