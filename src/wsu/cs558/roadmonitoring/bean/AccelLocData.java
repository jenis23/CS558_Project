package wsu.cs558.roadmonitoring.bean;

public class AccelLocData {
	private long timestamp;
	private double x;
	private double y;
	private double z;
	private double latitude;
	private double longitude;

	public AccelLocData(long timestamp, double x, double y, double z,
			double latitude, double longitude) {
		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "AccelData [timestamp=" + timestamp + ", x=" + x + ", y=" + y
				+ ", z=" + z + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}

}
