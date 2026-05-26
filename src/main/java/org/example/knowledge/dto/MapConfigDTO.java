package org.example.knowledge.dto;

public class MapConfigDTO {

    private Boolean enableMap;
    private String mapLongitudeField;
    private String mapLatitudeField;
    private String mapLabelField;
    private String mapDescField;

    public Boolean getEnableMap() { return enableMap; }
    public void setEnableMap(Boolean enableMap) { this.enableMap = enableMap; }

    public String getMapLongitudeField() { return mapLongitudeField; }
    public void setMapLongitudeField(String mapLongitudeField) { this.mapLongitudeField = mapLongitudeField; }

    public String getMapLatitudeField() { return mapLatitudeField; }
    public void setMapLatitudeField(String mapLatitudeField) { this.mapLatitudeField = mapLatitudeField; }

    public String getMapLabelField() { return mapLabelField; }
    public void setMapLabelField(String mapLabelField) { this.mapLabelField = mapLabelField; }

    public String getMapDescField() { return mapDescField; }
    public void setMapDescField(String mapDescField) { this.mapDescField = mapDescField; }
}
