package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, length = 50)
    private String nom;
    
    @Column(length = 50)
    private String prenom;
    
    @Column(nullable = false, length = 20)
    private String telephone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 255)
    private String photo;  // AJOUTER CETTE LIGNE
    
    public String toString() {
        return nom + " " + prenom;
    }
}