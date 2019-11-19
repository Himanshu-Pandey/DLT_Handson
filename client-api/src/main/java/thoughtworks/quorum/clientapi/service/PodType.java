package thoughtworks.quorum.clientapi.service;

public enum PodType {

    EC("ec"),
    BOOTH1("booth1"),
    BOOTH2("booth2"),
    BOOTH("booth");

    private String name;

    PodType(String unit) {
        this.name = unit;
    }

    public String toString() {
        return this.name;
    }
}
