package com.github.hexocraftapi.configuration.helper;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class Paths
{
	// List containing all the paths
	private ArrayList<Path> paths = new ArrayList<>();


	// Default constructior
	public Paths() { }

	// Test if the class is considered as a value or not
	static boolean isValue(Class<?> clazz)
	{
		return !(Configuration.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz));
	}

	// Test if the class is considered as list
	static boolean isList(Class<?> clazz)
	{
		return List.class.isAssignableFrom(clazz);
	}

	// Test if the class is considered as list
	static boolean isMap(Class<?> clazz)
	{
		return Map.class.isAssignableFrom(clazz);
	}

	// Create a new Path
	Path create(Path parent, String path)
	{
		return new Path(this, parent, path);
	}

	// Create a new Path
	Path create(String path)
	{
		return new Path(this, path);
	}

	// Add a path to the list
	// Only new path will be added
	Path add(Path parent, Path path)
	{
		// Check if path exist
		Path exist = findFromPath(parent, path.path());
		if(exist != null) return exist;

		// Add path to map
		if(paths.add(path))
			return path;

		return null;
	}



	Path get(int index)
	{
		return paths.get(index);
	}

	Path get(String path)
	{
		for(int i = 0; i < size(); i++)
		{
			if(get(i).path().equals(path))
				return get(i);
		}
		return null;
	}

	int size()
	{
		return paths.size();
	}



	// Construct the path
	private String constructPath(Path parent, String path)
	{
		if(path.startsWith("*."))
		{
			if(parent != null)
				return path.replace("*", parent.path());
			else
			{
				throw new AssertionError("This should not append");
//				Path lastPath = null;
//				for(int i = size() - 1; i >= 0 && lastPath == null; i--)
//					if(get(i).value() == false)
//						lastPath = get(i);
//
//				if(lastPath != null)
//					return path.replace("*", lastPath.path());
//				else
//					return path.replace("*.", "");
			}
		}

		return path;
	}

	private Path findFromPath(Path parent, String path)
	{
		for(int i = 0; i < size(); i++)
		{
			if( ((parent == null && get(i).parent() == null) || (parent != null && get(i).parent() != null && get(i).parent().path().equals(parent.path())))
				&& get(i).path() != null && get(i).path().equals(path))
				return get(i);
		}
		return null;
	}

	// Find a path by searching the right parent
	// and the right origin.
	private Path findFromOrigin(Path parent, String origin)
	{
		for(int i = 0; i < size(); i++)
		{
			if( ((parent == null && get(i).parent() == null) || (parent != null && get(i).parent() != null && get(i).parent().path().equals(parent.path())))
				&& get(i).origin() != null && get(i).origin().equals(origin))
				return get(i);
		}
		return null;
	}




	public class Path
	{
		private final Paths paths;

		private Path parent;
		private String origin;					// Path defined in the object class
		private String[] comments;				// Comments associated to the path
		private boolean isValue = false;		// Indicate if the path is associated with a value or not
		private boolean isChildList = false;	// Indicate if the path is associated with a value inside List
		private boolean isChildMap = false;		// Indicate if the path is associated with a value inside Map
		private String path;					// The calculated path


		Path(Paths paths, Path parent, String path)
		{
			this.paths = paths;
			this.parent = path.contains(".") ? parent : null;
			this.origin = path;
			this.path = null;
		}

		Path(Paths paths, String path)
		{
			this(paths, null, path);
		}

		Path parent() { return parent; }
		Path object(Path parent) { this.parent = parent; return  this; }

		String origin() { return this.origin; }
		Path origin(String origin) { this.origin = origin; return  this; }

		String[] comments()
		{
			return this.comments;
		}
		Path comments(String[] comments) { this.comments = comments; return  this; }

		boolean value() { return this.isValue; }
		Path value(boolean value) { this.isValue = value; return  this; }

		boolean childList() { return this.isChildList; }
		Path childList(boolean childList) { this.isChildList = childList; return  this; }

		boolean childMap() { return this.isChildMap; }
		Path childMap(boolean childMap) { this.isChildMap = childMap; return  this; }

		String path()
		{
			if(this.path != null)
				return this.path;

			String path = this.origin();

			// Check if path exist
			Path exist = paths.findFromOrigin(parent, path);

			// Construct path
			this.path = exist != null ? exist.path() : paths.constructPath(this.parent, path);

			return this.path;
		}
	}
}
