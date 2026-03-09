package sv.gob.bcr.onec.inst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;

import java.util.Optional;

public interface CodigoAccesoRepository extends JpaRepository<CodigoAcceso, Integer> {

    boolean existsByCodigo(String codigo);

    Optional<CodigoAcceso> findByCodigo(String codigo);
}
