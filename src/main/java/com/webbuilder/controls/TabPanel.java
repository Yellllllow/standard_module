package com.webbuilder.controls;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webbuilder.utils.StringUtil;

public class TabPanel extends ExtPanel {
	private String key[] = { "plugins", "tabMenu" };

	protected String getTagProperties() throws Exception {
		super.getTagProperties();
		String plugins = gs("plugins");
		JSONArray ja;

		if (plugins.isEmpty())
			ja = new JSONArray("[]");
		else {
			if (plugins.endsWith("]"))
				ja = new JSONArray(plugins);
			else
				ja = new JSONArray("[" + plugins + "]");
		}
		if (gb("tabMenu", true))
			ja.put(new JSONObject("{ptype:\"tabscrollermenu\"}"));
		if (ja.length() != 0)
			addExpress("plugins", ja.toString());
		return getPBuffer();
	}

	protected String[] getReservedKeys() throws Exception {
		return StringUtil.merge(super.getReservedKeys(), key);
	}
}
