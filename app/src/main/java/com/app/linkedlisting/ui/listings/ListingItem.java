package com.app.linkedlisting.ui.listings;

public class ListingItem {
    private String id;
    private String name;
    private String imageUrl;
    private String price;  // Changed from double to String

    public ListingItem() {
        // Required empty public constructor for Firebase
    }

    public ListingItem(String id, String name, String imageUrl, String price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;  // No need to parse since price is now a String
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
