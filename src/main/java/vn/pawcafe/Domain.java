package vn.pawcafe;

import javax.persistence.*;
import java.time.LocalDate;

/** Persistence models kept together for the small single-branch starter project. */
public final class Domain {
    private Domain() {}

    @Entity @Table(name="pets")
    public static class Pet {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        public String name, species, breed, personality, imageUrl, health, status;
        public boolean availableForAdoption;
        public Pet() {}
        public Pet(String name,String species,String breed,String personality,String imageUrl,String health,String status,boolean adoption){this.name=name;this.species=species;this.breed=breed;this.personality=personality;this.imageUrl=imageUrl;this.health=health;this.status=status;this.availableForAdoption=adoption;}
    }

    @Entity @Table(name="menu_items")
    public static class MenuItem {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        public String name, category, description, imageUrl;
        public long price;
        public boolean available=true;
        public MenuItem() {}
        public MenuItem(String n,String c,String d,long p,String image){name=n;category=c;description=d;price=p;imageUrl=image;}
    }

    @Entity @Table(name="bookings")
    public static class Booking {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        public String customerName, email, phone, slot, status;
        public LocalDate visitDate;
        public int guests;
        public long total;
        public Booking() {}
    }

    @Entity @Table(name="adoption_applications")
    public static class Adoption {
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
        public String applicantName, email, phone, experience, status;
        public Long petId;
        public Adoption() {}
    }
}
