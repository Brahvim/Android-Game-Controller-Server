package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

import com.brahvim.agc.server.App;

public enum Option {

	ADD(),
	STOP(),
	REMOVE(),
	CONTROLS(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, Option> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		for (final var o : Option.values())
			Option.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private Option() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("Option", myName);
		this.TOOLTIP = App.STRINGS.getString("Tooltip", myName);
	}

	public static Option valueOfLabel(final String p_label) {
		return Option.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
