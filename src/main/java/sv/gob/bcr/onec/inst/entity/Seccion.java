package sv.gob.bcr.onec.inst.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
    name = "seccion",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_seccion_codigo_formulario",
        columnNames = {"codigo", "id_formulario"}
    )
)
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seccion")
    private Integer idSeccion;

    @Column(nullable = false, length = 50)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_formulario", nullable = false)
    private Formulario formulario;

    @Column(nullable = false, length = 255)
    private String nombre;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode metadata;

    @Column(name = "en_edicion", nullable = false)
    private Boolean enEdicion;
}
