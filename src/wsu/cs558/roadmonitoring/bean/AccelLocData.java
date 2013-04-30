package wsu.cs558.roadmonitoring.bean;

public class AccelLocData {
	private int id;
	private long timeStamp;
	private double x;
	private double y;
	private double z;
	private double latitude;
	private double longitude;

	public AccelLocData(int id, long timeStamp, double x, double y, double z,
			double latitude, double longitude) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.x = x;
		this.y = y;
		this.z = z;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public AccelLocData(long timeStamp, double x, double y, double z,
			double latitude, double longitude) {
		this.timeStamp = timeStamp;
		this.x = x;
		this.y = y;
		this.z = z;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
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
		return "AccelData [timestamp=" + timeStamp + ", x=" + x + ", y=" + y
				+ ", z=" + z + ", latitude=" + latitude + ", longitude="
				+ longitude + "]" +"\n";
	}

}
