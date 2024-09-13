package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum Option {

	ADD(

			"Add client..."

	),
	STOP(

			"Stop awaiting clients..."

	),
	REMOVE(

			"Disconnect selected",
			"Shortcut: `Delete`"

	),
	CONTROLS(

			"Show controls for selected"

	),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, Option> labelEnumMap = new IdentityHashMap<>();

	static {
		for (final var o : Option.values())
			Option.labelEnumMap.put(o.LABEL, o);
	}

	private Option(final String p_label) {
		this.TOOLTIP = "";
		this.LABEL = p_label;
	}

	private Option(final String p_label, final String p_tooltip) {
		this.LABEL = p_label;
		this.TOOLTIP = p_tooltip;
	}

	public static Option valueOfLabel(final String p_label) {
		return Option.labelEnumMap.get(p_label);
	}
	// endregion

}
