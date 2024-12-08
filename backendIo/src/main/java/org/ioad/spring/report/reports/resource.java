package org.ioad.spring.report.reports;

public class resource {

    private String id;
    private String name;
    private String location;
    private String expDate;
    private String quantity;
    private String unit;
    private String organisationId;
    private String resourceStatus;
    private String resourceType;

    // Konstruktor z 9 argumentami
    public resource(String id, String name, String location, String expDate, String quantity,
                    String unit, String organisationId, String resourceStatus, String resourceType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.expDate = expDate;
        this.quantity = quantity;
        this.unit = unit;
        this.organisationId = organisationId;
        this.resourceStatus = resourceStatus;
        this.resourceType = resourceType;
    }

    // Gettery i Settery (opcjonalne)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getExpDate() { return expDate; }
    public String getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getOrganisationId() { return organisationId; }
    public String getResourceStatus() { return resourceStatus; }
    public String getResourceType() { return resourceType; }
}
