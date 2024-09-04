package ar.edu.itba.paw.models;

public class TestUser {
    private final String username;
    private final int id;
    private final String desc;
    private final String vehicleModel;
    private final int maxWeight;

    public TestUser(String username, int id, String desc, String vehicleModel, int maxWeight) {
        this.username = username;
        this.id = id;
        this.desc = desc;
        this.vehicleModel = vehicleModel;
        this.maxWeight = maxWeight;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
}
