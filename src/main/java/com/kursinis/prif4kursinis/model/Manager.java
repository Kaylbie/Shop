package com.kursinis.prif4kursinis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manager extends User {

    private String employeeId;
    private LocalDate employmentDate;
    private boolean isAdmin;
    @ManyToMany
    private List<Warehouse> worksAtWarehouse;

    @OneToMany(mappedBy = "responsibleManager")
    private List<Cart> carts;

    public Manager(String login, String password, String name, String surname, String employeeId, LocalDate employmentDate, boolean isAdmin) {
        super(login, password, name, surname);
        this.employeeId = employeeId;
        this.employmentDate = employmentDate;
        this.isAdmin = isAdmin;
    }

    public Manager(String login, String password) {

    }

    @Override
    public String toString() {
        return "Free text, ka noriu";
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
