package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum Option {

	ADD(),
	STOP(),
	REMOVE(),
	LAYOUT(),
	CONTROLS(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, Option> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : Option.valuesOrdered())
			Option.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private Option() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("OptionsList", myName);
		this.TOOLTIP = App.STRINGS.getString("Tooltips", myName);
	}

	public static Option[] valuesOrdered() {
		return new Option[] {

				ADD,
				STOP,
				REMOVE,
				LAYOUT,
				CONTROLS,

		};
	}

	public static Option valueOfLabel(final String p_label) {
		return Option.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
