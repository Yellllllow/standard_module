package com.webbuilder.controls;

import com.webbuilder.utils.StringUtil;

public class ExtExpress extends ExtControl {
	private String key[] = { "id" };

	public void create() throws Exception {
		headerScript(StringUtil.concat("Wd.", gs("id"), "={"));
		footerScript("};");
		headerScript(getJson(null));
	}

	protected String[] getReservedKeys() throws Exception {
		return key;
	}
}