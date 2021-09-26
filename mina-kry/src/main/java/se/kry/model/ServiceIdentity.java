package se.kry.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.kry.enumeration.Status;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_identity")
public class ServiceIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @Column(name = "url")
    String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.OK;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public ServiceIdentity(String url) {
        this.url = url;
    }

}
