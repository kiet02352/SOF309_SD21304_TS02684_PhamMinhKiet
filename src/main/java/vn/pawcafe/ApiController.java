package vn.pawcafe;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class ApiController {
    private final PetRepository pets;
    private final MenuRepository menu;
    private final BookingRepository bookings;
    private final AdoptionRepository adoptions;
    public ApiController(PetRepository pets,MenuRepository menu,BookingRepository bookings,AdoptionRepository adoptions){this.pets=pets;this.menu=menu;this.bookings=bookings;this.adoptions=adoptions;}

    @GetMapping("/pets") public List<Domain.Pet> pets(){return pets.findAll();}
    @GetMapping("/pets/adoptable") public List<Domain.Pet> adoptable(){return pets.findByAvailableForAdoptionTrue();}
    @PostMapping("/pets") public Domain.Pet savePet(@RequestBody Domain.Pet p){return pets.save(p);}
    @PutMapping("/pets/{id}") public Domain.Pet updatePet(@PathVariable Long id,@RequestBody Domain.Pet p){p.id=id;return pets.save(p);}
    @DeleteMapping("/pets/{id}") public void deletePet(@PathVariable Long id){pets.deleteById(id);}

    @GetMapping("/menu") public List<Domain.MenuItem> menu(){return menu.findByAvailableTrue();}
    @GetMapping("/menu/all") public List<Domain.MenuItem> allMenu(){return menu.findAll();}
    @PostMapping("/menu") public Domain.MenuItem saveMenu(@RequestBody Domain.MenuItem item){return menu.save(item);}
    @PutMapping("/menu/{id}") public Domain.MenuItem updateMenu(@PathVariable Long id,@RequestBody Domain.MenuItem item){item.id=id;return menu.save(item);}
    @DeleteMapping("/menu/{id}") public void deleteMenu(@PathVariable Long id){menu.deleteById(id);}

    @GetMapping("/slots") public List<Map<String,Object>> slots(@RequestParam String date){
        LocalDate day=LocalDate.parse(date); List<Map<String,Object>> result=new ArrayList<>();
        for(String slot:Arrays.asList("09:00 - 11:00","11:30 - 13:30","14:00 - 16:00","16:30 - 18:30","19:00 - 21:00")){
            int reserved=bookings.findByVisitDateAndSlotAndStatusNot(day,slot,"CANCELLED").stream().mapToInt(b->b.guests).sum();
            Map<String,Object> row=new LinkedHashMap<>();row.put("slot",slot);row.put("capacity",20);row.put("reserved",reserved);row.put("available",Math.max(0,20-reserved));result.add(row);
        } return result;
    }
    @PostMapping("/bookings") @Transactional public synchronized Domain.Booking createBooking(@RequestBody Domain.Booking b){
        if(b.visitDate==null||b.visitDate.isBefore(LocalDate.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ngày đặt không hợp lệ");
        if(b.guests<1||b.guests>8)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Số khách từ 1 đến 8");
        if(!Arrays.asList("09:00 - 11:00","11:30 - 13:30","14:00 - 16:00","16:30 - 18:30","19:00 - 21:00").contains(b.slot))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Khung giờ không hợp lệ");
        int reserved=bookings.findByVisitDateAndSlotAndStatusNot(b.visitDate,b.slot,"CANCELLED").stream().mapToInt(x->x.guests).sum();
        if(reserved+b.guests>20)throw new ResponseStatusException(HttpStatus.CONFLICT,"Khung giờ đã hết chỗ");
        b.status="CONFIRMED";b.total=b.guests*79000L;return bookings.save(b);
    }
    @GetMapping("/bookings") public List<Domain.Booking> bookings(){return bookings.findAll();}
    @PatchMapping("/bookings/{id}/cancel") public Domain.Booking cancel(@PathVariable Long id){Domain.Booking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(b.status==null||"CANCELLED".equals(b.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Đặt chỗ đã được hủy");try{LocalTime start=LocalTime.parse(b.slot.substring(0,5));LocalDateTime visit=LocalDateTime.of(b.visitDate,start);if(visit.isAfter(LocalDateTime.now().plusHours(2)))b.status="CANCELLED";else throw new ResponseStatusException(HttpStatus.CONFLICT,"Chỉ hủy trước giờ đặt ít nhất 2 giờ");}catch(StringIndexOutOfBoundsException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Khung giờ không hợp lệ");}return bookings.save(b);}
    @PatchMapping("/bookings/{id}/checkin") public Domain.Booking checkin(@PathVariable Long id){Domain.Booking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));b.status="CHECKED_IN";return bookings.save(b);}
    @PatchMapping("/bookings/{id}/checkout") public Domain.Booking checkout(@PathVariable Long id){Domain.Booking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));b.status="COMPLETED";return bookings.save(b);}

    @GetMapping("/adoptions") public List<Domain.Adoption> adoptions(){return adoptions.findAll();}
    @PostMapping("/adoptions") public Domain.Adoption createAdoption(@RequestBody Domain.Adoption a){if(!pets.existsById(a.petId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy thú cưng");a.status="PENDING";return adoptions.save(a);}
    @PatchMapping("/adoptions/{id}/status") public Domain.Adoption adoptionStatus(@PathVariable Long id,@RequestParam String value){Domain.Adoption a=adoptions.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!Arrays.asList("APPROVED","REJECTED","CONTACTED").contains(value))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);a.status=value;if("APPROVED".equals(value)){Domain.Pet p=pets.findById(a.petId).orElse(null);if(p!=null){p.availableForAdoption=false;p.status="Đã tìm được nhà mới";pets.save(p);}}return adoptions.save(a);}
    @GetMapping("/dashboard") public Map<String,Object> dashboard(){Map<String,Object> r=new LinkedHashMap<>();r.put("bookings",bookings.count());r.put("pets",pets.count());r.put("adoptionsPending",adoptions.findAll().stream().filter(a->"PENDING".equals(a.status)).count());r.put("revenue",bookings.findAll().stream().filter(b->!"CANCELLED".equals(b.status)).mapToLong(b->b.total).sum());return r;}
}
