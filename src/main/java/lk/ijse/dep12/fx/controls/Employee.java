package lk.ijse.dep12.fx.controls;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private final String id;
    private String nic;
    private String fullName;
    private String address;
    private String gender;

    public Employee(String id, String nic, String fullName, String address, String gender) {
        this.id = id;
        this.nic = nic;
        this.fullName = fullName;
        this.address = address;
        this.gender = gender;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getId() {
        return id;
    }

    public String getNic() {
        return nic;
    }


    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

}
