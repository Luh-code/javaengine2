package org.app.ecs;

public class Entity implements Comparable<Entity> {
	//----- Members -----

	private final int id;

	//----- Methods -----

	public int getId() {
		return id;
	}

	public Entity(int e) {
		this.id = e;
	}

	@Override
	public int compareTo(Entity o) {
		return this.id - o.getId();
	}
}
