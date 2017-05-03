package net.myspring.basic.modules.sys.web.form;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.Role;
import net.myspring.common.form.DataForm;
import net.myspring.common.tree.TreeNode;

import java.util.List;

/**
 * Created by wangzm on 2017/5/2.
 */
public class RoleForm extends DataForm<Role> {
    private String name;
    private String permission;
    private String remarks;
    private String permissionIdStr;
    private TreeNode permissionTree;
    private List<String> permissionIdList= Lists.newArrayList();

    public List<String> getPermissionIdList() {
        return permissionIdList;
    }

    public void setPermissionIdList(List<String> permissionIdList) {
        this.permissionIdList = permissionIdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPermissionIdStr() {
        return permissionIdStr;
    }

    public void setPermissionIdStr(String permissionIdStr) {
        this.permissionIdStr = permissionIdStr;
    }

    public TreeNode getPermissionTree() {
        return permissionTree;
    }

    public void setPermissionTree(TreeNode permissionTree) {
        this.permissionTree = permissionTree;
    }
}
