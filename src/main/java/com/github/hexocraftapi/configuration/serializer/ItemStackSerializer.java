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
import com.github.hexocraftapi.configuration.serializer.json.JsonItemStack;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class ItemStackSerializer implements Serializer<ItemStack>
{
	private ItemStackSerializer() {}
	private static ItemStackSerializer t = new ItemStackSerializer();
	public static ItemStackSerializer get() { return t; }

	@Override
	public Object serialize(final Configuration configuration, final ItemStack itemStack)
	{
		Validate.notNull(itemStack, "chunk cannot be null");

		final JsonItemStack jsonItemStack = new JsonItemStack(configuration.getPlugin(), itemStack);
		return jsonItemStack.toJson();
	}

	@Override
	public ItemStack deserialize(Configuration configuration, Class<? extends ItemStack> oClass, final Class<?>[] pClass, Object object)
	throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		final JsonItemStack jsonItemStack = JsonItemStack.fromJson(configuration.getPlugin(), object.toString());
		return jsonItemStack.toItemStack();
	}
}
