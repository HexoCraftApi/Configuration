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
import com.github.hexocraftapi.configuration.serializer.json.JsonVector;
import org.apache.commons.lang.Validate;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class VectorSerializer implements Serializer<Vector>
{
	private VectorSerializer() {}
	private static VectorSerializer t = new VectorSerializer();
	public static VectorSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final Vector vector)
	{
		Validate.notNull(vector, "vector cannot be null");

		final JsonVector jsonVector = new JsonVector(vector);
		return jsonVector.toJson();
	}

	@Override
	public Vector deserialize(Configuration configuration, Class<? extends Vector> oClass, final Class<?>[] pClass, Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		final JsonVector jsonVector = JsonVector.fromJson(object.toString());
		return jsonVector.toVector();
	}
}

