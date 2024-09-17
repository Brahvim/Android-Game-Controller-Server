package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionsTray {

	ADD(),
	HOME(),
	CLOSE(),
	REMOVE(),
	LAYOUT(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	private static final IdentityHashMap<String, OptionsTray> LABEL_ENUM_MAP = new IdentityHashMap<>();
	private static final OptionsTray[] ORDER_UI = new OptionsTray[] {

			// ADD,
			HOME,
			CLOSE,
			LAYOUT,

	};

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsTray.valuesOrdered())
			OptionsTray.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsTray() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("ListTray", myName);
		this.TOOLTIP = App.STRINGS.getString("TooltipsListTray", myName);
	}

	public static OptionsTray[] valuesOrdered() {
		return OptionsTray.ORDER_UI;
	}

	public static OptionsTray valueOfLabel(final String p_label) {
		return OptionsTray.LABEL_ENUM_MAP.get(p_label);
	}
	// endregion

}
