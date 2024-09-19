package com.brahvim.agc.server.front;

import java.util.IdentityHashMap;

public enum OptionsProfiles {

	ADD(),
	REMOVE(),
	IMPORT(),
	EXPORT(),

	/* */ ;

	// region Class stuff.
	public final String LABEL;
	public final String TOOLTIP;

	public static final OptionsProfiles[] ORDER_UI = new OptionsProfiles[] { // NOSONAR! Speed!

			ADD,
			REMOVE,
			EXPORT,
			IMPORT,

	};

	private static final IdentityHashMap<String, OptionsProfiles> LABEL_ENUM_MAP = new IdentityHashMap<>();

	static {
		// Could be faster in order (in memory access terms!), right?!:
		for (final var o : OptionsProfiles.ORDER_UI)
			OptionsProfiles.LABEL_ENUM_MAP.put(o.LABEL, o);
	}

	private OptionsProfiles() {
		final String myName = this.name();
		this.LABEL = App.STRINGS.getString("ListProfileChooserOptions", myName);
		this.TOOLTIP = App.STRINGS.getString("TooltipsProfileChooserListOptions", myName);
	}
	// endregion

}
