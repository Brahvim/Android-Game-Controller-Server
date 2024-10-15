package com.brahvim.agc.server.back;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import com.brahvim.agc.server.front.App;

public final class Profile {

	// region Fields.
	protected static final ArrayList<Integer> LIST_ALL = new ArrayList<>();
	protected static final ArrayList<Integer> LIST_ACTIVE = new ArrayList<>();

	private static final ArrayList<File> LIST_FILES = new ArrayList<>();
	private static final ArrayList<String> LIST_DATA = new ArrayList<>();
	private static final ArrayList<String> LIST_NAMES = new ArrayList<>();
	private static final ArrayDeque<Integer> QUEUE_FREE_INDICES = new ArrayDeque<>(1);

	private static int count = 1;

	private final Integer id;
	// endregion

	static {
		// Creating a "null-object"!
		Profile.QUEUE_FREE_INDICES.add(1);
	}

	public Profile(final File p_file) throws IOException {
		this.id = Profile.create();
		Profile.LIST_ALL.add(this.id);

		final StringBuilder builder = new StringBuilder();

		try (final var reader = new BufferedReader(new FileReader(p_file))) {

			while (reader.ready())
				builder.append(reader.readLine());

		} // finally { }

		this.setFile(p_file);
		this.setData(builder.toString());
		this.setName(String.format("Profile %d", Profile.count));
	}

	public File getFile() {
		return Profile.LIST_FILES.get(this.id);
	}

	public String getData() {
		return Profile.LIST_DATA.get(this.id);
	}

	public String getName() {
		return Profile.LIST_NAMES.get(this.id);
	}

	public File setFile(final File p_file) {
		return Profile.LIST_FILES.set(this.id, p_file);
	}

	public String setData(final String p_data) {
		return Profile.LIST_DATA.set(this.id, p_data);
	}

	public String setName(final String p_name) {
		return Profile.LIST_NAMES.set(this.id, p_name);
	}

	public synchronized void destroy() {
		Profile.LIST_ALL.remove(this.id);
		Profile.LIST_ACTIVE.remove(this.id);
		Profile.LIST_DATA.set(this.id, null);

		Profile.QUEUE_FREE_INDICES.add(this.id);
	}

	private static synchronized Integer create() {
		Integer id = Profile.QUEUE_FREE_INDICES.poll();

		if (id == null)
			id = ++Profile.count;

		App.ensureArrayListSize(Profile.LIST_DATA, id);
		App.ensureArrayListSize(Profile.LIST_NAMES, id);
		App.ensureArrayListSize(Profile.LIST_FILES, id);

		return id;
	}

}
