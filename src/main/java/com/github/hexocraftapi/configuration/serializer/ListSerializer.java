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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ListSerializer implements Serializer<List<?>>
{
	private ListSerializer() {}
	private static ListSerializer t = new ListSerializer();
	public static ListSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final List<?> object)
	{
		Validate.notNull(object, "object cannot be null");

		final List<Object> result = new ArrayList<Object>();
		for(final Object value : object)
			result.add(configuration.serialize(configuration, value));

		return result;
	}

	@Override
	public List<?> deserialize(Configuration configuration, Class<? extends List<?>> oClass, final Class<?>[] pClass, final Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Validate.notNull(object, "object cannot be null");

		final List<Object> result = new ArrayList<Object>();
		for(final Object value : (List<?>)object)
			result.add(configuration.deserialize(configuration, pClass != null ? pClass[0] : value.getClass(), null, value));

		return result;
	}
}
