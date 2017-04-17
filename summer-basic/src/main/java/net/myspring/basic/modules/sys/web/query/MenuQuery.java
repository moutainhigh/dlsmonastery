package net.myspring.basic.modules.sys.web.query;

import net.myspring.basic.modules.sys.dto.MenuCategoryDto;

import java.util.List;

/**
 * Created by lihx on 2017/4/7.
 */
public class MenuQuery {
    private String category;
    private String name;
    private String sort;
    private String menuCategoryId;
    private List<MenuCategoryDto> menuCategoryDtoList;
    private List<String> categoryList;

    public List<MenuCategoryDto> getMenuCategoryDtoList() {
        return menuCategoryDtoList;
    }

    public void setMenuCategoryDtoList(List<MenuCategoryDto> menuCategoryDtoList) {
        this.menuCategoryDtoList = menuCategoryDtoList;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(String menuCategoryName) {
        this.menuCategoryId = menuCategoryName;
    }
}
