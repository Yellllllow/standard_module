package com.webbuilder.controls;

import com.webbuilder.utils.StringUtil;

public class TField extends ExtControl {
	private String key[] = { "type" };

	public void create() throws Exception {
		boolean autoCreate = gb("autoCreate", true);
		String type = gs("type"), xtype, cls, id = gs("id");

		if (type.equals("password")) {
			xtype = "passwordfield";
			cls = "Ext.field.Password";
		} else if (type.equals("email")) {
			xtype = "emailfield";
			cls = "Ext.field.Email";
		} else if (type.equals("url")) {
			xtype = "urlfield";
			cls = "Ext.field.Url";
		} else if (type.equals("search")) {
			xtype = "searchfield";
			cls = "Ext.field.Search";
		} else {
			xtype = "textfield";
			cls = "Ext.field.Text";
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
