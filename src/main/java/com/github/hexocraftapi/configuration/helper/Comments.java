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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class Comments
{
	private static final String COMMENT_PREFIX = "#";

	private final Configuration config;
	private       Paths         paths;

	private String[] header;
	private String[] footer;



	public Comments(Configuration config)
	{
		this.config = config;
		this.paths = config.getPaths();
	}

	public void load() throws IOException
	{
		// Read config file and store each line in an array of string
		ArrayList<String> lines = new ArrayList<>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(this.config.getConfigFile()));
		while((line = reader.readLine()) != null)
			lines.add(line);
		reader.close();

		// Parse header
		this.header = parseHeader(lines);

		// Parse footer
		this.footer = parseFooter(lines);

		// Parse comments
		parseComments(lines, (this.header != null && this.header.length > 0) ? this.header.length : 0, lines.size() - ((this.footer != null && this.footer.length > 0) ? this.footer.length : 0) - 1);
	}

	private String[] parseHeader(ArrayList<String> lines)
	{
		ArrayList<String> header = new ArrayList<>();

		for(String line : lines)
		{
			if(line.startsWith(COMMENT_PREFIX))
				header.add(line);
			else
				break;
		}

		return header.toArray(new String[header.size()]);
	}

	private String[] parseFooter(ArrayList<String> lines)
	{
		ArrayList<String> footer = new ArrayList<>();

		for(int i = lines.size()-1; i >= 0; i--)
		{
			String line = lines.get(i);

			if(line.startsWith(COMMENT_PREFIX))
				footer.add(line);
			else
				break;
		}

		return footer.toArray(new String[footer.size()]);
	}

	private void parseComments(ArrayList<String> lines, int from, int to)
	{
		String line, trimLine;
		String path, fullPath, lastPath = "";
		int indent;
		final List<String> comments = new ArrayList<>();

		// Parse each lines to find comments
		for(int i = from; i <= to; i++)
		{
			line = lines.get(i);
			trimLine = line.trim();
			fullPath = line;

			// Find all comments
			if(!trimLine.isEmpty() && trimLine.startsWith(COMMENT_PREFIX))
			{
				comments.add(trimLine);
				continue;
			}

			// Found comment Path
			path = fullPath.split(":")[0];
			if(!path.isEmpty())
			{
				// Erase last path if necessary
				indent = path.indexOf(path.trim());
				if(indent==0) lastPath = "";

				// Reconstruct full path
				if(!lastPath.isEmpty())
				{
					// Update last path from indent value
					String[] pathParts = lastPath.split("\\.");
					lastPath = "";
					for(int j=0;j<(indent/2);j++)
						lastPath += pathParts[j] + ".";

					// Full path
					path = (lastPath + path.trim()).replaceAll("\'","");
				}

				// Store the path comment
				Paths.Path p = this.paths.get(path);
				if(p==null) p = this.paths.create(path);
				p.comments(comments.toArray(new String[comments.size()]));

				// Keep in mind the last path
				lastPath = path;

				// clear comments
				comments.clear();
			}
		}
	}

	public void save() throws IOException
	{
		// Read config file and store each line in an array of string
		ArrayList<String> lines = new ArrayList<>(); String line;
		BufferedReader reader = new BufferedReader(new FileReader(this.config.getConfigFile()));
		while((line = reader.readLine()) != null)
			lines.add(line);
		reader.close();

		// Create the writer
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.config.getConfigFile()));

		// Header
		String[] header = this.config.getHeader();
		if(header != null && header.length > 0)
		{
			for(String aHeader : header)
			{
				writer.write(aHeader);
				writer.newLine();
			}
			writer.newLine();
		}

		// Path
		String path, lastPath = ""; int indent = 0; String[] comments;
		for (int i = 0; i < lines.size(); i++)
		{
			// Existing comment line will be replaced
			if(lines.get(i).startsWith(COMMENT_PREFIX))
				continue;

			// Check if a path exist
			line = lines.get(i);
			path = line.contains(":") ? line.split(":")[0].replace("- ", "") : "";
			if(path.isEmpty())
			{
				writer.write(String.format("%" + (indent + 2) + "s", "") + line.trim());
				writer.newLine();
				continue;
			}

			// Erase last path if necessary
			indent = path.indexOf(path.trim());
			if(line.trim().startsWith("-")) indent += 2;
			if(indent==0) lastPath = "";

			// Reconstruct full path
			if(!lastPath.isEmpty())
			{
				// Update last path from indent value
				String[] pathParts = lastPath.split("\\.");
				lastPath = "";
				for(int j=0;j<(indent/2);j++)
					lastPath += pathParts[j] + ".";

				// Full path
				path = (lastPath + path.trim()).replaceAll("\'","");
			}

			// increase indent for list itel
			if(this.config.getPaths().get(path)!=null)
				indent += isListChild(path) ? (line.trim().startsWith("-") ? 0 : 2) : 0;

			// New line between each section
			if(indent==0) writer.newLine();

			// Add comment to the path
			comments = this.config.getComment(path);
			if(comments != null && comments.length > 0 && !comments[0].isEmpty())
			{
				boolean addDash = !comments[0].startsWith("#");
				for(String comment : comments)
				{
					writer.write((indent > 0 ? String.format("%" + indent + "s", "") : "") + (addDash ? "# " : "") + comment);
					writer.newLine();
				}
			}

			// Keep in mind the last path
			lastPath = path;

			// Write the original path
			writer.write((indent>0 ? String.format("%" + indent + "s", "") : "") + line.trim());
			writer.newLine();
		}

		// Footer
		String[] footer = this.config.getFooter();
		if(footer != null && footer.length > 0)
		{
			writer.newLine();
			writer.newLine();

			for(String aFooter : footer)
			{
				writer.write(aFooter);
				writer.newLine();
			}
		}

		// Close the writer
		writer.close();
	}

	private boolean isListChild(String path)
	{
		Paths.Path p = this.config.getPaths().get(path);
		return p != null && p.childList();
	}

	public String[] getHeader() { return this.header; }
	public String[] getFooter() { return this.footer; }
	public String[] getComment(String path) {
		Paths.Path p = this.paths.get(path);
		return p != null ? p.comments() : null;
	}
}
