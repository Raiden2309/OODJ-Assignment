package java.domain;

import java.util.List;

public class SystemRole {
    private String roleName;
    private List<String> permissionList;

    public SystemRole(String roleName,  List<String> permissionList) {
        this.roleName = roleName;
        this.permissionList = permissionList;
    }

    public List<String> getPermissions() {
        return permissionList;
    }

    //Getters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}