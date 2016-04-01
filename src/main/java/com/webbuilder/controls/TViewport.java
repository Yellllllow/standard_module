package com.webbuilder.controls;

public class TViewport extends ExtControl {
	public void create() throws Exception {
		headerScript("this.add([");
		footerScript("]);");
	}
}
