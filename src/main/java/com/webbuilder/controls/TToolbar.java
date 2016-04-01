package com.webbuilder.controls;

import com.webbuilder.utils.StringUtil;

public class TToolbar extends ExtControl {
	private String key[] = { "type" };

	public void create() throws Exception {
		String type = gs("type"), xtype, cls, id = gs("id");
		boolean autoCreate = gb("autoCreate", true);

		if (type.equals("titlebar")) {
			xtype = "titlebar";
			cls = "Ext.TitleBar";
		} else {
			xtype = "toolbar";
			cls = "Ext.Toolbar";
		}
		if (hasParent || !autoCreate) {
			xtype = StringUtil.concat("xtype:\"", xtype, "\"");

			if (hasParent) {
				headerScript(StringUtil.concat(getComma(), "{", getJson(xtype)));
				footerScript("}");
			} else {
				headerScript(StringUtil.concat("Wd.", id, "=Wd._", id, "={",
						getJson(xtype)));
				footerScript("};");
			}
		} else {
			headerScript(StringUtil.concat("Wd.", id, "=new ", cls, "({"));
			footerScript("});");
			headerScript(getJson(null));
		}
		if (hasChild) {
			headerScriptNL(",items:[");
			footerScript("]");
		}
	}

	protected String[] getReservedKeys() throws Exception {
		return key;
	}
}
