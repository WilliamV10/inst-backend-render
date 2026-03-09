package sv.gob.bcr.onec.inst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;

import java.util.Optional;

public interface CodigoAccesoRepository extends JpaRepository<CodigoAcceso, Integer> {

    boolean existsByCodigo(String codigo);

    Optional<CodigoAcceso> findByCodigo(String codigo);

    @Query(value = "SELECT bd_origen3.generar_codigo_acceso()", nativeQuery = true)
    String generarCodigo();
}
