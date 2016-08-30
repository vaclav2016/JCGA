package onion.cga;

public class CgaMouseClick {

	public enum Type {
		CLICK, RELEASE
	}

	private final int x;
	private final int y;
	private final Type type;

	public CgaMouseClick(Type type, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.type == null) ? 0 : this.type.hashCode());
		result = (prime * result) + this.x;
		result = (prime * result) + this.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CgaMouseClick other = (CgaMouseClick) obj;
		if (this.type != other.type) {
			return false;
		}
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CgaMouseClick [x=" + this.x + ", y=" + this.y + ", type=" + this.type + "]";
	}

}
