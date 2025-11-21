package Data;

import java.io.Serializable;
import java.util.Objects;

public class Equipment implements Serializable, Identifiable {

    private String id;
    private String name;
    private String description;
    private EquipmentStatus status;
    private String imageUri;

    public Equipment(String id, String name, String description, String imageUri, EquipmentStatus status) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.imageUri = imageUri;
    }

    public Equipment(String name,String description,String imageUri, EquipmentStatus status) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
