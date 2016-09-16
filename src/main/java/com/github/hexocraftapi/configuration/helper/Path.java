package com.github.hexocraftapi.configuration.helper;

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

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
class Path
{
	private Class owner;
	private String origin;
	private String[] paths;
	private String[] comments;
	private boolean value = false;
	private String path;


	Path(Class owner, String path, String... comments)
	{
		this.owner = owner;
		this.origin = path;
		this.paths = path.split("\\.");
		this.comments = comments;
		this.path = path();
	}


	String path()
	{
		if(this.path != null) return this.path;

		StringBuilder builder = new StringBuilder(this.paths.length);
		for(int i = 0; i < this.paths.length; i++)
		{
			builder.append(this.paths[i]);
			builder.append(i<this.paths.length-1?".":"");
		}
		this.path = builder.toString();
		return path();
	}


	Class owner() { return this.owner; }

	Path owner(Class owner) { this.owner = owner; return  this; }

	String origin() { return this.origin; }

	Path origin(String origin) { this.origin = origin; return  this; }

	public void setComments(String[] comments) { this.comments = comments; }

	String[] comments()
	{
		return this.comments;
	}

	boolean value() { return this.value; }

	Path value(boolean value) { this.value = value; return  this; }


	int levels()
	{
		return this.paths.length;
	}

	Path level(int index)
	{
		StringBuilder builder = new StringBuilder(index);
		for(int i = 0; i <= index; i++)
			builder.append(this.paths[i]);
		return new Path(null, builder.toString());
	}

	Path top()
	{
		return level(0);
	}

	public Path up()
	{
		return level(levels() - 1);
	}
}
