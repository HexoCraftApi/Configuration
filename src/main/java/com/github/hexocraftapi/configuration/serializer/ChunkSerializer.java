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
import com.github.hexocraftapi.configuration.serializer.json.JsonChunck;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class ChunkSerializer implements Serializer<Chunk>
{
	private ChunkSerializer() {}
	private static ChunkSerializer t = new ChunkSerializer();
	public static ChunkSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final Chunk chunk)
	{
		Validate.notNull(chunk, "chunk cannot be null");

		final JsonChunck jsonChunck = new JsonChunck(chunk);
		return jsonChunck.toJson();
	}

	@Override
	public Chunk deserialize(Configuration configuration, Class<? extends Chunk> oClass, final Class<?>[] pClass, Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		final JsonChunck jsonChunck = JsonChunck.fromJson(object.toString());
		return jsonChunck.toChunck();
	}
}
