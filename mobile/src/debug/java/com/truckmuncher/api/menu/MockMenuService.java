package com.truckmuncher.api.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;

public class MockMenuService implements MenuService {

    @Override
    public MenuItemAvailabilityResponse getMenuItemAvailability(@Body MenuItemAvailabilityRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void getMenuItemAvailability(@Body MenuItemAvailabilityRequest request, Callback<MenuItemAvailabilityResponse> callback) {
        callback.success(getMenuItemAvailability(request), null);
    }

    @Override
    public FullMenusResponse getFullMenus(@Body FullMenusRequest request) throws RetrofitError {
        List<Menu> menus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            menus.add(getMenu(new MenuRequest("Truck" + i)).menu);
        }
        return new FullMenusResponse(menus);
    }

    @Override
    public void getFullMenus(@Body FullMenusRequest request, Callback<FullMenusResponse> callback) {
        callback.success(getFullMenus(request), null);
    }

    @Override
    public MenuResponse getMenu(@Body MenuRequest request) throws RetrofitError {
        String truckId = "Truck1";
        if (request != null) {
            truckId = request.truckId;
        }
        int truckNumber = Integer.parseInt(truckId.substring(truckId.length() - 1));


        List<Category> categories = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            List<MenuItem> menuItems = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                MenuItem item = new MenuItem.Builder()
                        .id("MenuItem" + truckNumber + "_" + j + "_" + i)
                        .name("MenuItem" + j + "_" + i)
                        .price(i / 2f)
                        .notes("MenuItem" + j + "_" + i)
                        .tags(Arrays.asList("Tag1", "Tag2", "Tag3"))
                        .orderInCategory(i)
                        .isAvailable(i % 2 == 0)
                        .build();
                menuItems.add(item);
            }
            Category category = new Category.Builder()
                    .id("Category" + truckNumber + "_" + j)
                    .name("Category" + j)
                    .notes("Category" + j)
                    .orderInMenu(j)
                    .menuItems(menuItems)
                    .build();
            categories.add(category);
        }

        Menu menu = new Menu.Builder().truckId(truckId).categories(categories).build();
        return new MenuResponse.Builder().menu(menu).build();
    }

    @Override
    public void getMenu(@Body MenuRequest request, Callback<MenuResponse> callback) {
        callback.success(getMenu(request), null);
    }

    @Override
    public ModifyMenuItemAvailabilityResponse modifyMenuItemAvailability(@Body ModifyMenuItemAvailabilityRequest request) throws RetrofitError {
        return new ModifyMenuItemAvailabilityResponse();
    }

    @Override
    public void modifyMenuItemAvailability(@Body ModifyMenuItemAvailabilityRequest request, Callback<ModifyMenuItemAvailabilityResponse> callback) {
        callback.success(modifyMenuItemAvailability(request), null);
    }
}
