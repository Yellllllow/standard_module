package com.webbuilder.common;

import java.io.File;
import java.io.FileReader;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.StringUtil;

public class ScriptBuffer {
	private static Compilable compilable;
	private static ConcurrentHashMap<String, CompiledScript> scriptMap;
	private static Bindings utilBindings;

	public static void run(String id, String scriptText,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (scriptMap == null)
			initialize(false);
		CompiledScript script = scriptMap.get(id);
		if (script == null) {
			script = compilable.compile(StringUtil.concat("(function(){",
					scriptText, "\n})();"));
			scriptMap.put(id, script);
		}
		ScriptContext context = new SimpleScriptContext();
		context.setBindings(utilBindings, ScriptContext.GLOBAL_SCOPE);
		context.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);
		context.setAttribute("response", response, ScriptContext.ENGINE_SCOPE);
		script.eval(context);

	}

	public static void remove(String id) throws Exception {
		if (scriptMap == null)
			return;
		Set<Entry<String, CompiledScript>> es = scriptMap.entrySet();
		String k;

		for (Entry<String, CompiledScript> e : es) {
			k = e.getKey();
			if (k.startsWith(id + "."))
				scriptMap.remove(k);
		}
	}

	public static synchronized void initialize(boolean reload) throws Exception {
		if (!reload && scriptMap != null)
			return;
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		compilable = (Compilable) engine;
		utilBindings = engine.createBindings();
		CompiledScript wbScript;
		FileReader reader = new FileReader(new File(Main.path,
				"webbuilder/script/server.js"));
		try {
			wbScript = compilable.compile(reader);
		} finally {
			reader.close();
		}
		wbScript.eval(utilBindings);
		scriptMap = new ConcurrentHashMap<String, CompiledScript>();
	}
}