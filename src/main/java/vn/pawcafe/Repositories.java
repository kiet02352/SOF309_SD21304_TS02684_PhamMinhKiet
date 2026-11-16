package vn.pawcafe;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

final class Repositories { private Repositories() {} }
interface PetRepository extends JpaRepository<Domain.Pet,Long> { List<Domain.Pet> findByAvailableForAdoptionTrue(); }
interface MenuRepository extends JpaRepository<Domain.MenuItem,Long> { List<Domain.MenuItem> findByAvailableTrue(); }
interface BookingRepository extends JpaRepository<Domain.Booking,Long> { List<Domain.Booking> findByVisitDateAndSlotAndStatusNot(LocalDate date,String slot,String status); }
interface AdoptionRepository extends JpaRepository<Domain.Adoption,Long> {}
