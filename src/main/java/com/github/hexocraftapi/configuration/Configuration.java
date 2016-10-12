package com.github.hexocraftapi.configuration;

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

import com.github.hexocraftapi.configuration.annotation.ConfigurationSerializable;
import com.github.hexocraftapi.configuration.helper.Annotations;
import com.github.hexocraftapi.configuration.helper.Comments;
import com.github.hexocraftapi.configuration.helper.PathList;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public abstract class Configuration implements ConfigurationSerializable
{
	private final JavaPlugin plugin;			// The plugin
	private final String fileName;				// File name of the config file
	private final String templateName;			// Template to use

	private final File configFile;
	private final YamlConfiguration yamlConfiguration;
	private PathList paths = new PathList();

	private final Annotations annotations;
	private final Comments comments;
	private boolean saveOnLoad = false;


	protected Configuration(JavaPlugin plugin, String fileName)
	{
		this(plugin, fileName, null);
	}

	protected Configuration(JavaPlugin plugin, String fileName, String templateName)
	{
		// Plugin must exist
		Validate.notNull(plugin, "Plugin cannot be null");

		//
		this.plugin = plugin;
		this.fileName = fileName;
		this.templateName = templateName;

		// Get the plugin data folder
		File dataFolder = plugin.getDataFolder();
		if (dataFolder == null) throw new IllegalStateException();

		// Config file
		this.configFile = (fileName != null && !fileName.isEmpty()) ? new File(plugin.getDataFolder(), fileName) : null;
		this.yamlConfiguration = new YamlConfiguration();

		// Helpers
		this.annotations = new Annotations(this);
		this.comments = new Comments(this);
	}

	// Load the configuration file
	public boolean load()
	{
		try
		{
			// Only load if config file is defined
			if(this.configFile != null)
			{
				// Create config file from template (resource file) if it does not exist
				if(!this.configFile.exists() && this.templateName!=null)
					createFromTemplate();

				// Create an empty config file if it does not exist
				if(!this.configFile.exists())
					this.yamlConfiguration.save(this.configFile);

				// Load file
				this.yamlConfiguration.options().copyHeader(false);
				this.yamlConfiguration.options().header(null);
				this.yamlConfiguration.load(this.configFile);
			}

			// Load fields
			this.annotations.load();

			// Load comments
			if(this.configFile != null)
				this.comments.load();

			// Save if necessary
			if(saveOnLoad) save();

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// Save the configuration file
	public boolean save()
	{
		try
		{
			// Update fields
			this.annotations.update();

			// Only save if config file is defined
			if(this.configFile != null)
			{
				// Save file
				this.yamlConfiguration.save(this.configFile);

				// save comments
				this.comments.save();
			}

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Object serialize(final Configuration configuration, final Object object)
	{
		return this.annotations.serialize(configuration, object);
	}

	@Override
	public Object deserialize(final Configuration configuration, final Class<?> oClass, final Class<?>[] pClass, final Object object) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		return this.annotations.deserialize(configuration, oClass, pClass, object);
	}

	private void createFromTemplate() throws IOException
	{
		// Look for template (resource file) in the jar
		InputStream defConfigStream = this.plugin.getResource(this.templateName);
		if(defConfigStream == null)
			throw new FileNotFoundException("Could not find template file : " + this.templateName);

		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		this.yamlConfiguration.setDefaults(defConfig);
		this.yamlConfiguration.save(this.configFile);
	}

	public JavaPlugin getPlugin() { return plugin; }
	public String getFileName() { return fileName; }
	public String getTemplateName() { return templateName; }
	public File getConfigFile() { return configFile; }
	public YamlConfiguration getYamlConfiguration() { return yamlConfiguration; }
	public PathList getPaths() { return paths; }
	public boolean saveOnLoad() {
		return this.saveOnLoad;
	}
	public void saveOnLoad(boolean saveOnLoad) {
		this.saveOnLoad = saveOnLoad;
	}

	public String[] getHeader() { return (this.annotations.getDefaultHeaderComment()!=null && this.annotations.getDefaultHeaderComment().length > 0) ? this.annotations.getDefaultHeaderComment() : this.comments.getHeader(); }
	public String[] getFooter() { return (this.annotations.getDefaultFooterComment()!=null && this.annotations.getDefaultFooterComment().length > 0) ? this.annotations.getDefaultFooterComment() : this.comments.getFooter(); }
	public String[] getComment(String path)
	{
		String[] comment = this.comments.getComment(path);
		return (comment!=null && comment.length > 0) ? comment : this.annotations.getDefaultComment(path);
	}
}
