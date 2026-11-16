package vn.pawcafe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(PetRepository pets,MenuRepository menu){return args->{
        if(pets.count()==0)pets.saveAll(Arrays.asList(
            new Domain.Pet("Milo","Mèo","Mèo mướp","Thân thiện, thích ngủ nắng","https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600","Khỏe mạnh · tiêm phòng đầy đủ","Đang nghỉ",false),
            new Domain.Pet("Bông","Mèo","Mèo Ba Tư","Dịu dàng, thích được vuốt ve","https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=600","Khỏe mạnh · tiêm phòng đầy đủ","Sẵn sàng",true),
            new Domain.Pet("Đậu","Chó","Corgi","Vui vẻ, rất mê chơi bóng","https://images.unsplash.com/photo-1552053831-71594a27632d?w=600","Khỏe mạnh · tiêm phòng đầy đủ","Sẵn sàng",false),
            new Domain.Pet("Mây","Mèo","Mèo tam thể","Hiền lành, hơi nhút nhát","https://images.unsplash.com/photo-1495360010541-f48722b34f7d?w=600","Đang theo dõi sức khỏe","Đang nghỉ",true)));
        if(menu.count()==0)menu.saveAll(Arrays.asList(
            new Domain.MenuItem("Latte Paw","Cà phê","Espresso đậm đà cùng lớp sữa mịn.",59000,"☕"),new Domain.MenuItem("Cacao Marshmallow","Đồ uống","Cacao nguyên chất, kem marshmallow.",65000,"🍫"),new Domain.MenuItem("Trà đào cam sả","Trà trái cây","Trà thanh mát với đào và cam tươi.",55000,"🍑"),new Domain.MenuItem("Bánh cá mèo","Bánh ngọt","Bánh nướng nhân kem trứng.",39000,"🐟"),new Domain.MenuItem("Paw Brunch","Đồ ăn","Bánh mì bơ, trứng và salad.",89000,"🥪")));
    };}
}
