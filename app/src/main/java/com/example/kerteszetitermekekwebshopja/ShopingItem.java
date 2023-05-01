package com.example.kerteszetitermekekwebshopja;

public class ShopingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private int imageResource;
    private int carted;

    public ShopingItem() {
    }

    public ShopingItem(String name, String info, String price, int imageResource, int carted) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.imageResource = imageResource;
        this.carted = carted;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCarted() {
        return carted;
    }
    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
