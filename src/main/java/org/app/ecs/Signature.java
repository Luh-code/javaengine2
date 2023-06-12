package org.app.ecs;

import java.util.BitSet;

import static org.app.ecs.ECS.MAX_COMPONENTS;

public class Signature implements Cloneable {
	//----- Members -----

	private BitSet bits = new BitSet(MAX_COMPONENTS);

	//----- Methods -----

	public Signature() {

	}

	public Signature(Signature s) {
		this.bits = s.getBitSet();
	}

	public BitSet getBitSet() {
		return bits;
	}

	public void setBitSet(BitSet b) {
		bits = (BitSet) b.clone();
	}

	public void setBit(int b, boolean v) {
		bits.set(b, v);
	}

	public void flipBit(int b) {
		bits.set(b, !bits.get(b));
	}

	public void clear() {
		bits.clear();
	}

	public boolean compare(Signature s) {
		BitSet temp = (BitSet) s.getBitSet().clone();
		temp.andNot(bits);
		return temp.isEmpty();
	}

	@Override
	public Object clone() {
		return new Signature(this);
	}
}
