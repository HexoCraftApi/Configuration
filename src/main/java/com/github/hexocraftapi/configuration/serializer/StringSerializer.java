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
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class StringSerializer implements Serializer<String>
{
	private StringSerializer() {}
	private static StringSerializer t = new StringSerializer();
	public static StringSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final String object)
	{
		Validate.notNull(object, "object cannot be null");

		return object.replace(ChatColor.COLOR_CHAR, '&');
	}

	@Override
	public String deserialize(Configuration configuration, Class<? extends String> oClass, final Class<?>[] pClass, Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Validate.notNull(object, "object cannot be null");

		return ChatColor.translateAlternateColorCodes('&', object.toString());
	}
}
