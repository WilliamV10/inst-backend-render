package sv.gob.bcr.onec.inst.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "codigo_acceso")
public class CodigoAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo_acceso")
    private Integer idCodigoAcceso;

    @Column(nullable = false, length = 6, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Boolean activo;
}
