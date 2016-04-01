package com.webbuilder.controls;

import com.webbuilder.common.SysMap;

public class TSelect extends ExtControl {
	private String key[] = { "keyName", "keySortType", "options" };

	protected String getTagProperties() throws Exception {
		String keyName = gs("keyName"), options = gs("options");

		if (!keyName.isEmpty())
			options = SysMap.getList(keyName, gs("keySortType"), true);
		addExpress("options", options);
		return getPBuffer();
	}

	protected String[] getReservedKeys() throws Exception {
		return key;
	}

}
