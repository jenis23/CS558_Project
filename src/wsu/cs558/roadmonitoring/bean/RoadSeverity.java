package wsu.cs558.roadmonitoring.bean;

public class RoadSeverity {

	private double roadLatitude;
	private double roadLongitude;
	private double roadSeverity;
	private int roadId;

	public double getRoadLatitude() {
		return roadLatitude;
	}

	public void setRoadLatitude(double roadLatitude) {
		this.roadLatitude = roadLatitude;
	}

	public double getRoadLongitude() {
		return roadLongitude;
	}

	public void setRoadLongitude(double roadLongitude) {
		this.roadLongitude = roadLongitude;
	}

	public double getRoadSeverity() {
		return roadSeverity;
	}

	public void setRoadSeverity(double roadSeverity) {
		this.roadSeverity = roadSeverity;
	}

	public int getRoadId() {
		return roadId;
	}

	public void setRoadId(int roadId) {
		this.roadId = roadId;
	}

}
